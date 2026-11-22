package com.example.DAO

import com.example.Class.Jugador
import com.example.ConexionBD
import java.sql.SQLException

object JugadorDAO {

    fun insertar(jugador: Jugador): Boolean {
        val sql = "INSERT INTO jugadores (nombre, nickname) VALUES (?, ?)"

        return try {
            ConexionBD.getConnection()?.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, jugador.nombre)
                    stmt.setString(2, jugador.nickname)
                    stmt.executeUpdate() > 0
                }
            } ?: false
        } catch (e: SQLException) {
            println("Error al insertar jugador: ${e.message}")
            false
        }
    }

    fun eliminar(idJugador: Int): Boolean {
        val sqlEliminarListas = "DELETE FROM listas WHERE id_jugador = ?"
        val sqlEliminarJugador = "DELETE FROM jugadores WHERE id_jugador = ?"

        return try {
            ConexionBD.getConnection()?.use { conn ->
                conn.autoCommit = false
                try {
                    conn.prepareStatement(sqlEliminarListas).use { stmtListas ->
                        stmtListas.setInt(1, idJugador)
                        stmtListas.executeUpdate()
                    }

                    val borrado = conn.prepareStatement(sqlEliminarJugador).use { stmtJugador ->
                        stmtJugador.setInt(1, idJugador)
                        stmtJugador.executeUpdate() > 0
                    }

                    conn.commit()
                    borrado
                } catch (e: SQLException) {
                    conn.rollback()
                    println("Error al eliminar jugador (rollback): ${e.message}")
                    false
                } finally {
                    conn.autoCommit = true
                }
            } ?: false
        } catch (e: SQLException) {
            println("Error de conexión al eliminar jugador: ${e.message}")
            false
        }
    }
}