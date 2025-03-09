## Run locally using docker-compose
```bash
docker-compose up -d #to start the services ALL
docker-compose up -d zookeeper #to start zookeeper or specific service
```

## Stop the service using docker-compose
```bash
docker-compose down #to stop the services ALL
docker-compose down zookeeper #to stop zookeeper or specific service
``` 

## Check in the contianer logs
```bash
docker-compose logs -f zookeeper #to check the logs of zookeeper

docker exec -it zookeeper bash #to get into the zookeeper container
```
