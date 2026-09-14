-- 乡镇农产品信息发布平台 建表脚本
-- MySQL 8.x，字符集 utf8mb4

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

-- 初始分类数据（先清空再插入，保证脚本可重复执行）
TRUNCATE TABLE category;
INSERT INTO category (name, sort) VALUES
    ('脐橙', 1),
    ('茶叶', 2),
    ('大米', 3),
    ('其他', 4);

-- 预置管理员账号（admin / admin123，BCrypt 加密，幂等）
INSERT IGNORE INTO sys_user (username, password, real_name, role) VALUES
    ('admin', '$2a$10$mRAfTqGcasdxzmMGMCDDYeNc/92s7wYO7378EPbQoaJfA2CVFgTxq', '村委会管理员', 'ADMIN');

-- 说明：
-- 注册接口 POST /api/auth/register 默认注册为普通用户（role=USER），登录后回到前台首页。
-- 后台管理使用上方预置的管理员账号 admin 登录，或由管理员在数据库中提升权限。
