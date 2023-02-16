from flask import Blueprint, render_template, request, url_for, redirect, flash, session

from . import db

bp = Blueprint('posts', __name__, url_prefix="/api")


@bp.route("/posts", methods=['GET'])
def get_posts():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT post_id, content, created_by, created_at FROM app.posts;")
    result = cur.fetchall()
    output = [{"post_id": x[0], "content": x[1], "created_by": x[2], "created_at": x[3]} for x in result]
    conn.close()
    return output
