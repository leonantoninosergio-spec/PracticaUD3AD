package com.example.DAO

import com.example.Class.ListaVideojuego
import com.example.Class.Videojuego
import com.example.ConexionBD
import java.sql.SQLException

object ListaVideojuegoDAO {

    fun insertar(listaVideojuego: ListaVideojuego): Boolean {
        val sqlCheckLista = "SELECT 1 FROM listas WHERE id_lista = ?"
        val sqlCheckJuego = "SELECT 1 FROM videojuegos WHERE id = ?"
        val sqlInsert = "INSERT INTO listas_videojuegos (id_lista, id_videojuego, `fecha_añadido`) VALUES (?, ?, ?)"

        val conn = ConexionBD.getConnection() ?: return false

        return try {
            conn.autoCommit = false

            val existeLista = conn.prepareStatement(sqlCheckLista).use { stmt ->
                stmt.setInt(1, listaVideojuego.idLista)
                stmt.executeQuery().use { rs -> rs.next() }
            }
            if (!existeLista) {
                throw SQLException("La lista con ID ${listaVideojuego.idLista} no existe.")
            }

            val existeJuego = conn.prepareStatement(sqlCheckJuego).use { stmt ->
                stmt.setInt(1, listaVideojuego.idVideojuego)
                stmt.executeQuery().use { rs -> rs.next() }
            }
            if (!existeJuego) {
                throw SQLException("El videojuego con ID ${listaVideojuego.idVideojuego} no existe.")
            }

            val insertado = conn.prepareStatement(sqlInsert).use { stmt ->
                stmt.setInt(1, listaVideojuego.idLista)
                stmt.setInt(2, listaVideojuego.idVideojuego)
                stmt.setString(3, listaVideojuego.fechaAnyadido)
                stmt.executeUpdate() > 0
            }

            conn.commit()
            insertado
        } catch (e: SQLException) {
            try {
                conn.rollback()
            } catch (rbEx: SQLException) {
                println("Error al hacer rollback: ${rbEx.message}")
            }
            println("Transacción cancelada (rollback): ${e.message}")
            false
        } finally {
            try {
                conn.autoCommit = true
                conn.close()
            } catch (closeEx: SQLException) {
                println("Error al cerrar conexión: ${closeEx.message}")
            }
        }
    }

    fun obtenerVideojuegosDeLista(idLista: Int): List<Videojuego> {
        val videojuegos = mutableListOf<Videojuego>()
        val sql = """
            SELECT v.id, v.titulo, v.genero, v.anio, v.nota
            FROM videojuegos v
            INNER JOIN listas_videojuegos lv ON v.id = lv.id_videojuego
            WHERE lv.id_lista = ?
        """.trimIndent()

        try {
            ConexionBD.getConnection()?.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, idLista)
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            videojuegos.add(
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
            }
        } catch (e: SQLException) {
            println("Error al obtener videojuegos de la lista: ${e.message}")
        }

        return videojuegos
    }
}