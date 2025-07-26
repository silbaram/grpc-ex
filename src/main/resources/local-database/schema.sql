-- 상품 정보
CREATE TABLE IF NOT EXISTS goods (
    goods_no BIGINT PRIMARY KEY,
    goods_name VARCHAR(255) NOT NULL,
    price BIGINT,
    stock_level BIGINT
);

CREATE TABLE ERROR_MESSAGE (
    ID          BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 식별자 (자동 증가)
    ERROR_CODE  VARCHAR(50)  NOT NULL,             -- 오류 코드 (예: E404)
    LANG        VARCHAR(10)  NOT NULL,             -- 언어 코드 (예: ko, en)
    MESSAGE     VARCHAR(255) NOT NULL,             -- 화면에 노출될 메시지

    -- 오류 코드와 언어의 조합은 고유해야 하므로 UNIQUE 제약조건 추가
    CONSTRAINT UK_ERROR_CODE_LANG UNIQUE (ERROR_CODE, LANG)
);