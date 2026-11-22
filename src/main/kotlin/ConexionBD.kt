package com.example

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object ConexionBD {
    private const val URL = "jdbc:mysql://localhost:3306/videojuegos?useSSL=false&serverTimezone=UTC"
    private const val USER = "root"
    private const val PASSWORD = ""

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLException) {
            println("Error al conectar con la base de datos: ${e.message}")
            null
        }
    }

    fun testConnection(): Boolean {
        return getConnection()?.use {
            println("Conexión establecida con éxito a $URL")
            true
        } ?: false
    }

    fun closeConnection(conn: Connection?) {
        try {
            conn?.close()
            println("Conexión cerrada correctamente.")
        } catch (e: SQLException) {
            println("Error al cerrar la conexión: ${e.message}")
        }
    }
}