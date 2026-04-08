ALTER TABLE tb_product_category
    ADD CONSTRAINT fk_product_category_category
    FOREIGN KEY (category_id)
    REFERENCES tb_category (id);

ALTER TABLE tb_product_category
    ADD CONSTRAINT fk_product_category_product
    FOREIGN KEY (product_id)
    REFERENCES tb_product (id);