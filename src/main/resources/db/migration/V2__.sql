ALTER TABLE users
    ADD `role` VARCHAR(255) NULL;

ALTER TABLE product
    MODIFY price DOUBLE NOT NULL;