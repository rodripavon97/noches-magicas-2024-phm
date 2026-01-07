db.show.aggregate([
    {
        $unwind: "$funciones"
    },
    {
        $match: {
            "funciones.estaSoldOut": true,
            "funciones.fecha": {
                $gt: new Date()
            }
        }
    },
    {
        $group: {
            "_id": "$_id",
            "funcionesSoldOut": {
                $push: "$funciones"
            },
            "nombreBanda": { $first: "$nombreBanda" },
            "nombreRecital": { $first: "$nombreRecital" }
        }
    }
])