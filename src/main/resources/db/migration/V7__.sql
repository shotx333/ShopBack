ALTER TABLE product
    ADD stock INT NULL;

ALTER TABLE product
    MODIFY stock INT NOT NULL;

ALTER TABLE product
    MODIFY category_id BIGINT NOT NULL;