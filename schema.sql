# DROP TABLE IF EXISTS `member`;

CREATE TABLE `member` (
    `id`	BIGINT	NOT NULL auto_increment,
    `email`	VARCHAR(100)	NOT NULL,
    `name`	VARCHAR(20)	NOT NULL,
    `password`	VARCHAR(255)	NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);

CREATE UNIQUE INDEX idx_user_email ON member(`email`);

# DROP TABLE IF EXISTS `member_roles`;

CREATE TABLE `member_roles`(
    `member_id` BIGINT NOT NULL,
    `roles` VARCHAR(10) NOT NULL,
    primary key (member_id, roles)
);

# DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`(
    `id` BIGINT NOT NULL auto_increment,
    `original_file_name` VARCHAR(100) NOT NULL COMMENT 'UUID 붙이기 전 파일명',
    `attached_file_name` VARCHAR(100) NOT NULL COMMENT 'UUID 붙여진 파일명',
    `original_file_path` VARCHAR(255) NOT NULL COMMENT '암호화하기 전 파일경로',
    `encrypted_file_path` VARCHAR(255) NOT NULL COMMENT '암호화된 파일경로',
    `product_id` BIGINT NULL,
    primary key (id)
);

# DROP TABLE IF EXISTS `term`;

CREATE TABLE `term` (
    `id`	BIGINT	NOT NULL auto_increment,
    `title`	VARCHAR(80)	NOT NULL COMMENT '약관 제목',
    `is_required`	BIT	NOT NULL COMMENT '약관 필수동의 여부',
    `additional`	BIT	NOT NULL COMMENT '추가 약관 여부',
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);

# DROP TABLE IF EXISTS `sub_term`;

CREATE TABLE `sub_term` (
    `term_id`	BIGINT	NOT NULL COMMENT 'term 테이블의 id값',
    `version`	INTEGER	NOT NULL COMMENT '약관 버전',
    `url`	VARCHAR(255)	NULL COMMENT '약관 링크',
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (term_id, version)
);


# DROP TABLE IF EXISTS `user_sub_term`;

CREATE TABLE `member_sub_term` (
    `member_id`	BIGINT	NOT NULL,
    `term_id`	BIGINT	NOT NULL,
    `version`	INTEGER	NOT NULL COMMENT '약관 버전',
    `agree`	BIT	NOT NULL COMMENT '동의 여부',
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (member_id, term_id, version)
);

# DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
    `id`	BIGINT	NOT NULL auto_increment,
    `amount`	INT	NOT NULL COMMENT '재고',
    `price`	INT	NOT NULL COMMENT '가격',
    `name`	VARCHAR(50)	NOT NULL COMMENT '제품명',
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    `category_id`	BIGINT	NOT NULL,
    primary key (id)
);
CREATE INDEX idx_product_category_id ON product(`category_id`);

# DROP TABLE IF EXISTS `event`;

CREATE TABLE `event` (
    `id`	BIGINT	NOT NULL auto_increment,
    `name`	VARCHAR(30)	NOT NULL COMMENT '이벤트명',
    `start_date`	DATETIME	NULL COMMENT '이벤트 시작일',
    `end_date`	DATETIME	NULL COMMENT '이벤트 종료일',
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);

# DROP TABLE IF EXISTS `cart`;

CREATE TABLE `cart` (
    `id`	BIGINT	NOT NULL auto_increment,
    `user_id`	BIGINT	NOT NULL,
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);
CREATE INDEX idx_cart_user_id ON cart(`user_id`);

# DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
    `id`	BIGINT	NOT NULL auto_increment,
    `name`	VARCHAR(20)	NULL COMMENT '카테고리명',
    `parent_id`	BIGINT	NOT NULL,
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);
CREATE INDEX idx_category_parent_id on category(`parent_id`);

# DROP TABLE IF EXISTS `cart_product`;

CREATE TABLE `cart_product` (
    `id`	BIGINT	NOT NULL auto_increment,
    `product_amount`	INT	NOT NULL COMMENT '주문한 상품 갯수',
    `cart_id`	BIGINT	NOT NULL,
    `product_id`	BIGINT	NOT NULL,
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);
CREATE INDEX idx_cart_product_cart_id on cart_product(`cart_id`);
CREATE INDEX idx_cart_product_product_id on cart_product(`product_id`);

# DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
    `id`	BIGINT	NOT NULL auto_increment,
    `order_date`	DATETIME	NOT NULL COMMENT '주문일',
    `delivery_id`	BIGINT	NOT NULL,
    `cart_id`	BIGINT	NOT NULL,
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);
CREATE INDEX idx_orders_delivery_id on `orders`(`delivery_id`);
CREATE INDEX idx_orders_cart_id on `orders`(`cart_id`);

# DROP TABLE IF EXISTS `delivery`;

CREATE TABLE `delivery` (
    `id`	BIGINT	NOT NULL auto_increment,
    `status`	VARCHAR(30)	NOT NULL COMMENT '배송 상태',
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);

# DROP TABLE IF EXISTS `event_product`;

CREATE TABLE `event_product` (
    `id`	BIGINT	NOT NULL auto_increment,
    `product_id`	BIGINT	NOT NULL,
    `event_id`	BIGINT	NOT NULL,
    `created_at`	DATETIME	NOT NULL,
    `modified_at`	DATETIME	NOT NULL,
    primary key (id)
);
CREATE INDEX idx_event_product_product_id on `event_product`(`product_id`);
CREATE INDEX idx_event_product_event_id on `event_product`(`event_id`);

commit;
