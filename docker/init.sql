CREATE EXTENSION postgis;

CREATE SCHEMA app;

CREATE TYPE category as ENUM ('SCENERY', 'ANIMAL', 'PLANT');

-- Needed to enable sql function that generates UUID's https://www.postgresql.org/docs/current/uuid-ossp.html
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA app;

CREATE TABLE app.users (
	user_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	display_name varchar NULL DEFAULT 'Display name not set',
	email varchar NOT NULL,
	phone_number varchar DEFAULT NULL,
	password varchar NOT NULL,
	profile_pic_id uuid DEFAULT NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_id)
);

CREATE TABLE app.posts (
	post_id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
	title varchar NOT NULL DEFAULT 'Post Title',
	description varchar DEFAULT 'Post Description',
	content varchar NULL DEFAULT 'Default content.',
	created_by uuid NOT NULL,
    created_at timestamp NOT NULL DEFAULT NOW(),
    is_public boolean NOT NULL DEFAULT true,
    latitude float DEFAULT 1,
    longitude float DEFAULT 1,
    coordinates float[2],
    location geography,
    post_category category NOT NULL DEFAULT 'SCENERY'
);

CREATE TABLE app.posts_likes (
    posts_likes_id SERIAL PRIMARY KEY,
	post_id uuid NOT NULL,
	user_id uuid NOT NULL,
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE TABLE app.species (
    species_id SERIAL PRIMARY KEY,
    species_name varchar NOT NULL DEFAULT 'Test Species',
    species_image_path varchar DEFAULT NULL
);

CREATE TABLE app.posts_species (
    posts_species_id SERIAL PRIMARY KEY,
    post_id uuid NOT NULL,
	species_id int NOT NULL
);

CREATE TABLE app.posts_species (
    posts_species_id SERIAL PRIMARY KEY,
    post_id uuid NOT NULL,
	species_id int NOT NULL
);

CREATE TABLE app.users_species (
	post uuid NOT NULL,
	owner uuid NOT NULL,
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE TABLE app.comments_edges (
	parent uuid NOT NULL,
	child uuid NOT NULL
);

CREATE TABLE app.images (
	image_id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
	name varchar NULL DEFAULT 'Default image name',
	owner uuid NOT NULL,
    added_at timestamp NOT NULL DEFAULT NOW(),
	image_path varchar NOT NULL
);

CREATE TABLE app.post_images (
	image_id uuid NOT NULL,
	post_id uuid NOT NULL
);

-- TODO: Test this whether it actually speeds up queries.
CREATE INDEX latest_posts_index ON app.posts (created_at DESC);

ALTER TABLE app.users
ADD CONSTRAINT fk_profile_pic
FOREIGN KEY(profile_pic_id)
REFERENCES app.images(image_id);

ALTER TABLE app.posts
ADD CONSTRAINT fk_created_by
FOREIGN KEY(created_by)
REFERENCES app.users(user_id)
ON DELETE CASCADE;

ALTER TABLE app.images
ADD CONSTRAINT fk_owner
FOREIGN KEY(owner)
REFERENCES app.users(user_id)
ON DELETE CASCADE;

ALTER TABLE app.posts_likes
ADD CONSTRAINT fk_post_id
FOREIGN KEY(post_id)
REFERENCES app.posts(post_id)
ON DELETE CASCADE,
ADD CONSTRAINT fk_user_id
FOREIGN KEY(user_id)
REFERENCES app.users(user_id)
ON DELETE CASCADE;

ALTER TABLE app.posts_species
ADD CONSTRAINT fk_post_id
FOREIGN KEY(post_id)
REFERENCES app.posts(post_id)
ON DELETE CASCADE,
ADD CONSTRAINT fk_species_id
FOREIGN KEY(species_id)
REFERENCES app.species(species_id)
ON DELETE CASCADE;

ALTER TABLE app.post_images
ADD CONSTRAINT fk_post_id
FOREIGN KEY(post_id)
REFERENCES app.posts(post_id)
ON DELETE CASCADE,
ADD CONSTRAINT fk_image_id
FOREIGN KEY(image_id)
REFERENCES app.images(image_id)
ON DELETE CASCADE;


DO $$
DECLARE
test_id uuid := uuid_generate_v4();
image_id uuid := uuid_generate_v4();
post_id uuid := uuid_generate_v4();

user_id1 uuid := uuid_generate_v4();
image_id1 uuid := uuid_generate_v4();
post_id1 uuid := uuid_generate_v4();
profile_pic_id1 uuid := uuid_generate_v4();

user_id2 uuid := uuid_generate_v4();
image_id2 uuid := uuid_generate_v4();
post_id2 uuid := uuid_generate_v4();
profile_pic_id2 uuid := uuid_generate_v4();

user_id3 uuid := uuid_generate_v4();
image_id3 uuid := uuid_generate_v4();
post_id3 uuid := uuid_generate_v4();
profile_pic_id3 uuid := uuid_generate_v4();

user_id4 uuid := uuid_generate_v4();
image_id4 uuid := uuid_generate_v4();
post_id4 uuid := uuid_generate_v4();
profile_pic_id4 uuid := uuid_generate_v4();
BEGIN
    INSERT INTO app.users (display_name, email, password) VALUES ('default', 'default@example.com', 'default');
    INSERT INTO app.users (display_name, email, password) VALUES ('John Doe test', 'jdoe@example.com', 'jdoe');

    INSERT INTO app.users (user_id, display_name, email, password) VALUES (test_id, 'test', 'test@example.com', 'test');

    INSERT INTO app.users (user_id, display_name, email, password) VALUES (user_id1, 'test1', 'test1@example.com', 'test');

    INSERT INTO app.users (user_id, display_name, email, password) VALUES (user_id2, 'test2', 'test2@example.com', 'test');

    INSERT INTO app.users (user_id, display_name, email, password) VALUES (user_id3, 'test3', 'test3@example.com', 'test');

    INSERT INTO app.users (user_id, display_name, email, password) VALUES (user_id4, 'test4', 'test4@example.com', 'test');


    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (image_id, test_id, 'Test Image', 'images/test.jpeg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (profile_pic_id1, user_id1, 'Test Image', 'images/frog_king.jpg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (image_id1, user_id1, 'Test Image', 'images/scene1.jpg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (profile_pic_id2, user_id2, 'Test Image', 'images/messi.jpg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (image_id2, user_id2, 'Test Image', 'images/mushroom.jpg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (profile_pic_id3, user_id3, 'Test Image', 'images/tom.jpg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (image_id3, user_id3, 'Test Image', 'images/bird.jpg');

    INSERT INTO app.images (image_id, owner, name, image_path) VALUES (profile_pic_id4, user_id4, 'Test Image', 'images/woman.jpg');

      INSERT INTO app.images (image_id, owner, name, image_path) VALUES (image_id4, user_id4, 'Test Image', 'images/badger.jpg');

    UPDATE app.users
    SET profile_pic_id = image_id
    WHERE user_id = test_id;

    UPDATE app.users
    SET profile_pic_id = profile_pic_id1
    WHERE user_id = user_id1;

    UPDATE app.users
    SET profile_pic_id = profile_pic_id2
    WHERE user_id = user_id2;

    UPDATE app.users
    SET profile_pic_id = profile_pic_id3
    WHERE user_id = user_id3;

    UPDATE app.users
    SET profile_pic_id = profile_pic_id4
    WHERE user_id = user_id4;


--    INSERT INTO app.users (profile_pic_id, user_id, display_name, email, password) VALUES (image_id, test_id, 'test', 'test@example.com', 'test');
--
--    INSERT INTO app.users (profile_pic_id, user_id, display_name, email, password) VALUES (profile_pic_id1, user_id1, 'test1', 'test1@example.com', 'test');
--
--    INSERT INTO app.users (profile_pic_id, user_id, display_name, email, password) VALUES (profile_pic_id2, user_id2, 'test2', 'test2@example.com', 'test');
--
--    INSERT INTO app.users (profile_pic_id, user_id, display_name, email, password) VALUES (profile_pic_id3, user_id3, 'test3', 'test3@example.com', 'test');
--
--    INSERT INTO app.users (profile_pic_id, user_id, display_name, email, password) VALUES (profile_pic_id4, user_id4, 'test4', 'test4@example.com', 'test');

    INSERT INTO app.posts (post_id, content, created_by) VALUES (post_id, 'This is my post', test_id);
    INSERT INTO app.post_images (post_id, image_id) VALUES (post_id, image_id);

    INSERT INTO app.posts (post_id, content, created_by) VALUES (post_id1, 'This is my post', user_id1);
    INSERT INTO app.post_images (post_id, image_id) VALUES (post_id1, image_id1);

      INSERT INTO app.posts (post_id, content, created_by) VALUES (post_id2, 'This is my post', user_id2);
    INSERT INTO app.post_images (post_id, image_id) VALUES (post_id2, image_id2);

      INSERT INTO app.posts (post_id, content, created_by) VALUES (post_id3, 'This is my post', user_id3);
    INSERT INTO app.post_images (post_id, image_id) VALUES (post_id3, image_id3);

      INSERT INTO app.posts (post_id, content, created_by) VALUES (post_id4, 'This is my post', user_id4);
    INSERT INTO app.post_images (post_id, image_id) VALUES (post_id4, image_id4);
END $$;

