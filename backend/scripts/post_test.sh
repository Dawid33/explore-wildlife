#!/bin/sh

path_login="api/login"
path_register="api/register"
path_create_post="api/create-post"
path_get_post="api/post"
path_get_post_image="api/post/image"
path_get_nearest_posts="api/post/nearest"

email="email=test@example.com"
password="password=test"

#ls

# Login
#curl -X POST -F $email -F $password http://127.0.0.1:8080/$path_login

# Register
#email="cardi@b.com"
#password="omg"
#display_name="cardi_b"
#
#curl -X POST -F "email=$email" -F "password=$password" -F "display_name=$display_name" http://127.0.0.1:8080/$path_register

#post_title="From Shell"
#post_description="Hi. This is post from shell"
#post_latitude="12.3"
#post_longitude="542.1"
#created_by="f8737db2-5712-40bc-a6cc-0037ad417a00"
#image="backend/images/test.jpeg"
##image=""
#
## Create post with image
#curl -v -X POST -F "post_title=$post_title" -F "post_description=$post_description" -F "post_latitude=$post_latitude" -F "post_longitude=$post_longitude" -F "created_by=$created_by" -F "image=@$image" http://127.0.0.1:8080/$path_create_post

# Create post without image
#curl -v -X POST -F "post_title=$post_title" -F "post_description=$post_description" -F "post_latitude=$post_latitude" -F "post_longitude=$post_longitude" -F "created_by=$created_by" http://127.0.0.1:8080/$path_create_post

#curl -X POST -F "post_title=From Shell" -F "post_description=Hi. This is post from shell" -F "post_latitude=12.3" -F "post_longitude=521.1" -F "created_by=f8737db2-5712-40bc-a6cc-0037ad417a00" -F "file=@backend/images/test.jpeg" http://127.0.0.1:8080/$path_create_post

# Get post
post_id="151bbb26-56f9-4207-b2a0-5f8e850bf9ef"
post_id="c8c2ed17-28cc-48ba-b2d8-a00544e194fb"

#curl -X GET "http://127.0.0.1:8080/$path_get_post_image?id=$post_id"

# Nearest posts

curl -X GET "http://127.0.0.1:8080/$path_get_nearest_posts?id=$post_id"
