CREATE TABLE friendship (
                            id serial PRIMARY KEY,
                            status_id INT,
                            sent_time TIMESTAMP NOT NULL,
                            src_person_id INT,
                            dst_person_id INT,
                            CONSTRAINT fk_friendship
                                FOREIGN KEY (status_id)
                                    REFERENCES friendship_status (id),
                            CONSTRAINT fk_sender_person
                                FOREIGN KEY (src_person_id)
                                    REFERENCES person (id),
                            CONSTRAINT fk_recipient_person
                                FOREIGN KEY (dst_person_id)
                                    REFERENCES person (id)
);