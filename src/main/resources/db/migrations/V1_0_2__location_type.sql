ALTER TABLE location ADD COLUMN location_type UUID;

create table if not exists location_type(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT UNIQUE ,
    description TEXT,
    image_url TEXT
);