# Dockerization of MDS<i>Writer</i>

This docker-compose file creates/runs:
   * a mysql database
   * adminer to administrate that database accessible via `http://localhost:$MYSQL_ADMINER_PORT`, e.g. `http://localhost:8888`
   * the mdswriter app, see https://github.com/AIPHES/mdswriter, accessible via `http://localhost:$APP_PORT/mdswriter`, e.g. `http://localhost:8080/mdswriter`

## Prerequisites:
   * [docker](https://www.docker.com/get-docker)
   * [docker-compose](https://docs.docker.com/compose/)

## Required files:
  * [docker-compose.yml](docker-compose.yml)
  * [Dockerfile](Dockerfile) (to build and execute the msdwriter service)
  * [Dockerfile.mysql](Dockerfile.mysql) (mysql service)
  * [.env](.env) (environment file)

## Run
Adapt the .env file for your needs and start the service by executing
the following from the directory containing the mentioned files:
```bash
docker-compose up
```
   