package ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.Instalacion
import org.springframework.data.repository.CrudRepository

interface RepositorioInstalacion: CrudRepository<Instalacion, Long> {
}