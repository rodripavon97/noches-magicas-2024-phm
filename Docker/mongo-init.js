db.createUser(
    {
        user: 'grupo4',
        pwd: 'admin',
        roles: [
            {
                role: 'root',
                db: 'admin'
            }
        ]
    }
)