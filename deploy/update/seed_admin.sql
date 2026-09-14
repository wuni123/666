-- 预置管理员账号（admin / admin123，BCrypt，幂等）
INSERT IGNORE INTO sys_user (username, password, real_name, role) VALUES
    ('admin', '$2a$10$mRAfTqGcasdxzmMGMCDDYeNc/92s7wYO7378EPbQoaJfA2CVFgTxq', '村委会管理员', 'ADMIN');
