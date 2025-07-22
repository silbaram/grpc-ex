-- 상품 정보
CREATE TABLE IF NOT EXISTS goods (
    goods_no BIGINT PRIMARY KEY,
    goods_name VARCHAR(255) NOT NULL,
    price BIGINT,
    stock_level BIGINT
);