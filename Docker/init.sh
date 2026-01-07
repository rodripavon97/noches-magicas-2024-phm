#!/bin/sh

# Ejecutar los scripts de inicialización en los servidores configurados
docker compose exec configsvr01 sh -c "mongosh < /scripts/init-configserver.js"
docker compose exec shard01-a sh -c "mongosh < /scripts/init-shard01.js"
docker compose exec shard02-a sh -c "mongosh < /scripts/init-shard02.js"
sleep 20

docker compose exec router01 sh -c "mongosh < /scripts/init-router.js"
sleep 20

docker compose exec router01 sh -c "mongosh < /scripts/config_router.js"