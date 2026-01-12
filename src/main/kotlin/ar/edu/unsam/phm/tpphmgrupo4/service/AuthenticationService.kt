package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.DTO.AdminDTO
import ar.edu.unsam.phm.tpphmgrupo4.DTO.LoginDTO
import ar.edu.unsam.phm.tpphmgrupo4.DTO.UsuarioDataDTO
import ar.edu.unsam.phm.tpphmgrupo4.Exceptions.UnathorizedUser
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioAdmin
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable únicamente de la autenticación de usuarios.
 * Principio SRP: Una sola responsabilidad - autenticar usuarios
 */
@Service
class AuthenticationService {
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    
    @Autowired
    lateinit var repositorioUsuarioAdmin: RepositorioUsuarioAdmin

    /**
     * Autentica un usuario (común o admin) con sus credenciales.
     * @param user Credenciales de login
     * @return UsuarioDataDTO si es usuario común, AdminDTO si es admin
     * @throws UnathorizedUser si las credenciales son incorrectas
     */
    @Transactional(Transactional.TxType.NEVER)
    fun loginUsuario(user: LoginDTO): Any {
        val usuarioComun = repositorioUsuarioComun.findByUsernameAndPassword(user.username, user.password)
        val usuarioAdmin = repositorioUsuarioAdmin.findByUsernameAndPassword(user.username, user.password)

        return when {
            usuarioComun.isPresent -> UsuarioDataDTO.fromUserDataPostLogin(usuarioComun.get())
            usuarioAdmin.isPresent -> AdminDTO.fromAdminDTO(usuarioAdmin.get())
            else -> throw UnathorizedUser("Usuario y/o contraseña incorrecta")
        }
    }
}
