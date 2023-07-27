CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  user_id BIGINT NOT NULL,
  available BOOLEAN NOT NULL,
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS booking (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_time timestamp NOT NULL,
  end_time timestamp NOT NULL,
  item_id BIGINT NOT NULL,
  owner_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  text VARCHAR(512) NOT NULL,
  created timestamp NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);
