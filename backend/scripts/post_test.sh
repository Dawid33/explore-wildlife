#!/bin/sh

path_login="api/login"
path_register="api/register"

email="email=test@example.com"
password="password=test"

# Login
#curl -X POST -F $email -F $password http://127.0.0.1:8080/$path_login

email="cardi@b.com"
password="omg"
display_name="cardi_b"

curl -X POST -F "email=$email" -F "password=$password" -F "display_name=$display_name" http://127.0.0.1:8080/$path_register
