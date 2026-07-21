-- Seed initial roles on every startup
INSERT IGNORE INTO role (name) VALUES ('ADMIN');
INSERT IGNORE INTO role (name) VALUES ('USER');
