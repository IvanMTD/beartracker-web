ALTER TABLE article ADD COLUMN links TEXT[];

CREATE TABLE IF NOT EXISTS article_image(
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    parent UUID,
    image_url_sl TEXT,
    image_url_md TEXT,
    image_url_lg TEXT
);