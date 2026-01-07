sh.enableSharding("noches_magicas");
sh.shardCollection("noches_magicas.show", {"_id": "hashed" },false)
sh.shardCollection("noches_magicas.log", {"_id": "hashed" },false)
sh.shardCollection("noches_magicas.funcion", {"_id": "hashed" },false)