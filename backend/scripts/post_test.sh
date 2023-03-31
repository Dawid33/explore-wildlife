#!/bin/sh

path="api/login"
email="email=test@example.com"
password="password=test"


curl -X POST -F $email -F $password http://127.0.0.1:8080/$path
