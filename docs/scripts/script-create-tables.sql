CREATE TABLE seller (
    code VARCHAR(255) PRIMARY KEY,
    country_code VARCHAR(10) NOT NULL,
    identification_type VARCHAR(50) NOT NULL,
    identification_code VARCHAR(14) NOT NULL,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    location_latitude DOUBLE PRECISION(50) NOT NULL,
    location_longitude DOUBLE PRECISION(50) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    number VARCHAR(10) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    creator_id VARCHAR(255),
    status VARCHAR(50) CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'BLOCKED', 'REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT unique_seller_identification_code UNIQUE (identification_code)
);

CREATE INDEX idx_seller_country_code ON seller(country_code);

CREATE TABLE contact (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    value VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    seller_code VARCHAR(255) NOT NULL,
    CONSTRAINT fk_seller FOREIGN KEY (seller_code) REFERENCES seller(code) ON DELETE CASCADE
);

CREATE TABLE business_hour (
    id SERIAL PRIMARY KEY,
    day_of_week VARCHAR(20) NOT NULL,
    open_at VARCHAR(8) NOT NULL,
    close_at VARCHAR(8) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    seller_code VARCHAR(255) NOT NULL,
    CONSTRAINT fk_seller FOREIGN KEY (seller_code) REFERENCES seller(code) ON DELETE CASCADE
);










