package com.example

import java.sql.SQLException

object ListaDAO {

    fun insertar(nombre: String, idJugador: Int): Boolean {
        val sqlComprobarJugador = "SELECT 1 FROM jugadores WHERE id_jugador = ?"
        val sqlInsertarLista = "INSERT INTO listas (nombre, id_jugador) VALUES (?, ?)"

        return try {
            ConexionBD.getConnection()?.use { conn ->
                val existeJugador = conn.prepareStatement(sqlComprobarJugador).use { stmtCheck ->
                    stmtCheck.setInt(1, idJugador)
                    stmtCheck.executeQuery().use { rs -> rs.next() }
                }

                if (!existeJugador) {
                    println("No existe ningún jugador con ID $idJugador. No se creará la lista.")
                    return false
                }

                conn.prepareStatement(sqlInsertarLista).use { stmtInsert ->
                    stmtInsert.setString(1, nombre)
                    stmtInsert.setInt(2, idJugador)
                    stmtInsert.executeUpdate() > 0
                }
            } ?: false
        } catch (e: SQLException) {
            println("Error al crear lista: ${e.message}")
            false
        }
    }

    fun eliminar(idLista: Int): Boolean {
        val sql = "DELETE FROM listas WHERE id_lista = ?"

        return try {
            ConexionBD.getConnection()?.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, idLista)
                    stmt.executeUpdate() > 0
                }
            } ?: false
        } catch (e: SQLException) {
            println("Error al eliminar lista: ${e.message}")
            false
        }
    }
}