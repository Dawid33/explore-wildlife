# Backend

This python backend exposes API's to access and manipulate data that needs to be
stored online.

Docs for python libraries
- [flask](https://flask.palletsprojects.com/en/2.2.x/) - Easy server side
    rendering library.
- [gunicorn](https://gunicorn.org/#docs) - HTTP server for production
    deployment.
- [psycopg](https://www.psycopg.org/docs/index.html) - Postgresql database
    driver library.

## File Structure

- `src` - Folder containing project source code.
- `templates` - Folder for html templates. Used for quick api testing at
  https://localhost:8080 when running the development version locally. 
- `app.py` - File to run the backend in production using gunicorn.
- `dev.py` - File to run the backend locally for development with flask.
- `Dockerfile` - Instruction to build the docker image for the backend.
- `requirements.txt` - List of dependencies to be installed by pip.

## API endpoints:

- Testing / Debugging APIs
    - http://localhost:8080/api/users - return a list of all users
- Production APIs
    - http://localhost:8080/api/health_check - Check if the backend is accepting
        requests / working correctly. Used in docker compose to make sure
        backend is running before starting the gateway service.

## Running Locally

To run the backend you first need to install some of its depenencies. As usual,
python's package management leaves a lot to be desired as usual.

### Raw

Assuming you have python 3 installed, you can run the following commmands to
create a new venv and install the required dependencies.

```bash
# TODO
```

### Using Pycharm

If you're using pycharm then add a new python interpreter in the settings. Make
a Virtualenv Environment and set its path to be this folder. (NOTE: you'll
probably need to edit the default path as the default will point to the project
root.) You can then to go requirements.txt and right click on the dependencies
to install them. 

Unfortunately, installing `psycopg` requires some postgres development libraries
along with a compiler. Getting it on linux requires installing `lib-pq` and
`python3-dev`.

```bash
sudo apt install python3-dev libpq-dev
```

I haven't attempted installing on windows yet. If you install it on windows then
add any required steps here.

You can create a python run configuration for the production `app.py` but it
gives basically zero errors so its annoying for development. You can create a
shell run configuration with the command in the section below.

### Actually Running It

```bash
flask --app dev.py --debug run -p 8080
```

## Everything else

Both `app.py` and `dev.py` shouldn't require substantial changes throughout the
project. The business code is stored in `src` which contains all the API request
handlers.

