import psycopg2.extras
from flask import Flask, render_template
from backend.src import login, posts, images

app = Flask(__name__)
app.register_blueprint(login.bp)
app.register_blueprint(posts.bp)
app.register_blueprint(images.bp)

psycopg2.extras.register_uuid()

UPLOAD_FOLDER = 'images'
ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'}

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route("/")
def index():
    return render_template("index.html")


