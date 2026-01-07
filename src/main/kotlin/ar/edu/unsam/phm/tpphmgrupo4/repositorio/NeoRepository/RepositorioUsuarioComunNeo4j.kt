package ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository

@Repository
interface RepositorioUsuarioComunNeo4j: Neo4jRepository<UsuarioNodo, String>  {
    fun findUsuarioNodoByUsername(username: String) : UsuarioNodo

    @Query("MATCH (u:Usuarios {username: \$user})-[:SON_AMIGUES*2]-(amigoDeAmigo:Usuarios) " +
            "WHERE amigoDeAmigo <> u AND NOT (u)-[:SON_AMIGUES]-(amigoDeAmigo)" +
            "MATCH (u)-[:COMPRO_ENTRADA]->(show:Show)<-[:COMPRO_ENTRADA]-(amigoDeAmigo)" +
            "RETURN DISTINCT amigoDeAmigo")
    fun amiguesSugeridos(user: String): List<UsuarioNodo>
}


