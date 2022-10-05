insert into dialog(id, first_person_id, second_person_id, last_message_id, last_active_time)
values (1, 1, 2, null, '2022-09-28 15:42:08.064033');
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id)
values (1, '2022-09-28 15:42:08.064033', 1, 2, 'hello', 'SENT', 1);
update dialog set last_message_id = 1;