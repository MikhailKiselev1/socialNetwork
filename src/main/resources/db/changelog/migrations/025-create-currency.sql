CREATE TABLE currency (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR (255) UNIQUE NOT NULL,
                      price VARCHAR (255)
);