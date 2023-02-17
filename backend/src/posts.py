from flask import Blueprint, render_template, request, url_for, redirect, flash, session

from . import db

bp = Blueprint('posts', __name__, url_prefix="/api")


@bp.route("/posts", methods=['GET'])
def get_posts():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT post_id, content, created_by, created_at, has_images FROM app.posts  where created_at in (SELECT max(created_at) FROM app.posts GROUP BY created_at) order by created_at desc limit 10")
    result = cur.fetchall()
    posts = []
    for x in result:
        post = {"post_id": x[0], "content": x[1], "created_by": x[2], "created_at": x[3]}
        if bool(x[4]):
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
