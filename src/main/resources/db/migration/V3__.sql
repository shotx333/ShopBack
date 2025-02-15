CREATE TABLE category
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

ALTER TABLE product
    ADD category_id BIGINT NULL;

ALTER TABLE product
    MODIFY category_id BIGINT NOT NULL;

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);