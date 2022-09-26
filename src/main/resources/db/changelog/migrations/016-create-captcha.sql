CREATE TABLE captcha (
                                   id serial PRIMARY KEY,
                                   time TIMESTAMP NOT NULL,
                                   code VARCHAR (255) NOT NULL,
                                   secret_code VARCHAR (255) NOT NULL
);