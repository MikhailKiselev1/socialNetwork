ALTER TABLE IF EXISTS message ADD COLUMN dialog_id INT NOT NULL;

ALTER TABLE IF EXISTS message ADD CONSTRAINT fk_message_dialog FOREIGN KEY (dialog_id) REFERENCES dialog (id);