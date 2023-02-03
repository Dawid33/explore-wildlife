from flask import Blueprint

blueprint = Blueprint('urls', __name__, )


@blueprint.route("/")
def hello_world():
    return "<p>Hello, World!</p>"
