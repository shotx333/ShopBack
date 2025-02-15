CREATE TABLE order_item
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    product_id BIGINT NULL,
    quantity   INT NULL,
    price DOUBLE NULL,
    order_id   BIGINT NULL,
    CONSTRAINT pk_orderitem PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    username   VARCHAR(255) NULL,
    created_at datetime NULL,
    total_price DOUBLE NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE product
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    price DOUBLE NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDERITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);