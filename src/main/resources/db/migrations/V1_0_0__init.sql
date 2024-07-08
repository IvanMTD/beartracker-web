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

CREATE TABLE IF NOT EXISTS location (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sef TEXT UNIQUE,
    creator UUID,
    latitude float,
    longitude float,
    title TEXT UNIQUE,
    notation TEXT,
    subject UUID,
    meta_description TEXT,
    meta_keywords TEXT[],
    created date,
    updated date
);

CREATE TABLE IF NOT EXISTS location_content (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent UUID NOT NULL,
    position int NOT NULL,
    content_type TEXT NOT NULL,
    content_title TEXT,
    content TEXT,
    image_url_sm TEXT,
    image_url_md TEXT,
    image_url_lg TEXT,
    image_description TEXT
);

CREATE TABLE IF NOT EXISTS subject(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title text not null,
    iso text not null,
    federal_district text not null
);