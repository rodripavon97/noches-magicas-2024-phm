package ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.ShowNodo
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface RepositorioShowNeo4j: Neo4jRepository<ShowNodo,String> {
    fun findShowNodoByShowId(id: String): ShowNodo
}