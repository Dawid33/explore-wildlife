
# CS4084 Project

## Android App

The app is an android studio project. To run just load up that folder in android
studio. 

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

## File Structure

- `android-app` - The android studio java app.
- `backend` - Python backend service.
- `docker` - Files for making docker stuff work. The files here could be put in the project root but having a folder for them makes it a little neater. 
- `docker-compose.yml` - File for running docker compose commands
  ([spec](https://docs.docker.com/compose/compose-file/),
  [docs](https://docs.docker.com/compose/))
- `.env` - Environment variables that can be used in `docker-compose.yml`
  ([stackoverflow explanation](https://stackoverflow.com/questions/29377853/how-to-use-environment-variables-in-docker-compose))
- `.gitignore` - Files to ignore when commiting to git.

## Docs

Each important folder has its own README.md for its documentation.

- [android-app/README.md](android-app)
- [backend/README.md](backend)
- [docker/README.md](docker)
