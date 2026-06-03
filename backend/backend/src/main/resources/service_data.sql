-- INSERT INTO `service` (service_id, name, category, address, images, phone, rating, review_count, business_hours, description, services_offered) VALUES
INSERT IGNORE INTO `service` (service_id, name, category, address, images, phone, rating, review_count, business_hours, description, services_offered) VALUES
-- 1. 安心宠物托管中心 (sitting)
(1, '安心宠物托管中心', 'sitting', '朝阳区朝阳北路101号',
 '["https://images.unsplash.com/photo-1583337130417-3346a1be7dee", "https://images.unsplash.com/photo-1517849845537-4d257902454a"]',
 '010-12345678', 4.9, 156,
 '{"monday":"08:00-20:00","tuesday":"08:00-20:00","wednesday":"08:00-20:00","thursday":"08:00-20:00","friday":"08:00-20:00","saturday":"09:00-19:00","sunday":"09:00-19:00"}',
 '专业宠物托管服务，一对一照顾，每日发送视频报告，让您出差旅行无后顾之忧。',
 '[{"name":"日间托管","price":60},{"name":"全天托管","price":120},{"name":"豪华套房","price":200},{"name":"VIP专人托管","price":300}]'),

-- 2. 汪星人托管乐园 (sitting)
(2, '汪星人托管乐园', 'sitting', '海淀区万泉河路66号',
 '["https://images.unsplash.com/photo-1543466835-00a7907e9de1", "https://images.unsplash.com/photo-1545249390-6bdfa286032f"]',
 '010-23456789', 4.8, 98,
 '{"monday":"07:00-21:00","tuesday":"07:00-21:00","wednesday":"07:00-21:00","thursday":"07:00-21:00","friday":"07:00-21:00","saturday":"08:00-20:00","sunday":"08:00-20:00"}',
 '大型活动场地，每日定时遛狗、喂食，专业团队照看，狗狗可以尽情玩耍。',
 '[{"name":"基础托管","price":80},{"name":"加长活动托管","price":130},{"name":"训练托管","price":180},{"name":"接送服务","price":40}]'),

-- 3. 萌宠护理服务站 (sitting)
(3, '萌宠护理服务站', 'sitting', '西城区西直门南大街22号',
 '["https://images.unsplash.com/photo-1541599540903-216a46ca1dc0", "https://images.unsplash.com/photo-1537151625747-768fd6aa2e5f"]',
 '010-34567890', 4.7, 67,
 '{"monday":"09:00-18:00","tuesday":"09:00-18:00","wednesday":"09:00-18:00","thursday":"09:00-18:00","friday":"09:00-18:00","saturday":"10:00-17:00","sunday":"10:00-17:00"}',
 '家庭式托管服务，温暖舒适的环境，定期向主人汇报宠物状态。',
 '[{"name":"日常托管","price":70},{"name":"夜间托管","price":90},{"name":"周末托管","price":110},{"name":"药物护理","price":50}]'),

-- 4. 柯基美容造型馆 (grooming)
(4, '柯基美容造型馆', 'grooming', '东城区东直门南大街9号',
 '["https://images.unsplash.com/photo-1507149833265-60c372daea22", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7"]',
 '010-45678901', 4.9, 203,
 '{"monday":"09:00-19:00","tuesday":"09:00-19:00","wednesday":"09:00-19:00","thursday":"09:00-19:00","friday":"09:00-19:00","saturday":"09:00-18:00","sunday":"09:00-18:00"}',
 '专为柯基设计的美容造型，也接待其他犬种，创意造型深受欢迎。',
 '[{"name":"基础洗护","price":98},{"name":"创意造型","price":188},{"name":"SPA护理","price":268},{"name":"指甲修剪","price":38}]'),

-- 5. 宠物急救救援队 (emergency)
(5, '宠物急救救援队', 'emergency', '朝阳区东三环中路88号',
 '["https://images.unsplash.com/photo-1558788353-f76d92427f16", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e"]',
 '010-56789012', 4.9, 45,
 '{"monday":"24小时","tuesday":"24小时","wednesday":"24小时","thursday":"24小时","friday":"24小时","saturday":"24小时","sunday":"24小时"}',
 '24小时宠物急救服务，专业救援车辆，快速响应各类宠物紧急情况。',
 '[{"name":"紧急出诊","price":500},{"name":"外伤处理","price":200},{"name":"中毒急救","price":350},{"name":"夜间急诊","price":400}]'),

-- 6. 宠物家庭医师 (vet)
(6, '宠物家庭医师', 'vet', '丰台区方庄路15号',
 '["https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e"]',
 '010-67890123', 4.8, 134,
 '{"monday":"09:00-20:00","tuesday":"09:00-20:00","wednesday":"09:00-20:00","thursday":"09:00-20:00","friday":"09:00-20:00","saturday":"09:00-18:00","sunday":"09:00-18:00"}',
 '家庭式宠物诊所，温馨的诊疗环境，耐心细致的医疗服务。',
 '[{"name":"基础体检","price":150},{"name":"疫苗接种","price":120},{"name":"皮肤病治疗","price":180},{"name":"B超检查","price":280}]'),

-- 7. 专业遛狗管家 (walking)
(7, '专业遛狗管家', 'walking', '朝阳区奥林匹克公园附近',
 '["https://images.unsplash.com/photo-1543466835-00a7907e9de1", "https://images.unsplash.com/photo-1517849845537-4d257902454a"]',
 '010-78901234', 4.7, 89,
 '{"monday":"06:30-21:00","tuesday":"06:30-21:00","wednesday":"06:30-21:00","thursday":"06:30-21:00","friday":"06:30-21:00","saturday":"07:00-20:00","sunday":"07:00-20:00"}',
 '资深遛狗师团队，提供1对1个性化遛狗服务，可选择路线时长。',
 '[{"name":"标准遛狗","price":40},{"name":"延长遛狗","price":60},{"name":"公园社交","price":55},{"name":"跑步陪练","price":70}]'),

-- 8. 喵星人寄养公寓 (boarding)
(8, '喵星人寄养公寓', 'boarding', '海淀区上地信息路28号',
 '["https://images.unsplash.com/photo-1495360010541-f48722b34f7d", "https://images.unsplash.com/photo-1541599540903-216a46ca1dc0"]',
 '010-89012345', 4.9, 178,
 '{"monday":"08:00-22:00","tuesday":"08:00-22:00","wednesday":"08:00-22:00","thursday":"08:00-22:00","friday":"08:00-22:00","saturday":"08:00-22:00","sunday":"08:00-22:00"}',
 '猫咪专属寄养公寓，多层猫爬架，独立空间，每天清洁消毒。',
 '[{"name":"标准寄养","price":100},{"name":"豪华寄养","price":180},{"name":"猫咪陪玩","price":50},{"name":"毛发梳理","price":40}]'),

-- 9. 爱宠美容工作室 (grooming)
(9, '爱宠美容工作室', 'grooming', '朝阳区建国路88号',
 '["https://images.unsplash.com/photo-1516734212186-a967f81ad0d7", "https://images.unsplash.com/photo-1583337130417-3346a1be7dee"]',
 '010-12345678', 4.8, 128,
 '{"monday":"09:00-18:00","tuesday":"09:00-18:00","wednesday":"09:00-18:00","thursday":"09:00-18:00","friday":"09:00-18:00","saturday":"10:00-17:00","sunday":"10:00-17:00"}',
 '这是一家专注宠物美容与护理的门店，环境干净整洁，店员对宠物很耐心，适合洗护、造型和日常护理。',
 '[{"name":"基础洗澡","price":88},{"name":"精致美容","price":158},{"name":"豪华SPA","price":288},{"name":"驱虫护理","price":68}]'),

-- 10. 萌宠乐园寄养中心 (boarding)
(10, '萌宠乐园寄养中心', 'boarding', '海淀区中关村大街123号',
 '["https://images.unsplash.com/photo-1503256207526-0d5d80fa2f47", "https://images.unsplash.com/photo-1517849845537-4d257902454a"]',
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
 '["https://images.unsplash.com/photo-1587300003388-59208cc962cb", "https://images.unsplash.com/photo-1558788353-f76d92427f16"]',
 '010-55443322', 4.9, 312,
 '{"monday":"24小时","tuesday":"24小时","wednesday":"24小时","thursday":"24小时","friday":"24小时","saturday":"24小时","sunday":"24小时"}',
 '24小时宠物医院，急诊随时接待，设备齐全，医生经验丰富。',
 '[{"name":"急诊救治","price":300},{"name":"住院护理","price":200},{"name":"影像检查","price":180},{"name":"实验室检测","price":100}]'),

-- 14. 遛狗达人服务 (walking)
(14, '遛狗达人服务', 'walking', '海淀区五道口',
 '["https://plus.unsplash.com/premium_photo-1694819488591-a43907d1c5cc", "https://images.unsplash.com/photo-1545249390-6bdfa286032f"]',
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
 '[{"name":"流浪动物救助","price":0},{"name":"领养服务","price":0},{"name":"医疗援助","price":0},{"name":"绝育计划","price":0}]'),

-- 16. 柴犬主题咖啡馆 (grooming)
(16, '柴犬主题咖啡馆', 'grooming', '朝阳区三里屯',
 '["https://images.unsplash.com/photo-1583511655857-d19b40a7a54e", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba"]',
 '010-99887766', 4.9, 189,
 '{"monday":"11:00-20:00","tuesday":"11:00-20:00","wednesday":"11:00-20:00","thursday":"11:00-20:00","friday":"11:00-21:00","saturday":"10:00-21:00","sunday":"10:00-20:00"}',
 '可以和可爱的柴犬互动的咖啡馆，提供饮品和甜点，宠物友好。',
 '[{"name":"饮品","price":28},{"name":"甜点","price":38},{"name":"柴犬互动","price":58},{"name":"周边商品","price":68}]');