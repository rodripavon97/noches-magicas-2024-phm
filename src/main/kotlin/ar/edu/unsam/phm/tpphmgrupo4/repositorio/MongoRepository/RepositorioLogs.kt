package ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository

import ar.edu.unsam.phm.tpphmgrupo4.DTO.LogsDTO
import org.springframework.data.mongodb.repository.MongoRepository

interface RepositorioLogs: MongoRepository<LogsDTO,String> {

}