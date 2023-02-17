from flask import Flask, render_template
from backend.src import login, posts, images

app = Flask(__name__)
app.register_blueprint(login.bp)
app.register_blueprint(posts.bp)
app.register_blueprint(images.bp)


@app.route("/")
def index():
    return render_template("index.html")


