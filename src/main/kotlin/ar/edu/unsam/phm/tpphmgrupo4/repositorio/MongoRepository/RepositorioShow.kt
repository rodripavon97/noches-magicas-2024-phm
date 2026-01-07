    package ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository

    import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
    import org.springframework.data.mongodb.repository.MongoRepository
    import org.springframework.data.mongodb.repository.Query
    import java.time.LocalDate

    interface RepositorioShow: MongoRepository<Show, String>{

        @Query("{" +
                "'nombreBanda': {\$regex: ?1, \$options: 'i'}, " +
                "'nombreInstalacion': {\$regex: ?2, \$options: 'i'}, " +
                "'funciones': {\$elemMatch: {'fecha': {\$gte: ?3}}}" +
                "}")
        fun findFilteredShows(idUsuario: Long?, artista: String?, locacion: String?, fecha: LocalDate): List<Show>


        @Query("{" +
                "'nombreBanda': {\$regex: ?1, \$options: 'i'}, " +
                "'nombreInstalacion': {\$regex: ?2, \$options: 'i'}, " +
                "}")
        fun findAdminFilteredShows(idAdmin: Long, artista: String?, locacion: String?): List<Show>

        fun findDetailesById(id : String): Show

    }