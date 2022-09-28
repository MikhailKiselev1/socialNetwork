INSERT INTO post(id, time, author_id, title, post_text, is_blocked, is_deleted)
VALUES (1, '2011-01-01 00:00:00', 1, 'title', 'post_text', false, false);
INSERT INTO notification(id, notification_type, sent_time, person_id, entity_id, contact, is_read)
VALUES (1, 'POST', '2011-01-01 00:00:00', 1, 1, '', false);