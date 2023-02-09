from flask import Blueprint, render_template, request, url_for, redirect, flash, session

from . import db

bp = Blueprint('login', __name__, url_prefix="/api")


@bp.route("/users", methods=['GET'])
def get_users():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT user_id, display_name, email, password FROM app.users;")
    result = cur.fetchall()
    output = [{"user_id": x[0], "display_name": x[1], "email": x[2], "password": x[3]} for x in result]
    conn.close()
    return output


@bp.route('/register', methods=['POST'])
def register():
    email = request.form['email']
    password = request.form['password']
    display_name = request.form['display_name']
    db_conn = db.get_db()
    cursor = db_conn.cursor()
    error = None

    cursor.execute('SELECT email FROM app.users WHERE email = %s', (email,))
    possible_user = cursor.fetchone()

    if not email:
        error = 'Email is required.'
    elif not password:
        error = 'Password is required.'
    elif not display_name:
        error = 'Display name is required.'
    elif possible_user is not None:
        error = f"User with email '{email}' is already registered."

    if error is None:
        cursor.execute('INSERT INTO app.users (display_name, email, password) VALUES (%s, %s, %s)',
            (display_name, email, password))
        db_conn.commit()
        db_conn.close()
        return {
            "success": True
        }
    else:
        db_conn.close()
        return error


@bp.route('/login', methods=['POST'])
def login():
    email = request.form['email']
    password = request.form['password']
    db_conn = db.get_db()
    cursor = db_conn.cursor()
    error = None
    cursor.execute(
        "SELECT password FROM app.users WHERE email = %s", (email,))
    db_result = cursor.fetchone()
    print(db_result)

    if db_result is None:
        error = 'Incorrect email.'
    elif db_result[0] != password:
        error = 'Incorrect password.'

    db_conn.close()
    if error is None:
        # session.clear()
        # session['user_id'] = user['id']
        return {
            "success": True,
            "session_token": "",
        }
    else:
        return {
            "success": False,
            "session_token": "",
        }
