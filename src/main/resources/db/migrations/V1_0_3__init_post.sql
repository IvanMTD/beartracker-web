CREATE TABLE IF NOT EXISTS article (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sef TEXT UNIQUE,
    creator UUID,
    title TEXT UNIQUE,
    notation TEXT,
    content TEXT,
    meta_title TEXT,
    meta_description TEXT,
    meta_keywords TEXT[],
    created date,
    updated date
);