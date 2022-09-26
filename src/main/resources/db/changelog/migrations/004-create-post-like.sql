CREATE TABLE post_like (
                           id serial PRIMARY KEY,
                           time TIMESTAMP NOT NULL,
                           person_id INT,
                           post_id INT,
                           type VARCHAR (255),
                           CONSTRAINT fk_person_like
                                FOREIGN KEY (person_id)
                                    REFERENCES person (id)
);