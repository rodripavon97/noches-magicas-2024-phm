db.show.aggregate([
    {
        $match: {
            "tipoInstalacion": "ESTADIO"
        }
    },
    {
        $group: {
            _id: null,
            count: { $sum: 1 },
            shows: { $push: { nombreRecital: "$nombreRecital", nombreBanda: "$nombreBanda" } }
        }
    },
    {
        $project: {
            _id: 0,
            count: 1,
            shows: 1
        }
    }
])
