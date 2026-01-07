db.show.aggregate(
    { $match: {
        puntaje: { $gt : 4 }
    }},
    { $project: {
        nombreBanda: 1,
        nombreRecital: 1,
        puntaje: 1,
        nroComentarios: 1
    }}
)