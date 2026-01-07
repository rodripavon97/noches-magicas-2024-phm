import ar.edu.unsam.phm.tpphmgrupo4.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/*class EntradaTest {

    @Test
    fun `test calcular precio de la entrada`() {
        var teatroLunaPark = Teatro(
            true,
            2500,
            3000
        ).also {
            it.nombre = "Luna Park"
            it.ubicacion = UbicacionMapa("15.00.64", "19.00.24")
        }
        val entrada = Entrada(
            numeroEntrada = 1,
            ubicacion = Ubicacion.Pullman,
            fechaEntrada = LocalDate.now(),
            funcion = Funcion(
                fecha = LocalDate.now(),
                hora = LocalTime.now(),
                show = Show("Aventura",
                    "The Last Tour",
                    "prueba.jpg",
                    15000.00,
                    teatroLunaPark,
                    Estado.PrecioBase,
                    mutableListOf())
            )
        )
        val precioEsperado = 10030.0
        val precioCalculado = entrada.precio(entrada.ubicacion)
        assertEquals(precioEsperado, precioCalculado)
    }

    @Test
    fun `test calcular precio final de la entrada`() {
        var estadioMasMonumental = Estadio(
            450000.00,
            26000,
            27000,
            28000
        ).also {
            it.nombre = "Estadio Mas Monumental"
            it.ubicacion = UbicacionMapa("20.00.64", "40.00.24")
        }
        val entrada = Entrada(
            numeroEntrada = 1,
            ubicacion = Ubicacion.PlateaBaja,
            fechaEntrada = LocalDate.now(),
            funcion = Funcion(
                fecha = LocalDate.now(),
                hora = LocalTime.now(),
                show = Show("Gorillaz",
                    "The Gateway",
                    "prueba.jpg",
                    130000.00,
                    estadioMasMonumental,
                    Estado.Megashow,
                    mutableListOf())
            )
        )
        val precioEsperado = 19509
        val precioCalculado = entrada.precioFinal(entrada.ubicacion).roundToInt()
        assertEquals(precioEsperado, precioCalculado)
    }


    @Test
    fun `test calcular precio final barato de la entrada en Teatro`() {
        var teatroLunaPark = Teatro(
            true,
            2500,
            3000
        ).also {
            it.nombre = "Luna Park"
            it.ubicacion = UbicacionMapa("15.00.64", "19.00.24")
        }

        val funcion = Funcion(
            fecha = LocalDate.now(),
            hora = LocalTime.now(),
            show = Show("Aventura",
                "The Last Tour",
                "prueba.jpg",
                15000.00,
                teatroLunaPark,
                Estado.PrecioBase,
                mutableListOf())
        )

        val entrada = Entrada(
            numeroEntrada = 1,
            ubicacion = Ubicacion.PlateaAlta,
            fechaEntrada = LocalDate.now(),
            funcion = funcion
        )

        funcion.show.instalacion.categorias.addAll(listOf(Ubicacion.Campo, Ubicacion.Palco))

        val precioEsperado = 8024
        val precioCalculado = entrada.precioFinalBarato().roundToInt()
        assertEquals(precioEsperado, precioCalculado)
    }

    @Test
    fun `test calcular precio final caro de la entrada en Teatro`() {
        var teatroLunaPark = Teatro(
            true,
            2500,
            3000
        ).also {
            it.nombre = "Luna Park"
            it.ubicacion = UbicacionMapa("15.00.64", "19.00.24")
        }

        val funcion = Funcion(
            fecha = LocalDate.now(),
            hora = LocalTime.now(),
            show = Show("Aventura",
                "The Last Tour",
                "prueba.jpg",
                15000.00,
                teatroLunaPark,
                Estado.PrecioBase,
                mutableListOf())
        )

        val entrada = Entrada(
            numeroEntrada = 1,
            ubicacion = Ubicacion.PlateaBaja,
            fechaEntrada = LocalDate.now(),
            funcion = funcion
        )

        // Agregamos otras ubicaciones
        funcion.show.instalacion.categorias.addAll(listOf(Ubicacion.Campo, Ubicacion.Palco))

        val precioEsperado = 16024.0
        val precioCalculado = entrada.precioFinalCaro()
        assertEquals(precioEsperado, precioCalculado)
    }

    @Test
    fun `test calcular precio final caro de la entrada en Estadio`() {
        var estadioMasMonumental = Estadio(
            450000.00,
            26000,
            27000,
            28000
        ).also {
            it.nombre = "Estadio Mas Monumental"
            it.ubicacion = UbicacionMapa("20.00.64", "40.00.24")
        }

        val funcion = Funcion(
            fecha = LocalDate.now(),
            hora = LocalTime.now(),
            show = Show("Gorillaz",
                "The Gateway",
                "prueba.jpg",
                130000.00,
                estadioMasMonumental,
                Estado.Megashow,
                mutableListOf())
        )

        val entrada = Entrada(
            numeroEntrada = 1,
            ubicacion = Ubicacion.PlateaAlta,
            fechaEntrada = LocalDate.now(),
            funcion = funcion
        )


        funcion.show.instalacion.categorias.addAll(listOf(Ubicacion.Campo, Ubicacion.Palco))

        val precioEsperado = 26009
        val precioCalculado = entrada.precioFinalCaro().roundToInt()
        assertEquals(precioEsperado, precioCalculado)
    }


    fun `test calcular precio final barato de la entrada en Estadio`() {
        var estadioMasMonumental = Estadio(
            450000.00,
            26000,
            27000,
            28000
        ).also {
            it.nombre = "Estadio Mas Monumental"
            it.ubicacion = UbicacionMapa("20.00.64", "40.00.24")
        }

        val funcion = Funcion(
            fecha = LocalDate.now(),
            hora = LocalTime.now(),
            show = Show("Gorillaz",
                "The Gateway",
                "prueba.jpg",
                130000.00,
                estadioMasMonumental,
                Estado.Megashow,
                mutableListOf())
        )

        val entrada = Entrada(
            numeroEntrada = 1,
            ubicacion = Ubicacion.PlateaAlta,
            fechaEntrada = LocalDate.now(),
            funcion = funcion
        )


        funcion.show.instalacion.categorias.addAll(listOf(Ubicacion.Campo, Ubicacion.Palco))

        val precioEsperado = 13009
        val precioCalculado = entrada.precioFinalBarato().roundToInt()
        assertEquals(precioEsperado, precioCalculado)
    }


}*/