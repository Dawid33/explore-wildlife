from flask import Blueprint, render_template, request, url_for, redirect, flash, session, send_file, make_response

from . import db

bp = Blueprint('images', __name__, url_prefix="/api")


@bp.route("/image", methods=['GET'])
def download_image():
    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    try:
        cur.execute("SELECT image_id, image_path FROM app.images WHERE image_id = %s;", [id])

        result = cur.fetchone()
        conn.close()

        if result is not None:
            return send_file(f"images/{result[1]}", mimetype='image/jpeg')
        else:
            return make_response("", 404)
    except:
        return make_response("", 500)


# @bp.route("/query_image", methods=['GET'])
def query_image():
    return "Hello"


def upload_image():
    request.args.get("id")
    return "Hello"

