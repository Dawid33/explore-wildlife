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
	post_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	content varchar NULL DEFAULT 'Default content.',
	created_by uuid NOT NULL,
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE TABLE app.comments (
	parent_post_id uuid NOT NULL,
	comment_id uuid NOT NULL DEFAULT uuid_generate_v4(),
	created_by uuid NOT NULL,
    created_at timestamp NOT NULL DEFAULT NOW()
);

CREATE TABLE app.comments_edges (
	parent uuid NOT NULL,
	child uuid NOT NULL
);

-- TODO: Test this whether it actually speeds up queries.
CREATE INDEX latest_posts_index ON app.posts (created_at DESC);

DO $$
DECLARE
test_id uuid := uuid_generate_v4();
BEGIN
    INSERT INTO app.users (display_name, email, password) VALUES ('John Doe test', 'jdoe@example.com', 'jdoe');
    INSERT INTO app.users (user_id, display_name, email, password) VALUES (test_id, 'test', 'test@example.com', 'test');

    INSERT INTO app.posts (content, created_by) VALUES ('This is my post', test_id);
    INSERT INTO app.posts (content, created_by) VALUES ('This is another post', test_id);
END $$;

