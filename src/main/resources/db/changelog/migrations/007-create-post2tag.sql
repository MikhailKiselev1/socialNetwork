CREATE TABLE post2tag (
                          id serial PRIMARY KEY,
                          tag_id INT,
                          post_id INT,
                                CONSTRAINT fk_tag
                                    FOREIGN KEY (tag_id)
                                        REFERENCES tag (id)
);