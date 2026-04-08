CREATE TABLE tb_product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    price FLOAT(53),
    date TIMESTAMP WITHOUT TIME ZONE,
    img_url VARCHAR(255),
    active BOOLEAN NOT NULL
);