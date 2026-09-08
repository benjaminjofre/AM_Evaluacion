package com.example.lavexpressspa


import java.util.Date

object EstadoSlot {
    const val LIBRE = "LIBRE"
    const val EN_USO = "EN_USO"
    const val EN_CICLO_FINAL = "EN_CICLO_FINAL"
    const val FUERA_DE_SERVICIO = "FUERA_DE_SERVICIO"
}


object TipoUsuario {
    const val REGULAR = "REGULAR"
    const val SUSCRIPTOR = "SUSCRIPTOR"
    const val EMPRESA = "EMPRESA"
}


open class Maquina(
    val codigo: String,
    val marcaModelo: String,
    val tipoUsuario: String,
    val fechaIngreso: Date = Date()
) {
    init {
        if (!validarFormatoCodigo(codigo)) {
            throw IllegalArgumentException("Error: El código '$codigo' no cumple el formato (dos letras, dos números, dos letras).")
        }

        if (tipoUsuario != TipoUsuario.REGULAR &&
            tipoUsuario != TipoUsuario.SUSCRIPTOR &&
            tipoUsuario != TipoUsuario.EMPRESA) {
            throw IllegalArgumentException("Error: Tipo de usuario no válido '$tipoUsuario'.")
        }
    }

    private fun validarFormatoCodigo(cod: String): Boolean {
        if (cod.length != 6) return false

        val c0 = cod[0]
        val c1 = cod[1]
        val c2 = cod[2]
        val c3 = cod[3]
        val c4 = cod[4]
        val c5 = cod[5]

        return c0.isLetter() && c1.isLetter() &&
                c2.isDigit() && c3.isDigit() &&
                c4.isLetter() && c5.isLetter()
    }

    open fun calcularMontoBase(minutosUso: Double): Double {
        return 0.0
    }
}

class Lavadora(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: String
) : Maquina(codigo, marcaModelo, tipoUsuario) {

    override fun calcularMontoBase(minutosUso: Double): Double {
        var minutosEfectivos = minutosUso
        if (tipoUsuario == TipoUsuario.SUSCRIPTOR) {
            minutosEfectivos *= 0.80
        }
        val horas = minutosEfectivos / 60.0
        return horas * 1200.0
    }
}

class Secadora(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: String
) : Maquina(codigo, marcaModelo, tipoUsuario) {

    override fun calcularMontoBase(minutosUso: Double): Double {
        if (minutosUso < 30) {
            return 0.0
        }
        val horas = minutosUso / 60.0
        return horas * 1000.0
    }
}

class LavasecaIndustrial(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: String,
    val conVapor: Boolean
) : Maquina(codigo, marcaModelo, tipoUsuario) {

    override fun calcularMontoBase(minutosUso: Double): Double {
        val horas = minutosUso / 60.0
        var tarifaHora = 2800.0
        if (conVapor) {
            tarifaHora *= 1.30
        }
        return horas * tarifaHora
    }
}

class Slot(
    val numero: Int,
    var estado: String = EstadoSlot.LIBRE,
    var maquina: Maquina? = null,
    var motivoEstado: String = ""
)

data class Ticket(
    val numeroTicket: Int,
    val maquina: Maquina,
    val minutosUso: Double,
    val montoFinal: Double
)