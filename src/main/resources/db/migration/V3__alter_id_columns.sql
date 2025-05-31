-- Alter users table
ALTER TABLE users RENAME TO users_old;
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    street TEXT,
    suite TEXT,
    city TEXT,
    zipcode TEXT,
    lat TEXT,
    lng TEXT,
    phone TEXT,
    website TEXT,
    company_name TEXT,
    company_catch_phrase TEXT,
    company_bs TEXT
);
INSERT INTO users SELECT * FROM users_old;
DROP TABLE users_old;

-- Alter auth_users table
ALTER TABLE auth_users RENAME TO auth_users_old;
CREATE TABLE auth_users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
INSERT INTO auth_users SELECT * FROM auth_users_old;
DROP TABLE auth_users_old; 