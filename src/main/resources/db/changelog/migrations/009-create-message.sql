CREATE TABLE message (
                         id serial PRIMARY KEY,
                         time TIMESTAMP NOT NULL,
                         author_id INT,
                         recipient_id INT,
                         message_text TEXT,
                         read_status VARCHAR (255),
                         CONSTRAINT fk_message_author
                                FOREIGN KEY (author_id)
                                    REFERENCES person (id),
                         CONSTRAINT fk_message_recipient
                                FOREIGN KEY (recipient_id)
                                    REFERENCES person (id)
);