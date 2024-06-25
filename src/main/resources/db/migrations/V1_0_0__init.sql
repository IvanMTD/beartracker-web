CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS application_user (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE,
    email TEXT UNIQUE,
    password TEXT,
    avatar TEXT,
    lastname TEXT,
    name TEXT,
    middle_name TEXT,
    birthday DATE,
    placed_at DATE,
    role TEXT
);