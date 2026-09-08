package com.example.lavexpressspa

import kotlinx.coroutines.delay

class SistemaLavExpress {

    val slots = MutableList(10) { id -> Slot(numero = id + 1) } // Capaciddad 10 slots
    val historialTickets = mutableListOf<Ticket>()
    private var contadorTickets = 1000

    suspend fun registrarEntrada(maquina: Maquina): String {

        val slotDisponible = slots.find { it.estado == EstadoSlot.LIBRE }
            ?: throw IllegalStateException("Error: Sistema sin capacidad. No hay slots disponibles.")

        slotDisponible.estado = EstadoSlot.EN_CICLO_FINAL
        slotDisponible.motivoEstado = "Registrando entrada en sensor..."

        delay(3000)

        slotDisponible.maquina = maquina
        slotDisponible.estado = EstadoSlot.EN_USO
        slotDisponible.motivoEstado = ""

        return "Entrada confirmada: Máquina ${maquina.codigo} asignada al Slot #${slotDisponible.numero}"
    }

    suspend fun registrarSalida(codigoMaquina: String, minutosUso: Double): Ticket {
        val slot = slots.find { it.maquina?.codigo == codigoMaquina && it.estado == EstadoSlot.EN_USO }
            ?: throw IllegalArgumentException("Error: La máquina $codigoMaquina no está registrada en el sistema.")

        slot.estado = EstadoSlot.EN_CICLO_FINAL
        slot.motivoEstado = "Calculando tarifa..."

        delay(6500)

        val maquina = slot.maquina!!

        val montoBase = maquina.calcularMontoBase(minutosUso)
        val montoConIVA = montoBase * 1.19

        var montoFinal = montoConIVA
        if (maquina.tipoUsuario == TipoUsuario.EMPRESA) {
            montoFinal *= 0.50
        }

        if (montoFinal <= 0 && !(maquina is Secadora && minutosUso < 30)) {
            slot.estado = EstadoSlot.EN_USO
            throw ArithmeticException("Error: Cálculo de tarifa inválido ($montoFinal).")
        }

        contadorTickets++
        val ticket = Ticket(contadorTickets, maquina, minutosUso, montoFinal)
        historialTickets.add(ticket)


        slot.maquina = null
        slot.estado = EstadoSlot.LIBRE
        slot.motivoEstado = ""

        return ticket
    }

    fun contarSlotsDisponibles(): Int {
        return slots.count { it.estado == EstadoSlot.LIBRE }
    }

    fun obtenerTotalRecaudado(): Double {
        return historialTickets.sumOf { it.montoFinal }
    }

    fun obtenerIngresoPromedio(): Double {
        if (historialTickets.isEmpty()) return 0.0
        return obtenerTotalRecaudado() / historialTickets.size
    }


    fun generarReporteCierre(): String {
        val sb = StringBuilder()
        sb.append("=====================================\n")
        sb.append("     REPORTE DE CIERRE DE TURNO     \n")
        sb.append("=====================================\n")

        for (t in historialTickets) {
            val tipoStr = t.maquina.javaClass.simpleName
            val pagoStr = String.format("%.0f", t.montoFinal)
            sb.append("Ticket #${t.numeroTicket} | Tipo: $tipoStr | Código: ${t.maquina.codigo} | Uso: ${t.minutosUso} min | Pago: $$pagoStr\n")
        }

        sb.append("-------------------------------------\n")
        sb.append("Total Recaudado: $${String.format("%.0f", obtenerTotalRecaudado())}\n")
        sb.append("Atenciones Realizadas: ${historialTickets.size}\n")
        sb.append("Ingreso Promedio: $${String.format("%.0f", obtenerIngresoPromedio())}\n")
        sb.append("Slots Libres al Cierre: ${contarSlotsDisponibles()}\n")

        return sb.toString()
    }
}