CREATE TABLE person_settings (
                      id SERIAL PRIMARY KEY,
                      person_id INT UNIQUE NOT NULL,
                      post_comment_notification BOOLEAN DEFAULT TRUE,
                      comment_comment_notification BOOLEAN DEFAULT TRUE,
                      friend_request_notification BOOLEAN DEFAULT TRUE,
                      message_notification BOOLEAN DEFAULT TRUE,
                      friend_birthday_notification BOOLEAN DEFAULT TRUE,
                      like_notification BOOLEAN DEFAULT TRUE,
                      post_notification BOOLEAN DEFAULT TRUE,
                      CONSTRAINT fk_person_person_settings
                            FOREIGN KEY (person_id)
                                REFERENCES person (id)
);