package ar.edu.unsam.phm.tpphmgrupo4.Bootstrap

import ar.edu.unsam.phm.tpphmgrupo4.ProyectoNocheMagicas
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

class ServletInitializer : SpringBootServletInitializer() {
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(ProyectoNocheMagicas::class.java)
    }
}