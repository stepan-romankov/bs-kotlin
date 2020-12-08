## Feel free to use docker-compose to start both service and PostgreSQL database
```shell
docker-compose up
```

## Alternatively start a docker container and provide PostgreSQL database of version >= 9.5
```shell
docker build -t blocksize .

docker run --rm\
   -p 50051:50051\
   -e POSTGRES_HOST=host.docker.internal\
   -e POSTGRES_PORT=5432\
   -e POSTGRES_USER=postgres\
   -e POSTGRES_PASSWORD=test\
   -e POSTGRES_DB=blocksize\
   -e SECRET=bl0cksrze__5623_sdkjWrew\
   blocksize

```