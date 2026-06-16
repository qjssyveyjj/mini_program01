-- 测试数据脚本
USE health_manager;

-- 插入测试用户
INSERT INTO users (openid, nickname, phone) VALUES
('oWx12345abc', '张三', '13800138000'),
('oWx67890def', '李四', '13900139000');

-- 插入健康数据
INSERT INTO health_data (user_id, timestamp, systolic, diastolic, heart_rate, blood_oxygen, weight, notes) VALUES
(1, '2026-06-15 08:00:00', 120, 80, 72, 98, 65.5, '晨检'),
(1, '2026-06-15 20:00:00', 122, 82, 70, 97, 65.3, '晚检'),
(2, '2026-06-15 09:00:00', 130, 85, 75, 96, 70.2, '晨检');
