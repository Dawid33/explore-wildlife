from flask import Blueprint, render_template, request, url_for, redirect, flash, session, send_file, make_response

from . import db

bp = Blueprint('images', __name__, url_prefix="/api")


@bp.route("/images", methods=['GET'])
def query_image():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT image_id, name, owner, added_at, image_path FROM app.images;")
    result = cur.fetchall()
    output = [{"image_id": x[0], "name": x[1], "owner": x[2], "added_at": x[3], "image_path": x[4]} for x in result]
    conn.close()
    return output


@bp.route("/image", methods=['GET'])
def download_image():
    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    try:
        cur.execute("SELECT image_id, image_path FROM app.images WHERE image_id = %s;", [id])

        result = cur.fetchone()
        conn.close()
        print(result[1])
        if result is not None:
            return send_file(result[1], mimetype='image/png')
        else:
            return make_response("", 404)
    except:
        return make_response("", 500)


@bp.route("/upload_image", methods=['POST'])
def upload_image():
    response = {"success": False}

    if 'image' not in request.files:
        response['error'] = "Missing image in http request"
        return response
    image = request.files['image']
    conn = db.get_db()
    cur = conn.cursor()
    try:
        cur.execute("SELECT \"uuid_generate_v4\"()")
        image_id = cur.fetchone()[0]

        image_path = f"images/{image_id}.png"

        # Make every image exist under "default" user for now until we have login working properly.
        cur.execute("SELECT user_id FROM app.users WHERE display_name = 'default';")
        default_user_uuid = cur.fetchone()[0]
        cur.execute(
            'INSERT INTO app.images (image_id, name, owner, image_path) VALUES (%s, %s, %s, %s);',
            (image_id, str(image.filename), default_user_uuid, str(image_path)))

        cur.execute("SELECT name FROM app.images WHERE image_id = %s;", [image_id])
        name = cur.fetchone()[0]
        conn.commit()

        image = request.files['image']
        image.save(image_path)
    except Exception as e:
        response['error'] = "Internal Error: DB "
        print(e)
        return response

    conn.close()

    response["success"] = True
    return response

def query_image():
    return "Hello"


def upload_image():
    request.args.get("id")
    return "Hello"

