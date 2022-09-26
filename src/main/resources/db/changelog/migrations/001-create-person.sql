CREATE TABLE person (
                        id SERIAL PRIMARY KEY,
                        first_name VARCHAR (255) NOT NULL,
                        last_name VARCHAR (255) NOT NULL,
                        reg_date TIMESTAMP NOT NULL,
                        birth_date TIMESTAMP,
                        email VARCHAR (255) UNIQUE NOT NULL,
                        phone VARCHAR(255) UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        photo VARCHAR(255),
                        about VARCHAR (255),
                        city VARCHAR (255),
                        country VARCHAR (255),
                        confirmation_code INT,
                        is_approved BOOLEAN DEFAULT FALSE NOT NULL,
                        messages_permission VARCHAR (255),
                        last_online_time TIMESTAMP,
                        is_blocked BOOLEAN DEFAULT FALSE
);