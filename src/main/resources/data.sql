INSERT INTO user_roles (name, created_at, updated_at) VALUES ('User', current_timestamp, current_timestamp);

INSERT INTO users (username, email, password, active, status, created_at, updated_at) VALUES ('Jenya', 'jenya.yacovets@gmail.com', '$2a$10$6lmEyTeqZkJhiV7SLh8DRujeq3HX3HBO5n/T5Z9VdxGGSfXtzGN/2', true, 0, current_timestamp, current_timestamp);
INSERT INTO users_roles VALUES (1, 1);
INSERT INTO users (username, email, password, active, status, created_at, updated_at) VALUES ('Boris', 'boris@gmail.com', '$2a$10$6lmEyTeqZkJhiV7SLh8DRujeq3HX3HBO5n/T5Z9VdxGGSfXtzGN/2', true, 0, current_timestamp, current_timestamp);
INSERT INTO users_roles VALUES (2, 1);
