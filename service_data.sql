INSERT INTO `service` (service_id, name, category, address, images, phone, rating, review_count, business_hours, description, services_offered) VALUES
-- 9. 爱宠美容工作室 (grooming)
(9, '爱宠美容工作室', 'grooming', '朝阳区建国路88号', 
 '["https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", "https://images.unsplash.com/photo-1583337130417-3346a1be7dee"]', 
 '010-12345678', 4.8, 128, 
 '{"monday":"09:00-18:00","tuesday":"09:00-18:00","wednesday":"09:00-18:00","thursday":"09:00-18:00","friday":"09:00-18:00","saturday":"10:00-17:00","sunday":"10:00-17:00"}', 
 '这是一家专注宠物美容与护理的门店，环境干净整洁，店员对宠物很耐心，适合洗护、造型和日常护理。',
 '[{"name":"基础洗澡","price":88},{"name":"精致美容","price":158},{"name":"豪华SPA","price":288},{"name":"驱虫护理","price":68}]'),

-- 10. 萌宠乐园寄养中心 (boarding)
(10, '萌宠乐园寄养中心', 'boarding', '海淀区中关村大街123号', 
 '["https://images.unsplash.com/photo-1583337130417-3346a1be7dee", "https://images.unsplash.com/photo-1517849845537-4d257902454a"]', 
 '010-87654321', 4.9, 256, 
 '{"monday":"00:00-23:59","tuesday":"00:00-23:59","wednesday":"00:00-23:59","thursday":"00:00-23:59","friday":"00:00-23:59","saturday":"00:00-23:59","sunday":"00:00-23:59"}', 
 '提供专业的宠物寄养服务，24小时监控，专人照顾，让您放心出行。',
 '[{"name":"日间寄养","price":80},{"name":"长期寄养","price":200},{"name":"宠物训练","price":150},{"name":"健康监测","price":50}]'),

-- 11. 宠物之家医院 (vet)
(11, '宠物之家医院', 'vet', '西城区西单北大街45号', 
 '["https://images.unsplash.com/photo-1517849845537-4d257902454a", "https://images.unsplash.com/photo-1507149833265-60c372daea22"]', 
 '010-11223344', 4.7, 89, 
 '{"monday":"09:00-20:00","tuesday":"09:00-20:00","wednesday":"09:00-20:00","thursday":"09:00-20:00","friday":"09:00-20:00","saturday":"09:00-18:00","sunday":"09:00-18:00"}', 
 '专业宠物医院，拥有先进设备和经验丰富的兽医团队，提供全面的宠物医疗服务。',
 '[{"name":"疫苗接种","price":120},{"name":"常规体检","price":200},{"name":"外科手术","price":500},{"name":"牙科护理","price":150}]'),

-- 12. 毛孩子SPA会所 (grooming)
(12, '毛孩子SPA会所', 'grooming', '东城区王府井大街67号', 
 '["https://images.unsplash.com/photo-1507149833265-60c372daea22", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7"]', 
 '010-99887766', 4.6, 45, 
 '{"monday":"10:00-19:00","tuesday":"10:00-19:00","wednesday":"10:00-19:00","thursday":"10:00-19:00","friday":"10:00-19:00","saturday":"10:00-18:00","sunday":"10:00-18:00"}', 
 '专注于宠物SPA和美容，使用进口产品，让您的宠物享受顶级护理体验。',
 '[{"name":"芳香SPA","price":128},{"name":"泥浆浴","price":98},{"name":"毛发养护","price":68},{"name":"足部护理","price":48}]'),

-- 13. 安心宠物医院 (vet)
(13, '安心宠物医院', 'vet', '朝阳区望京街10号', 
 '["https://images.unsplash.com/photo-1583511655857-d19b40a7a54e", "https://images.unsplash.com/photo-1558788353-f76d92427f16"]', 
 '010-55443322', 4.9, 312, 
 '{"monday":"24小时","tuesday":"24小时","wednesday":"24小时","thursday":"24小时","friday":"24小时","saturday":"24小时","sunday":"24小时"}', 
 '24小时宠物医院，急诊随时接待，设备齐全，医生经验丰富。',
 '[{"name":"急诊救治","price":300},{"name":"住院护理","price":200},{"name":"影像检查","price":180},{"name":"实验室检测","price":100}]'),

-- 14. 遛狗达人服务 (walking)
(14, '遛狗达人服务', 'walking', '海淀区五道口', 
 '["https://images.unsplash.com/photo-1543466835-00a7907e9de1", "https://images.unsplash.com/photo-1545249390-6bdfa286032f"]', 
 '010-66778899', 4.8, 67, 
 '{"monday":"07:00-20:00","tuesday":"07:00-20:00","wednesday":"07:00-20:00","thursday":"07:00-20:00","friday":"07:00-20:00","saturday":"08:00-19:00","sunday":"08:00-19:00"}', 
 '专业遛狗服务，经验丰富的遛狗师，确保狗狗安全和快乐。',
 '[{"name":"日常遛狗","price":35},{"name":"社交遛狗","price":45},{"name":"训练遛狗","price":60},{"name":"定制路线","price":80}]'),

-- 15. 宠物救助站 (emergency)
(15, '宠物救助站', 'emergency', '通州区', 
 '["https://images.unsplash.com/photo-1541599540903-216a46ca1dc0", "https://images.unsplash.com/photo-1537151625747-768fd6aa2e5f"]', 
 '010-44332211', 4.5, 23, 
 '{"monday":"10:00-17:00","tuesday":"10:00-17:00","wednesday":"10:00-17:00","thursday":"10:00-17:00","friday":"10:00-17:00","saturday":"10:00-16:00","sunday":"关闭"}', 
 '流浪动物救助站，提供领养、救助服务，欢迎爱心人士参与。',
 '[{"name":"流浪动物救助","price":0},{"name":"领养服务","price":0},{"name":"医疗援助","price":0},{"name":"绝育计划","price":0}]')