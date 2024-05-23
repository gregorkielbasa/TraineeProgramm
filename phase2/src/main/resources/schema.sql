CREATE TABLE IF NOT EXISTS PRODUCTS (
    PRODUCT_ID BIGINT AUTO_INCREMENT (100000000) PRIMARY KEY,
    PRODUCT_NAME VARCHAR(24) NOT NULL);

CREATE TABLE IF NOT EXISTS CUSTOMERS (
    CUSTOMER_ID BIGINT AUTO_INCREMENT (100000000) PRIMARY KEY,
    CUSTOMER_NAME VARCHAR(16) NOT NULL);

CREATE TABLE IF NOT EXISTS BASKETS (
    BASKET_ID BIGINT  AUTO_INCREMENT (1000) PRIMARY KEY,
    CUSTOMER_ID BIGINT NOT NULL);

CREATE TABLE IF NOT EXISTS BASKET_ITEMS (
    BASKET_ID BIGINT NOT NULL,
    PRODUCT_ID BIGINT NOT NULL,
    AMOUNT INT NOT NULL,
    PRIMARY KEY (BASKET_ID, PRODUCT_ID));

CREATE TABLE IF NOT EXISTS ORDERS (
    ORDER_ID BIGINT AUTO_INCREMENT (1000) PRIMARY KEY,
    CUSTOMER_ID BIGINT NOT NULL);

CREATE TABLE IF NOT EXISTS ORDER_ITEMS (
    ORDER_ID BIGINT NOT NULL,
    PRODUCT_ID BIGINT NOT NULL,
    AMOUNT INT NOT NULL,
    PRIMARY KEY (ORDER_ID, PRODUCT_ID));