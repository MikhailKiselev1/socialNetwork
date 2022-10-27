ALTER TABLE person DROP COLUMN notifications_websocket_user_id;
ALTER TABLE person ADD COLUMN online_status VARCHAR (255) DEFAULT 'OFFLINE';