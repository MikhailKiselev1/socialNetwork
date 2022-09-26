CREATE TABLE post_comment (
                      id serial PRIMARY KEY,
                      time TIMESTAMP NOT NULL,
                      post_id INT,
                      parent_id INT,
                      author_id INT,
                      comment_text TEXT,
                      is_blocked BOOLEAN DEFAULT FALSE,
                      is_deleted BOOLEAN DEFAULT FALSE,
                        CONSTRAINT fk_comment_parent
                            FOREIGN KEY (parent_id)
                                REFERENCES post_comment (id),
                        CONSTRAINT fk_comment_person
                            FOREIGN KEY (author_id)
                                REFERENCES person (id),
                        CONSTRAINT fk_comment_post
                            FOREIGN KEY (post_id)
                                REFERENCES post (id)
);