CREATE TABLE product_image
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    image_url     VARCHAR(255) NULL,
    `primary`     BIT(1) NOT NULL,
    display_order INT NULL,
    product_id    BIGINT NULL,
    CONSTRAINT pk_productimage PRIMARY KEY (id)
);

ALTER TABLE product_image
    ADD CONSTRAINT FK_PRODUCTIMAGE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE product
    MODIFY stock INT NOT NULL;