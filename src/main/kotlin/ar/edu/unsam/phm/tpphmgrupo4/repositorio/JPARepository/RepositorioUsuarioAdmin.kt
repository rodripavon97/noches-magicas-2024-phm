package ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioAdmin
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RepositorioUsuarioAdmin : CrudRepository<UsuarioAdmin, Long> {
    fun findByUsernameAndPassword(username: String, password: String): Optional<UsuarioAdmin>
}