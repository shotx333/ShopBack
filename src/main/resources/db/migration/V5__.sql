ALTER TABLE product
    ADD image_url VARCHAR(255) NULL;

ALTER TABLE product
    MODIFY category_id BIGINT NOT NULL;