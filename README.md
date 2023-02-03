
# CS4084 Project

## Android App

## Backend Services

The backend consists of:
- A postgres database
- A python api to interface with the database. Written using flask and gunicorn.
- An nginx reverse proxy.

To run all of these services locally run

```bash
docker compose up
# Or run it with -d to run the services in "detached" mode i.e in the background.
# docker compose up -d 
```

## Docs
- [backend/README.md](backend)

### Nginx "Gateway"

The nginx "gateway" reverse proxy is only for development purposes. The real gateway
service will use a different configuration file for ssl (http**s**). This proxy acts as 
a middle man between the python api and the user. Its useful for the following reasons:
- Easier ssl support.
- Nginx is more battle tested and secure than a flimsy python service.
- Avoid CORS issues if a website is also hosted in the future.
- It is universally recommended to hide internal services through a proxy like nginx

Such a gateway service is technically not necessary for local development since
we are not hosting a website that gets loaded in the browser but having it lets 
us mimic the likely deployment configuration with no practical downside.

### Docker

Some more docker commands that might be useful during development

```bash
docker compose down # Bring down and remove running docker containers

# Combo to reset docker images / containers. Useful if docker does something strange 
# or breaks in some way. Also useful to force docker to pull new images for everything.
docker image prune -a # Remove all saved docker images
docker container prune # Remove all stopped containers
```

Useful commands for writing and debugging docker stuff

```bash
# Get names and ID's of running containers
docker ps 
# Or 'docker compose ps' for just the services in the docker-compose.yml file in your
# current directory. Useful if you have a lot of random containers running. 

# Run sh in a running container
docker exec -it <container-name-or-id> sh
```

### Postgres database
The postgres docker container automatically creates a new postgres database everytime
you launch it at `docker/postgres_database` if it doesn't already exist. If you accidentally
break your database beyond reason (it happens, `¯\_(ツ)_/¯`) then just delete that folder and 
restart the docker containers.

```commandline
docker compose down
docker compse up -d
```

When the database is first created it runs the `docker/init.sql` file to set everything up.
That file does not get executed when you restart the containers, only when creating the database
from scratch.
