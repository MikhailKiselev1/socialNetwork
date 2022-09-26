CREATE TABLE block_history (
                               id serial PRIMARY KEY,
                               time TIMESTAMP NOT NULL,
                               person_id INT,
                               post_id INT,
                               comment_id INT,
                               action VARCHAR (255),
                               CONSTRAINT fk_block_person
                                    FOREIGN KEY (person_id)
                                        REFERENCES person (id),
                               CONSTRAINT fk_block_post
                                    FOREIGN KEY (post_id)
                                        REFERENCES post (id),
                               CONSTRAINT fk_block_comment
                                    FOREIGN KEY (comment_id)
                                        REFERENCES post_comment (id)
);