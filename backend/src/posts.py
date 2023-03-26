from flask import Blueprint, render_template, request, url_for, redirect, flash, session

from . import db
import uuid
import psycopg2.extras

bp = Blueprint('posts', __name__, url_prefix="/api")


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
    cur.execute(
        "SELECT post_id, content, created_by, created_at, has_images FROM app.posts  where created_at in (SELECT max(created_at) FROM app.posts GROUP BY created_at) order by created_at desc limit 10")
    result = cur.fetchall()
    posts = []
    for raw_post in result:
        post = {"post_id": raw_post[0], "content": raw_post[1], "created_by": raw_post[2], "created_at": raw_post[3]}
        if bool(raw_post[4]):
            cur.execute("SELECT post_id, image_id FROM app.post_images WHERE post_id = %s", [post["post_id"]])
            result = cur.fetchall()
            post["images"] = [x[1] for x in result]
        posts.append(post)

    conn.close()
    print(posts)
    return posts


@bp.route("/post", methods=['GET'])
def get_post():
    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT post_id, content, created_by, created_at FROM app.posts WHERE post_id = %s;", [id])
    result = cur.fetchone()
    output = dict(post_id=result[0], content=result[1], created_by=result[2], created_at=result[3])
    conn.close()
    return output

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
