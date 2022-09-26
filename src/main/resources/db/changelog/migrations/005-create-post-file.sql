CREATE TABLE post_file (
                           id serial PRIMARY KEY,
                           post_id INT,
                           name VARCHAR (255),
                           path VARCHAR (255),
                           CONSTRAINT fk_file_post
                                FOREIGN KEY (post_id)
                                    REFERENCES post (id)
);