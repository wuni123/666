-- 增量更新：产品表增加审核字段，存量数据默认已审核通过（幂等）
ALTER TABLE product ADD COLUMN audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0待审核 1通过 2驳回';
UPDATE product SET audit_status = 1;
