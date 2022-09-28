CREATE TABLE dialog (
                      id serial PRIMARY KEY,
                      first_person_id INT NOT NULL,
                      second_person_id INT,
                      last_message_id INT,
                      last_active_time TIMESTAMP NOT NULL,
                      CONSTRAINT fk_dialog_first_person
                          FOREIGN KEY (first_person_id)
                              REFERENCES person (id),
                      CONSTRAINT fk_dialog_second_person
                          FOREIGN KEY (second_person_id)
                              REFERENCES person (id),
                      CONSTRAINT fk_dialog_last_message
                          FOREIGN KEY (last_message_id)
                              REFERENCES message (id)
);