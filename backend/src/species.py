from flask import Blueprint, render_template, request, url_for, redirect, flash, session, send_file, make_response

from . import db

bp = Blueprint('species', __name__, url_prefix="/api")

@bp.route("/species/<uuid:user_id>", methods=['GET'])
def get_user(user_id):
    conn = db.get_db()
    cur = conn.cursor()

    cur.execute(f"SELECT created_by, post_id FROM app.posts WHERE created_by = \'{user_id}\';")
    result = cur.fetchall()
    posts = [x[1] for x in result]

    animals = []
    for post in posts:
        cur.execute(f"SELECT post_id, species_id FROM app.posts_species WHERE post_id = \'{post}\';")
        result = cur.fetchone()
        if result:
            found_animal = False
            for x in animals:
                if x["id"] == result[1]:
                    x["count"] += 1
                    found_animal = True
                    break

            if not found_animal:
                animals.append({
                    "id": result[1],
                    "count": 1
                })

    for animal in animals:
        cur.execute(f"SELECT species_id, species_name FROM app.species WHERE species_id = \'{animal['id']}\';")
        result = cur.fetchone()
        animal["name"] = result[1]

    conn.close()
    return animals
