CREATE TABLE notification (
                              id serial PRIMARY KEY,
                              notification_type VARCHAR (255),
                              sent_time TIMESTAMP NOT NULL,
                              person_id INT,
                              entity_id INT,
                              contact VARCHAR (255),
                              is_read BOOLEAN DEFAULT FALSE,
                              CONSTRAINT fk_person_notification
                                    FOREIGN KEY (person_id)
                                        REFERENCES person (id)
);