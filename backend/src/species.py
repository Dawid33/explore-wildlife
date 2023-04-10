from flask import Blueprint, render_template, request, url_for, redirect, flash, session, send_file, make_response

from . import db

bp = Blueprint('animals', __name__, url_prefix="/api")


@bp.route("/species/<uuid:user_id>", methods=['GET'])
def query_image():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT image_id, name, owner, added_at, image_path FROM app.;")
    result = cur.fetchall()
    output = [{"image_id": x[0], "name": x[1], "owner": x[2], "added_at": x[3], "image_path": x[4]} for x in result]
    conn.close()
    return output
