db.logs.aggregate([
    {
        $group: {
            _id: "$idShow",
            numeroDeClics: { $sum: 1 }
        }
    },
    {
        $sort: { numeroDeClics: -1 }
    },
    {
        $limit: 1
    },
    {
        $lookup: {
            from: "show",
            let: { idShowObj: { $toObjectId: "$_id" } },
            pipeline: [
                {
                    $match: {
                        $expr: { $eq: ["$_id", "$$idShowObj"] }
                    }
                },
                {
                    $project: {
                        nombreBanda: 1
                    }
                }
            ],
            as: "showData"
        }
    },
    {
        $unwind: "$showData"
    },
    {
        $project: {
            _id: 0,
            nombreBanda: "$showData.nombreBanda",
            numeroDeClics: 1
        }
    }
])
