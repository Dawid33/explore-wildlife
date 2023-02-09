import os

import psycopg2


def get_db():
    db_host = "localhost"
    if os.getenv("DB_HOST") is not None:
        db_host = os.environ["DB_HOST"]

    conn = psycopg2.connect(
        host=db_host,
        database="appdb",
        user="app",
        password="app")
    return conn
