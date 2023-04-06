from flask import Blueprint, render_template, request, url_for, redirect, flash, session, send_from_directory

from . import db

bp = Blueprint('users', __name__, url_prefix="/api")

import os
from flask import Flask, request
from werkzeug.utils import secure_filename

UPLOAD_FOLDER = 'images/users'
ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'}
app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['SESSION_TYPE'] = 'filesystem'


def allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@bp.route("/users", methods=['GET'])
def get_users():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT user_id, display_name, email, password, profile_pic_id FROM app.users;")
    result = cur.fetchall()
    output = [{"user_id": x[0], "display_name": x[1], "email": x[2], "password": x[3], "profile_pic_id": x[4]} for x in
              result]
    conn.close()
    return output


@bp.route("/users/<uuid:user_id>", methods=['GET'])
def get_user(user_id):
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute(f"SELECT user_id, display_name, email, profile_pic_id, phone_number FROM app.users WHERE user_id = \'{user_id}\';")
    result = cur.fetchone()
    output = {"user_id": result[0], "display_name": result[1], "email": result[2], "profile_pic_id": result[3], "phone_number": result[4]}
    conn.close()
    return output


@bp.route("/users/<uuid:user_id>/password", methods=['PUT'])
def update_password(user_id):
    result = {
        "success": False,
    }
    try:
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

    if not password:
        result['error'] = 'Password is required.'

    if result.get('error') is None:
        try:
            cursor = db_conn.cursor()
            cursor.execute(f"UPDATE app.users "
                           f"SET password = \'{password}\'"
                           f"WHERE user_id = \'{user_id}\'")
            db_conn.commit()
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Database request failed, unable to register user'
        result['success'] = True

    db_conn.close()
    return result


@bp.route("/users/<uuid:user_id>", methods=['PUT'])
def update_profile(user_id):
    result = {
        "success": False,
    }
    try:
        email = request.form['email']
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
    elif not display_name:
        result['error'] = 'Display name is required.'
    elif possible_user is not None:
        result['error'] = f"User with email '{email}' is already registered."

    if result.get('error') is None:
        try:

            cursor.execute(f"UPDATE app.users "
                           f"SET display_name = \'{display_name}\', email = \'{email}\' "
                           f"WHERE user_id = \'{user_id}\'")
            db_conn.commit()
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Database request failed, unable to register user'
        result['success'] = True

    db_conn.close()
    return result


@bp.route("/users/<uuid:user_id>/update-profile-pic", methods=['POST'])
def update_profile_pic(user_id):
    result = {
        "success": False,
    }

    try:
        file = request.files['file']
        # file = request.data

        print('File: ', file)

        # if user does not select file, browser also
        # submit an empty part without filename
        if file.filename == '':
            print("No file!!")

        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            print('File name: ', filename)

            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            image = filename


    except Exception as e:
        print("No image")
        print(e)
        result['error'] = 'Internal Error: Failed while reading post request form data'
        return result

    try:
        db_conn = db.get_db()
    except Exception as e:
        print(e)
        result['error'] = 'Internal Error: Cannot connect to database'
        return result

    if result.get('error') is None:
        try:
            cursor = db_conn.cursor()
            cursor.execute(f"UPDATE app.users "
                           f"SET profile_pic_name = \'{image}\'"
                           f"WHERE user_id = \'{user_id}\'")
            db_conn.commit()
        except Exception as e:
            print(e)
            result['error'] = 'Internal Error: Database request failed, unable to register user'
        result['success'] = True

    db_conn.close()
    return result


@bp.route("/user/profile-pic", methods=['GET'])
def get_profile_pic_image():
    id = str(request.args.get('id'))
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT profile_pic_name FROM app.users WHERE user_id = %s;", [id])
    result = cur.fetchone()

    conn.close()

    if result[0]:
        return send_from_directory(app.config['UPLOAD_FOLDER'],
                                   result[0])
    else:
        return {
            "success": False,
        }
