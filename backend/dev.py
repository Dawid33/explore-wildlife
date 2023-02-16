from flask import Flask, render_template
from src import login, posts

app = Flask(__name__)
app.register_blueprint(login.bp)
app.register_blueprint(posts.bp)


@app.route("/")
def index():
    return render_template("index.html")


