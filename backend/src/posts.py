# from PIL.Image import Image
import psycopg2.extras
from flask import Blueprint, send_from_directory, render_template, request, url_for, redirect, flash, session

from . import db

bp = Blueprint('posts', __name__, url_prefix="/api")

import os
from flask import Flask, request
from werkzeug.utils import secure_filename

UPLOAD_FOLDER = 'images/posts'
ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'}
app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['SESSION_TYPE'] = 'filesystem'


# Check if file has valid extension
def allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@bp.route('/create-post', methods=['POST'])
def create_post():
    # check if the post request has the file part

    # psycopg2.extras.register_uuid()
    result = {
        "success": False,
    }
    try:
        post_title = request.form['post_title']
        post_description = request.form['post_description']
        post_latitude = float(request.form['post_latitude'])
        post_longitude = float(request.form['post_longitude'])
        post_image_id = request.form['post_image_id']
        created_by = request.form['created_by']
        image = ""
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed while reading post request form data'
        return result

    try:
        db_conn = db.get_db()
        cursor = db_conn.cursor()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Cannot connect to database'
        return result

    if not post_title:
        result['error'] = 'Post title is required.'
    elif not post_latitude:
        result['error'] = 'Post latitude is required.'
    elif not post_longitude:
        result['error'] = 'Post longitude is required.'

    if result.get('error') is None:
        try:
            # Generate a new UUID
            cursor.execute("SELECT \"uuid_generate_v4\"()")
            post_id = cursor.fetchone()[0]

            # Create a post with that UUID
            cursor.execute(
                f'INSERT INTO app.posts (post_id, title, description, latitude, longitude, created_by, coordinates, location, has_images) VALUES '
                f'(%s, \'{post_title}\', \'{post_description}\', {post_latitude}, {post_longitude}, \'{created_by}\', ARRAY[{post_latitude}, {post_longitude}],'
                f'\'SRID=4326;POINT({post_longitude} {post_latitude})\', \'True\')', (post_id,))

            # Create a link between the new post and image id
            cursor.execute('INSERT INTO app.post_images (post_id, image_id) VALUES (%s, %s)', (post_id, post_image_id))

            print("New image added!!")

            db_conn.commit()
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Database request failed, unable to create post'

    result['success'] = True
    db_conn.close()
    return result


# For liking posts
@bp.route("/posts/<uuid:post>/toggle-like", methods=['Post'])
def like_post(post):
    psycopg2.extras.register_uuid()
    # post = uuid.uuid4();
    # print('Type: ', type(post))

    # post = (post);

    result = {
        "success": False,
    }

    # Checking if values exist
    try:
        user_id = request.form['user_id']
        # post_id = request.form['post_id']
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed while reading post request form data'
        return result

    # Checking if can connect to database
    try:
        conn = db.get_db()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Cannot connect to database'
        return result

    # Check to see if post exists.
    try:
        # 1+1
        cursor = conn.cursor()
        cursor.execute('SELECT post_id FROM app.posts WHERE post_id = %s', (post,))
        possible_post = cursor.fetchone()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed to execute SQL query'
        conn.close()
        return result

    # If no errors, add like to database
    if result.get('error') is None:
        try:
            cursor = conn.cursor()
            cursor.execute('SELECT post_id FROM app.posts_likes WHERE post_id = %s AND user_id = %s', (post, user_id))

            if cursor.fetchone() is None:
                print('hi')
                cursor.execute('INSERT INTO app.posts_likes (post_id, user_id) VALUES (%s, %s)',
                               (post, user_id))
            else:
                cursor.execute('DELETE FROM app.posts_likes WHERE post_id = %s AND user_id = %s', (post, user_id))

            conn.commit()
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Database request failed, unable to register user'
        result['success'] = True

    # conn = db.get_db()
    # cur = conn.cursor()
    # cur.execute("SELECT post_id, content, created_by, created_at FROM app.posts WHERE post_id = %s;", [id])
    # result = cur.fetchone()
    conn.close()

    return result


@bp.route("/posts", methods=['GET'])
def get_posts():
    conn = db.get_db()
    cur = conn.cursor()
    # cur.execute(
    #     "SELECT post_id, content, created_by, created_at, has_images FROM app.posts  where created_at in (SELECT max(created_at) FROM app.posts GROUP BY created_at) order by created_at desc limit 10")

    cur.execute(
        "SELECT post_id, title, content, created_by, created_at, has_images, ST_X(location::geometry), "
        "ST_Y(location::geometry) FROM app.posts  where created_at in (SELECT max(created_at) FROM app.posts GROUP BY "
        "created_at) order by created_at desc limit 10")

    result = cur.fetchall()
    posts = []
    for raw_post in result:
        post = {"post_id": raw_post[0], "title": raw_post[1], "content": raw_post[2], "created_by": raw_post[3], "created_at": raw_post[4], "longitude": raw_post[6], "latitude": raw_post[7]}
        if bool(raw_post[5]):
            cur.execute("SELECT post_id, image_id FROM app.post_images WHERE post_id = %s", [post["post_id"]])
            result = cur.fetchall()
            post["images"] = [x[1] for x in result]

        cur.execute("select COUNT(post_id) from posts_likes pl where pl.post_id = %s;", [post["post_id"]])
        result = cur.fetchone()
        post["likes"] = result[0]

        current_user = request.args.get('user_id')

        has_liked = False

        if current_user is not None and current_user != "null":

            print("User: " + current_user)

            cur.execute("select posts_likes_id from posts_likes pl where pl.post_id = %s and user_id = %s;", (post["post_id"], current_user))
            result = cur.fetchone()

            if result:
                has_liked = True

        post["has_liked"] = has_liked

        posts.append(post)

    conn.close()
    print(posts)
    return posts


@bp.route("/post", methods=['GET'])
def get_post():
    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute(
        "SELECT post_id, content, created_by, created_at, description, image_name FROM app.posts WHERE post_id = %s;",
        [id])
    result = cur.fetchone()
    output = dict(post_id=result[0], content=result[1], created_by=result[2], created_at=result[3],
                  description=result[4], image_name=result[5])

    if result[5]:
        return send_from_directory(app.config['UPLOAD_FOLDER'],
                                   result[5])

    conn.close()
    return output


@bp.route("/post/image", methods=['GET'])
def get_post_image():
    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT image_name FROM app.posts WHERE post_id = %s;", [id])
    result = cur.fetchone()

    conn.close()

    if result[0]:
        return send_from_directory(app.config['UPLOAD_FOLDER'],
                                   result[0])
    else:
        return {
            "success": False,
        }


@bp.route("/post/nearest", methods=['GET'])
def get_nearest_posts():
    earth_radius = 6371
    distance = 25
    number_of_results = 10

    # Checking if values exist
    try:
        number_of_results = request.form['num_results']
    except Exception as e:
        print('No amount specified, using default value')

    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    # cur.execute("SELECT image_name FROM app.posts WHERE post_id = %s;", [id])
    cur.execute("SELECT ST_X(location::geometry), ST_Y(location::geometry) FROM app.posts WHERE post_id = %s;", [id])

    result = cur.fetchall()

    if result:
        latitude = result[0][1]
        longitude = result[0][0]

        # print(result[0][0])

        print("Latitude: ", latitude)
        print("Longitude: ", longitude)

        # cur.execute(f"SELECT post_id, ({earth_radius} * acos(cos(radians({latitude})) * cos(radians(coordinates[0])) * cos(radians(coordinates[1]) - radians({longitude})) + sin(radians({latitude})) * sin(radians(radians(coordinates[0])))) AS distance FROM app.posts HAVING distance < {distance} ORDER BY distance LIMIT {number_of_results} OFFSET 0;")

        # cur.execute(
        #     f"SELECT post_id, ({earth_radius} * acos(cos(radians({latitude})) * cos(radians(coordinates[0])) * cos(radians(coordinates[1]) - radians({longitude})) + sin(radians({latitude})) * sin(radians(radians(coordinates[0])))) AS distance FROM app.posts HAVING distance < {distance} ORDER BY distance LIMIT {number_of_results} OFFSET 0;")

        cur.execute(
            f"SELECT * FROM app.posts order by location <-> \'SRID=4326;POINT({longitude} {latitude})\' limit {number_of_results};")
        result = cur.fetchall()

    conn.close()

    return result

    # if result[0]:
    #     return send_from_directory(app.config['UPLOAD_FOLDER'],
    #                                result[0])
    # else:
    #     return {
    #         "success": False,
    #     }

# @bp.route("/post", methods=['GET'])
# def get_post():
#     id = str(request.args.get('id'))
#     conn = db.get_db()
#     cur = conn.cursor()
#     cur.execute("SELECT post_id, content, created_by, created_at FROM app.posts WHERE post_id = %s;", [id])
#     result = cur.fetchone()
#     output = dict(post_id=result[0], content=result[1], created_by=result[2], created_at=result[3])
#     conn.close()
#     return output
