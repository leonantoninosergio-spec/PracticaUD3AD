package com.example

import com.example.Class.Jugador
import com.example.Class.ListaVideojuego
import com.example.Class.Videojuego
import com.example.DAO.JugadorDAO
import com.example.DAO.ListaVideojuegoDAO
import com.example.DAO.VideojuegoDAO
import java.time.LocalDate

fun main() {
    if (!ConexionBD.testConnection()) {
        println("No se pudo conectar con la base de datos. Revisa XAMPP y ConexionBD.kt.")
        return
    }

    var opcion: Int?
    do {
        mostrarMenu()
        opcion = readlnOrNull()?.trim()?.toIntOrNull()

        when (opcion) {
            // --- VIDEOJUEGOS ---
            1 -> verTodosLosVideojuegos()
            2 -> buscarVideojuegoPorId()
            3 -> anadirVideojuego()
            4 -> modificarVideojuego()
            5 -> eliminarVideojuego()

            // --- JUGADORES ---
            6 -> crearJugador()
            7 -> eliminarJugador()

            // --- LISTAS ---
            8 -> crearLista()
            9 -> eliminarLista()
            10 -> anadirVideojuegoALista()
            11 -> verVideojuegosDeLista()

            0 -> println("Saliendo del catálogo...")
            else -> println("Opción no válida. Introduce un número del 0 al 11.")
        }
    } while (opcion != 0)
}

private fun mostrarMenu() {
    println(
        """
        
        ===== CÁTALOGO DE VIDEOJUEGOS =====

        --- VIDEOJUEGOS ---
        1. Ver todos los videojuegos
        2. Buscar videojuego por ID
        3. Añadir videojuego
        4. Modificar videojuego
        5. Eliminar videojuego

        --- JUGADORES ---
        6. Crear jugador
        7. Eliminar jugador

        --- LISTAS ---
        8. Crear lista
        9. Eliminar lista
        10. Añadir videojuego a una lista
        11. Ver videojuegos de una lista

        0. Salir
        ====================================

        Escoge una opción:
        """.trimIndent()
    )
}

// --- BLOQUE DE VIDEOJUEGOS ---

private fun verTodosLosVideojuegos() {
    val juegos = VideojuegoDAO.listarVideojuegos()
    if (juegos.isEmpty()) {
        println("No hay videojuegos registrados.")
    } else {
        println("\n--- Lista de videojuegos ---")
        juegos.forEach {
            println(" - [${it.id}] ${it.titulo} (${it.genero}), año: ${it.anio}, nota: ${it.nota}/10")
        }
    }
}

private fun buscarVideojuegoPorId() {
    val id = leerEntero("Introduce el ID del videojuego: ") ?: return
    val juego = VideojuegoDAO.consultarPorId(id)
    if (juego != null) {
        println("[${juego.id}] ${juego.titulo} (${juego.genero}), año: ${juego.anio}, nota: ${juego.nota}/10")
    } else {
        println("No se encontró ningún videojuego con el ID $id.")
    }
}

private fun anadirVideojuego() {
    print("Título: ")
    val titulo = readln().trim()
    print("Género: ")
    val genero = readln().trim()
    val anio = leerEntero("Año: ") ?: return
    val nota = leerDouble("Nota (0.0 - 10.0): ") ?: return

    if (titulo.isBlank() || genero.isBlank()) {
        println("El título y el género no pueden estar vacíos.")
        return
    }

    val insertado = VideojuegoDAO.insertarVideojuego(
        Videojuego(titulo = titulo, genero = genero, anio = anio, nota = nota)
    )
    if (insertado) {
        println("Videojuego añadido correctamente.")
    }
}

private fun modificarVideojuego() {
    val id = leerEntero("ID del videojuego a modificar: ") ?: return
    val actual = VideojuegoDAO.consultarPorId(id)
    if (actual == null) {
        println("No existe un videojuego con el ID $id.")
        return
    }

    print("Nuevo título (${actual.titulo}): ")
    val titulo = readln().trim().ifEmpty { actual.titulo }
    print("Nuevo género (${actual.genero}): ")
    val genero = readln().trim().ifEmpty { actual.genero }
    print("Nuevo año (${actual.anio}): ")
    val anioInput = readln().trim()
    val anio = if (anioInput.isEmpty()) actual.anio else anioInput.toIntOrNull() ?: run {
        println("Año no válido.")
        return
    }
    print("Nueva nota (${actual.nota}): ")
    val notaInput = readln().trim()
    val nota = if (notaInput.isEmpty()) actual.nota else notaInput.toDoubleOrNull() ?: run {
        println("Nota no válida.")
        return
    }

    val actualizado = VideojuegoDAO.actualizarVideojuegos(
        Videojuego(id = id, titulo = titulo, genero = genero, anio = anio, nota = nota)
    )
    if (actualizado) {
        println("Videojuego actualizado correctamente.")
    }
}

private fun eliminarVideojuego() {
    val id = leerEntero("ID del videojuego a eliminar: ") ?: return
    val eliminado = VideojuegoDAO.eliminarVideojuego(id)
    if (eliminado) {
        println("Videojuego eliminado correctamente.")
    } else {
        println("No se pudo eliminar o no existía el videojuego con ID $id.")
    }
}

// --- BLOQUE DE JUGADORES ---

private fun crearJugador() {
    print("Nombre del jugador: ")
    val nombre = readln().trim()
    print("Nickname: ")
    val nickname = readln().trim()

    if (nombre.isBlank() || nickname.isBlank()) {
        println("El nombre y el nickname no pueden estar vacíos.")
        return
    }

    val creado = JugadorDAO.insertar(Jugador(nombre = nombre, nickname = nickname))
    if (creado) {
        println("Jugador creado correctamente.")
    }
}

private fun eliminarJugador() {
    val idJugador = leerEntero("ID del jugador a eliminar: ") ?: return
    val eliminado = JugadorDAO.eliminar(idJugador)
    if (eliminado) {
        println("Jugador eliminado correctamente.")
    } else {
        println("No se pudo eliminar o no existía el jugador con ID $idJugador.")
    }
}

// --- BLOQUE DE LISTAS ---

private fun crearLista() {
    print("Nombre de la lista: ")
    val nombre = readln().trim()
    val idJugador = leerEntero("ID del jugador al que pertenece: ") ?: return

    if (nombre.isBlank()) {
        println("El nombre de la lista no puede estar vacío.")
        return
    }

    val creada = ListaDAO.insertar(nombre = nombre, idJugador = idJugador)
    if (creada) {
        println("Lista creada correctamente para el jugador $idJugador.")
    }
}

private fun eliminarLista() {
    val idLista = leerEntero("ID de la lista a eliminar: ") ?: return
    val eliminada = ListaDAO.eliminar(idLista)
    if (eliminada) {
        println("Lista eliminada correctamente.")
    } else {
        println("No se pudo eliminar o no existía la lista con ID $idLista.")
    }
}

private fun anadirVideojuegoALista() {
    val idLista = leerEntero("ID de la lista: ") ?: return
    val idVideojuego = leerEntero("ID del videojuego: ") ?: return
    print("Fecha añadido (vacío para usar hoy ${LocalDate.now()}): ")
    val fechaInput = readln().trim()
    val fecha = fechaInput.ifEmpty { LocalDate.now().toString() }

    val anadido = ListaVideojuegoDAO.insertar(
        ListaVideojuego(
            idLista = idLista,
            idVideojuego = idVideojuego,
            fechaAnyadido = fecha
        )
    )
    if (anadido) {
        println("Videojuego añadido a la lista correctamente.")
    }
}

private fun verVideojuegosDeLista() {
    val idLista = leerEntero("ID de la lista: ") ?: return
    val juegos = ListaVideojuegoDAO.obtenerVideojuegosDeLista(idLista)
    if (juegos.isEmpty()) {
        println("La lista $idLista no tiene videojuegos o no existe.")
    } else {
        println("\n--- Videojuegos de la lista $idLista ---")
        juegos.forEach {
            println(" - [${it.id}] ${it.titulo} (${it.genero}), año: ${it.anio}, nota: ${it.nota}/10")
        }
    }
}

// --- LEECTURAS DE ENTRADA ---

private fun leerEntero(mensaje: String): Int? {
    print(mensaje)
    val valor = readlnOrNull()?.trim()?.toIntOrNull()
    if (valor == null) {
        println("Debes introducir un número entero válido.")
    }
    return valor
}

private fun leerDouble(mensaje: String): Double? {
    print(mensaje)
    val valor = readlnOrNull()?.trim()?.toDoubleOrNull()
    if (valor == null) {
        println("Debes introducir un número decimal válido (usa punto, ej. 9.5).")
    }
    return valor
}