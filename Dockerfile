FROM azul/zulu-openjdk-alpine:14 as builder
COPY . /build/
WORKDIR /build/
RUN ./gradlew -version --no-daemon
RUN ./gradlew test installDist  --no-daemon --info

FROM azul/zulu-openjdk-alpine:14
RUN apk update && apk add netcat-openbsd
EXPOSE 50051

COPY --from=builder /build/build/install/blocksize-assignment /app/
WORKDIR /app
CMD ./bin/blocksize-assignment




