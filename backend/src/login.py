from flask import Blueprint, render_template

from . import db

bp = Blueprint('login', __name__, url_prefix="/api")


@bp.route("/users", methods=['GET'])
def get_users():
    conn = db.get_db()
    cur = conn.cursor()
    cur.execute("SELECT * FROM app.users;")
    result = cur.fetchall()
    output = [{ "id": x[0], "display_name": x[1]} for x in result]
    conn.close()
    return output

# @bp.route('/register', methods=('GET', 'POST'))
# def register():
#     if request.method == 'POST':
#         username = request.form['username']
#         password = request.form['password']
#         db = get_db()
#         error = None
#
#         if not username:
#             error = 'Username is required.'
#         elif not password:
#             error = 'Password is required.'
#         elif db.execute(
#             'SELECT id FROM user WHERE username = ?', (username,)
#         ).fetchone() is not None:
#             error = 'User {} is already registered.'.format(username)
#
#         if error is None:
#             db.execute(
#                 'INSERT INTO user (username, password) VALUES (?, ?)',
#                 (username, generate_password_hash(password))
#             )
#             db.commit()
#             return redirect(url_for('auth.login'))
#
#         flash(error)
#
#     return render_template('auth/register.html')
#
# @bp.route('/login', methods=('GET', 'POST'))
# def login():
#     if request.method == 'POST':
#         username = request.form['username']
#         password = request.form['password']
#         db = get_db()
#         error = None
#         user = db.execute(
#             'SELECT * FROM user WHERE username = ?', (username,)
#         ).fetchone()
#
#         if user is None:
#             error = 'Incorrect username.'
#         elif not check_password_hash(user['password'], password):
#             error = 'Incorrect password.'
#
#         if error is None:
#             session.clear()
#             session['user_id'] = user['id']
#             return redirect(url_for('index'))
#
#         flash(error)
#
#     return render_template('auth/login.html')
