from flask import Blueprint, render_template, request, url_for, redirect, flash, session, send_from_directory

from . import db

bp = Blueprint('login', __name__, url_prefix="/api")

import os
from flask import Flask, request
from werkzeug.utils import secure_filename

UPLOAD_FOLDER = 'images/users'
ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'}
app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['SESSION_TYPE'] = 'filesystem'



@bp.route('/register', methods=['POST'])
def register():
    result = {
        "success": False,
    }
    try:
        email = request.form['email']
        password = request.form['password']
        display_name = request.form['display_name']
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed while reading post request form data'
        return result

    try:
        db_conn = db.get_db()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Cannot connect to database'
        return result

    try:
        cursor = db_conn.cursor()
        cursor.execute('SELECT email FROM app.users WHERE email = %s', (email,))
        possible_user = cursor.fetchone()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed to execute SQL query'
        db_conn.close()
        return result

    if not email:
        result['error'] = 'Email is required.'
    elif not password:
        result['error'] = 'Password is required.'
    elif not display_name:
        result['error'] = 'Display name is required.'
    elif possible_user is not None:
        result['error'] = f"User with email '{email}' is already registered."

    if result.get('error') is None:
        try:
            cursor.execute('INSERT INTO app.users (display_name, email, password) VALUES (%s, %s, %s)',
                           (display_name, email, password))

            cursor.execute("SELECT user_id FROM app.users WHERE email = %s", (email,))
            db_result = cursor.fetchone()

            result['user_id'] = db_result[0]

            db_conn.commit()
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Database request failed, unable to register user'
        result['success'] = True

    db_conn.close()
    return result


@bp.route('/login', methods=['POST'])
def login():
    result = {
        "success": False,
    }
    try:
        email = request.form['email']
        password = request.form['password']
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed while reading post request form data'
        return result

    try:
        db_conn = db.get_db()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Cannot connect to database'
        return result

    try:
        cursor = db_conn.cursor()
        cursor.execute("SELECT password, user_id FROM app.users WHERE email = %s", (email,))
        db_result = cursor.fetchone()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Failed to execute SQL query'
        db_conn.close()
        return result

    if db_result is None:
        result['error'] = 'Incorrect email or password'
    elif db_result[0] != password:
        result['error'] = 'Incorrect password'

    # db_conn.close()
    if result.get('error'):
        result['success'] = False
        # return result
    else:
        try:
            cursor = db_conn.cursor()
            cursor.execute("SELECT user_id FROM app.users WHERE email = %s", (email,))
            db_result = cursor.fetchone()

            result['user_id'] = db_result[0]
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Failed to execute SQL query'
            # db_conn.close()
            # return result

        result['success'] = True
        # return result
    db_conn.close()
    return result


