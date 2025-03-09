ALTER TABLE orders
    ADD payment_intent_id VARCHAR(255) NULL;

ALTER TABLE orders
    ADD payment_status VARCHAR(255) NULL;

ALTER TABLE product
    ADD stock INT NULL;

ALTER TABLE product
    MODIFY stock INT NOT NULL;