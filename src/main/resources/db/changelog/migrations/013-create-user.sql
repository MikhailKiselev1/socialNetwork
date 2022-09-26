CREATE TABLE "user" (
                      id serial PRIMARY KEY,
                      name VARCHAR (255) NOT NULL,
                      e_mail VARCHAR (255),
                      password VARCHAR (255) NOT NULL,
                      type VARCHAR (255) NOT NULL
);