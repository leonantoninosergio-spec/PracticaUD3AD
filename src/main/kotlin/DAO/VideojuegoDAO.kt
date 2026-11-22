package com.example.DAO

import com.example.Class.Videojuego
import com.example.ConexionBD
import java.sql.Connection
import java.sql.SQLException

object VideojuegoDAO {

    fun listarVideojuegos(): List<Videojuego> {
        val lista = mutableListOf<Videojuego>()
        val sql = "SELECT * FROM videojuegos"

        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    lista.add(
                        Videojuego(
                            id = rs.getInt("id"),
                            titulo = rs.getString("titulo"),
                            genero = rs.getString("genero"),
                            anio = rs.getInt("anio"),
                            nota = rs.getDouble("nota")
                        )
                    )
                }
            }
        }
        return lista
    }

    fun consultarPorId(id: Int): Videojuego? {
        var videojuego: Videojuego? = null
        val sql = "SELECT * FROM videojuegos WHERE id = ?"

        ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    videojuego = Videojuego(
                        id = rs.getInt("id"),
                        titulo = rs.getString("titulo"),
                        genero = rs.getString("genero"),
                        anio = rs.getInt("anio"),
                        nota = rs.getDouble("nota")
                    )
                }
            }
        }
        return videojuego
    }

    fun insertarVideojuego(videojuego: Videojuego): Boolean {
        val sql = "INSERT INTO videojuegos(titulo, genero, anio, nota) VALUES (?, ?, ?, ?)"
        return ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, videojuego.titulo)
                stmt.setString(2, videojuego.genero)
                stmt.setInt(3, videojuego.anio)
                stmt.setDouble(4, videojuego.nota)
                stmt.executeUpdate() > 0
            }
        } ?: false
    }

    fun actualizarVideojuegos(videojuego: Videojuego): Boolean {
        val id = videojuego.id ?: return false
        val sql = "UPDATE videojuegos SET titulo = ?, genero = ?, anio = ?, nota = ? WHERE id = ?"
        return ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, videojuego.titulo)
                stmt.setString(2, videojuego.genero)
                stmt.setInt(3, videojuego.anio)
                stmt.setDouble(4, videojuego.nota)
                stmt.setInt(5, id)
                stmt.executeUpdate() > 0
            }
        } ?: false
    }

    fun eliminarVideojuego(id: Int): Boolean {
        val sql = "DELETE FROM videojuegos WHERE id = ?"
        return ConexionBD.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate() > 0
            }
        } ?: false
    }
    fun anyadirALista(idLista: Int, idVideojuego: Int, fecha: String) {
        var conn: Connection? = null
        try {
            conn = ConexionBD.getConnection()
            conn?.autoCommit = false


            val existeVideojuego = conn?.prepareStatement("SELECT 1 FROM videojuegos WHERE id = ?")?.use { stmt ->
                stmt.setInt(1, idVideojuego)
                stmt.executeQuery().use { rs -> rs.next() }
            } ?: false

            if (!existeVideojuego) {
                throw SQLException("El videojuego con id=$idVideojuego no existe en la tabla videojuegos.")
            }

            conn?.prepareStatement(
                "INSERT INTO listas_videojuegos(id_lista, id_videojuego, fecha_añadido) VALUES (?, ?, ?)"
            )?.use { stmt ->
                stmt.setInt(1, idLista)
                stmt.setInt(2, idVideojuego)
                stmt.setString(3, fecha)
                stmt.executeUpdate()
            }

            conn?.commit()
            println("Videojuego $idVideojuego añadido a la lista $idLista con éxito.")
        } catch (e: SQLException) {
            println("Error en la transacción, se revierten los cambios: ${e.message}")
            try {
                conn?.rollback()
                println("Transacción revertida.")
            } catch (rollbackEx: SQLException) {
                println("Error al hacer rollback: ${rollbackEx.message}")
            }
        } finally {
            try {
                conn?.autoCommit = true
                conn?.close()
            } catch (closeEx: SQLException) {
                println("Error al cerrar la conexión: ${closeEx.message}")
            }
        }
    }
}