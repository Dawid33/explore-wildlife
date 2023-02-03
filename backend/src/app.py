from flask import Flask, request
import multiprocessing
from test import blueprint as test_blueprint
import gunicorn.app.base

app = Flask(__name__)
app.register_blueprint(test_blueprint)
port = 8080


class StandaloneApplication(gunicorn.app.base.BaseApplication):
    def __init__(self, app, options=None):
        self.options = options or {}
        self.application = app
        super().__init__()

    def load_config(self):
        config = {key: value for key, value in self.options.items()
                  if key in self.cfg.settings and value is not None}
        for key, value in config.items():
            self.cfg.set(key.lower(), value)

    def load(self):
        return self.application


if __name__ == '__main__':
    options = {
        'bind': '%s:%s' % ('127.0.0.1', '8080'),
        'workers': (multiprocessing.cpu_count() * 2) + 1,
    }
    StandaloneApplication(app, options).run()
