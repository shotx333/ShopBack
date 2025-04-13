ALTER TABLE order_item
    DROP COLUMN price;

ALTER TABLE order_item
    ADD price DECIMAL NOT NULL;

ALTER TABLE order_item
    MODIFY price DECIMAL NOT NULL;

ALTER TABLE product
    DROP COLUMN price;

ALTER TABLE product
    ADD price DECIMAL NOT NULL;

ALTER TABLE orders
    DROP COLUMN total_price;

ALTER TABLE orders
    ADD total_price DECIMAL NOT NULL;

ALTER TABLE orders
    MODIFY total_price DECIMAL NOT NULL;