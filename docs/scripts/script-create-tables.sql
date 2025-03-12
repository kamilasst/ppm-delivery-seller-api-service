CREATE TABLE br_seller (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    identification_type VARCHAR(50) NOT NULL,
    identification_code VARCHAR(14) NOT NULL UNIQUE CHECK (LENGTH(identification_code) = 14 AND identification_code ~ '^[0-9]+$'),
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    location_latitude VARCHAR(50) NOT NULL,
    location_longitude VARCHAR(50) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    number VARCHAR(10) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    creator_id VARCHAR(255),
    status VARCHAR(50) CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'BLOCKED', 'REJECTED')),
    create_at VARCHAR(30) NOT NULL,
    update_at VARCHAR(30),
    CONSTRAINT fk_seller_identification_code UNIQUE (identification_code)  -- Garantir a unicidade do identification_code
);

CREATE TABLE us_seller (LIKE br_seller INCLUDING ALL);
CREATE TABLE ar_seller (LIKE br_seller INCLUDING ALL);
CREATE TABLE co_seller (LIKE br_seller INCLUDING ALL);
CREATE TABLE mx_seller (LIKE br_seller INCLUDING ALL);
CREATE TABLE py_seller (LIKE br_seller INCLUDING ALL);

CREATE TABLE br_contact (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    value VARCHAR(100) NOT NULL,
    seller_code VARCHAR(255) NOT NULL,
    CONSTRAINT fk_br_seller FOREIGN KEY (seller_code) REFERENCES br_seller(code) ON DELETE CASCADE
);

CREATE TABLE us_contact (LIKE br_contact INCLUDING ALL);
CREATE TABLE ar_contact (LIKE br_contact INCLUDING ALL);
CREATE TABLE co_contact (LIKE br_contact INCLUDING ALL);
CREATE TABLE mx_contact (LIKE br_contact INCLUDING ALL);
CREATE TABLE py_contact (LIKE br_contact INCLUDING ALL);


CREATE TABLE br_business_hour (
    id SERIAL PRIMARY KEY,
    day_of_week VARCHAR(20) NOT NULL,
    open_at VARCHAR(8) NOT NULL,
    close_at VARCHAR(8) NOT NULL,
    seller_code VARCHAR(255) NOT NULL,
    CONSTRAINT fk_br_seller FOREIGN KEY (seller_code) REFERENCES br_seller(code) ON DELETE CASCADE
);

CREATE TABLE us_business_hour (LIKE br_business_hour INCLUDING ALL);
CREATE TABLE ar_business_hour (LIKE br_business_hour INCLUDING ALL);
CREATE TABLE co_business_hour (LIKE br_business_hour INCLUDING ALL);
CREATE TABLE mx_business_hour (LIKE br_business_hour INCLUDING ALL);
CREATE TABLE py_business_hour (LIKE br_business_hour INCLUDING ALL);