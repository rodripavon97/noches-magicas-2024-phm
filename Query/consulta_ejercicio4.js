db.show.aggregate([
    {
        $addFields: {
            "costoBase": {
                $add: ["$costoBanda", "$costoInstalacion"]
            }
        }
    },
    {
        $match: {
            "costoBase": {
                $gt: 1000,
                $lt: 5000
            }
        }
    },
    {
        $project: {
            "nombreBanda": 1,
            "nombreRecital": 1,
            "costoBase": 1
        }
    }
])