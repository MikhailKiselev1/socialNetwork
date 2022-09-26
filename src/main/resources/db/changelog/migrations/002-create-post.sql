CREATE TABLE post (
                      id SERIAL PRIMARY KEY,
                      time TIMESTAMP NOT NULL,
                      author_id INT,
                      title VARCHAR (255),
                      post_text TEXT,
                      is_blocked BOOLEAN DEFAULT FALSE,
                      is_deleted BOOLEAN DEFAULT FALSE,
                      CONSTRAINT fk_person_post
                            FOREIGN KEY (author_id)
                                REFERENCES person (id)
);