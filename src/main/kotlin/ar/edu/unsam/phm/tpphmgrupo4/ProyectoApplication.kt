package ar.edu.unsam.phm.tpphmgrupo4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootApplication
@EnableMongoRepositories(basePackages = ["ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository"])
@EnableJpaRepositories (basePackages = ["ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository"])
@EnableRedisRepositories (basePackages = ["ar.edu.unsam.phm.tpphmgrupo4.repositorio.RedisRepository"])
@EnableNeo4jRepositories (basePackages = ["ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository"] )

class ProyectoNocheMagicas {
}
fun main(args: Array<String>) {
    runApplication<ProyectoNocheMagicas>(*args)
}