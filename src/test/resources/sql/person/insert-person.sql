INSERT INTO person(id, first_name, last_name, reg_date, birth_date, email, phone, password, photo, about, city, country,
confirmation_code, is_approved, messages_permission, last_online_time, is_blocked)
    values (1, 'test', 'testov', '2011-01-01 00:00:00', '2011-01-01 00:00:00', 'test@mail.ru', '8950',
     '$2a$12$UZd0cpBO1YP7hpS16flN3OpiDmqGVEeDnk7QU9ZdmRJ1/f0wGNm3O', '/cb73442c-305e-4e15-a7fd-d65d5969dd7e.jpg',
      'about', 'Moscow', 'Russia', 1234, true, 'ALL', '2011-01-01 00:00:00', false);
