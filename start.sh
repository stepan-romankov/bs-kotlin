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
