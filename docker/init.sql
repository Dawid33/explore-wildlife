CREATE SCHEMA app;

-- Needed to enable sql function that generates UUID's https://www.postgresql.org/docs/current/uuid-ossp.html
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA app;

CREATE TABLE app.users (
	user_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	display_name varchar NULL DEFAULT 'Display name not set',
	email varchar NOT NULL,
	password varchar NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_id)
);

CREATE TABLE app.posts (
	created_by uuid NOT NULL,
	post_id uuid NOT NULL DEFAULT uuid_generate_v4()
);

CREATE TABLE app.comments (
	parent_post_id uuid NOT NULL,
	comment_id uuid NOT NULL DEFAULT uuid_generate_v4()
);

CREATE TABLE app.comments_edges (
	parent uuid NOT NULL,
	child uuid NOT NULL
);

INSERT INTO app.users (display_name, email, password) VALUES ('John Doe Bro', 'jdoe@example.com', 'jdoe');
INSERT INTO app.users (display_name, email, password) VALUES ('another', 'another@example.com', 'another');
INSERT INTO app.users (display_name, email, password) VALUES ('test', 'test@example.com', 'test');
