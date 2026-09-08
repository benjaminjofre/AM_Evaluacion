package com.example.lavexpressspa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lavexpressspa.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sistema = SistemaLavExpress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEjecutarPruebas.setOnClickListener {
            ejecutarPruebasEntorno()
        }
    }

    private fun escribirLog(mensaje: String) {
        val textoActual = binding.txtConsola.text.toString()
        binding.txtConsola.text = "$textoActual\n$mensaje"
    }

    private fun ejecutarPruebasEntorno() {
        binding.txtConsola.text = "Iniciando pruebas del sistema LavExpress...\n"

        // Corrutina para ejecutar operaciones asíncronas
        lifecycleScope.launch {

            try {
                escribirLog("Intentando registrar máquina inválida '123ABC'...")
                val invalida = Lavadora("123ABC", "Samsung WW90", TipoUsuario.SUSCRIPTOR)
                sistema.registrarEntrada(invalida)
            } catch (e: Exception) {
                escribirLog("[ERROR CAPTURADO]: ${e.message}")
            }

            try {
                escribirLog("\n--- REGISTRANDO ENTRADAS ---")

                val m1 = Lavadora("LV12CD", "Samsung WW90", TipoUsuario.SUSCRIPTOR)
                escribirLog(sistema.registrarEntrada(m1))

                val m2 = Lavadora("LV99ZA", "LG F4WV509", TipoUsuario.REGULAR)
                escribirLog(sistema.registrarEntrada(m2))

                val m3 = Secadora("SC22TO", "Bosch WTH85200", TipoUsuario.REGULAR)
                escribirLog(sistema.registrarEntrada(m3))

                val m4 = LavasecaIndustrial("LI44RG", "Miele PW6", TipoUsuario.EMPRESA, conVapor = true)
                escribirLog(sistema.registrarEntrada(m4))

                val m5 = LavasecaIndustrial("LI77RG", "Speed Queen SF7", TipoUsuario.REGULAR, conVapor = false)
                escribirLog(sistema.registrarEntrada(m5))

            } catch (e: Exception) {
                escribirLog("[ERROR ENTRADAS]: ${e.message}")
            }

            try {
                escribirLog("\n--- REGISTRANDO SALIDAS Y EMITIENDO TICKETS ---")

                val t1 = sistema.registrarSalida("LV12CD", 75.0)
                escribirLog("Ticket #${t1.numeroTicket} emitido. Total: $${String.format("%.0f", t1.montoFinal)}")

                val t2 = sistema.registrarSalida("LV99ZA", 180.0)
                escribirLog("Ticket #${t2.numeroTicket} emitido. Total: $${String.format("%.0f", t2.montoFinal)}")

                val t3 = sistema.registrarSalida("SC22TO", 25.0)
                escribirLog("Ticket #${t3.numeroTicket} emitido. Total: $${String.format("%.0f", t3.montoFinal)}")

                val t4 = sistema.registrarSalida("LI44RG", 120.0)
                escribirLog("Ticket #${t4.numeroTicket} emitido. Total: $${String.format("%.0f", t4.montoFinal)}")

                val t5 = sistema.registrarSalida("LI77RG", 45.0)
                escribirLog("Ticket #${t5.numeroTicket} emitido. Total: $${String.format("%.0f", t5.montoFinal)}")

            } catch (e: Exception) {
                escribirLog("[ERROR SALIDAS]: ${e.message}")
            }

            try {
                escribirLog("\nIntentando salida de máquina inexistente 'XX99XX'...")
                sistema.registrarSalida("XX99XX", 50.0)
            } catch (e: Exception) {
                escribirLog("[ERROR CAPTURADO]: ${e.message}")
            }

            escribirLog("\n" + sistema.generarReporteCierre())
        }
    }
}