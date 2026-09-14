-- 乡镇农产品信息发布平台 数据库初始化（建表 + 江西演示数据）
-- MySQL 8.x，字符集 utf8mb4，可重复执行

CREATE DATABASE IF NOT EXISTS agri_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE agri_platform;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    real_name   VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/USER',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 农产品分类表
CREATE TABLE IF NOT EXISTS category (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort        INT         DEFAULT 0 COMMENT '排序',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '农产品分类表';

-- 农产品表
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100)   NOT NULL COMMENT '产品名称',
    category_id BIGINT         DEFAULT NULL COMMENT '分类ID',
    origin      VARCHAR(200)   DEFAULT NULL COMMENT '产地',
    description TEXT           COMMENT '描述',
    price       DECIMAL(10, 2) DEFAULT NULL COMMENT '价格',
    unit        VARCHAR(20)    DEFAULT NULL COMMENT '单位：斤/袋/盒',
    cover_url   VARCHAR(255)   DEFAULT NULL COMMENT '封面图URL',
    image_urls  VARCHAR(1000)  DEFAULT NULL COMMENT '多图URL，逗号分隔',
    longitude   DOUBLE         DEFAULT NULL COMMENT '经度',
    latitude    DOUBLE         DEFAULT NULL COMMENT '纬度',
    stock       INT            DEFAULT 0 COMMENT '库存',
    status      TINYINT        DEFAULT 1 COMMENT '状态：0下架 1上架',
    audit_status TINYINT       DEFAULT 0 COMMENT '审核状态：0待审核 1通过 2驳回',
    create_by   BIGINT         DEFAULT NULL COMMENT '发布人ID',
    deleted     TINYINT        NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
    create_time DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category_id),
    KEY idx_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '农产品表';

-- 通知表
CREATE TABLE IF NOT EXISTS notice (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    content     TEXT         COMMENT '内容',
    publish_by  BIGINT       DEFAULT NULL COMMENT '发布人ID',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '通知表';

-- 初始分类（先清空再插入，保证可重复执行）
TRUNCATE TABLE category;
INSERT INTO category (name, sort) VALUES
    ('脐橙', 1),
    ('茶叶', 2),
    ('大米', 3),
    ('其他', 4);

-- 演示数据：农产品（清空后插入，演示数据默认已审核通过）
DELETE FROM product;
INSERT INTO product
    (name, category_id, origin, description, price, unit, stock, longitude, latitude, status, audit_status)
VALUES
    ('赣南脐橙', 1, '江西省赣州市信丰县果园', '果大形正，橙红鲜艳，肉质脆嫩化渣，甜酸适度，国家地理标志产品。', 4.50, '斤', 20000, 114.9305, 25.3861, 1, 1),
    ('寻乌血橙', 1, '江西省赣州市寻乌县', '色泽紫红，汁多味甜，含丰富花青素，当地特色品种。', 6.00, '斤', 8000, 115.6512, 24.9633, 1, 1),
    ('安远蜜橘', 1, '江西省赣州市安远县', '皮薄汁多，甜度高，耐储存，主产区位于东江源头。', 3.80, '斤', 12000, 115.3921, 25.1376, 1, 1),
    ('井冈山绿茶', 2, '江西省吉安市井冈山市', '产自黄洋界高山茶园，香气持久，滋味鲜爽回甘。', 88.00, '盒', 1500, 114.2892, 26.5705, 1, 1),
    ('婺源绿茶', 2, '江西省上饶市婺源县', '汤清叶绿，香高味醇，中国绿茶之乡出品。', 68.00, '袋', 2000, 117.8622, 29.2485, 1, 1),
    ('庐山云雾茶', 2, '江西省九江市庐山风景区', '生于云雾缭绕的庐山，条索秀丽，有独特的豆花香。', 128.00, '盒', 800, 115.9860, 29.5522, 1, 1),
    ('鄱阳湖生态大米', 3, '江西省上饶市鄱阳县', '鄱阳湖流域灌溉，颗粒饱满，米饭清香柔软。', 5.80, '斤', 30000, 116.6737, 29.0031, 1, 1),
    ('崇义梯田大米', 3, '江西省赣州市崇义县', '高山梯田种植，一年一季，米质软糯，氨基酸含量高。', 6.50, '斤', 10000, 114.3072, 25.6818, 1, 1),
    ('广昌白莲', 4, '江西省抚州市广昌县', '中国白莲之乡，颗大粒圆，肉质粉糯，炖汤煮粥皆宜。', 36.00, '斤', 5000, 116.3257, 26.8423, 1, 1),
    ('泰和乌鸡蛋', 4, '江西省吉安市泰和县', '泰和乌鸡所产，蛋壳淡绿，营养价值高，滋补佳品。', 1.50, '个', 20000, 114.9092, 26.7900, 1, 1);

-- 演示数据：通知
DELETE FROM notice;
INSERT INTO notice (title, content) VALUES
    ('平台正式上线', '乡镇农产品信息发布平台正式上线！本平台为农户提供农产品发布、产地地图展示与数据看板服务，欢迎各村信息员注册使用。'),
    ('赣南脐橙采摘季开始', '当前正值赣南脐橙采摘季，各农户可登录后台发布最新产品与价格信息，标注产地经纬度后将在地图上直观展示，方便采购商对接。');

-- 预置管理员账号（admin / admin123，BCrypt 加密，幂等）
INSERT IGNORE INTO sys_user (username, password, real_name, role) VALUES
    ('admin', '$2a$10$mRAfTqGcasdxzmMGMCDDYeNc/92s7wYO7378EPbQoaJfA2CVFgTxq', '村委会管理员', 'ADMIN');

-- 说明：注册默认 role=USER（登录回前台首页）；后台管理用上方预置的 admin 账号登录。
