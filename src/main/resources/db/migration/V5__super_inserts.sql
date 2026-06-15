-- ==========================================================
-- V5: SUPER SCRIPT DE DADOS - CARE PLUS (COMPLETO)
-- ==========================================================

-- 1. ALIMENTOS (foods)
INSERT IGNORE INTO `foods` (`id`, `calories_per100g`, `carbs`, `fats`, `fiber`, `name`, `proteins`, `serving_size`, `serving_unit`, `sodium`, `sugar`) VALUES 
(4, 52, 14, 0.2, 2.4, 'Maçã', 0.3, 100, 'g', 1, 10),
(5, 89, 23, 0.3, 2.6, 'Banana Nanica', 1.1, 100, 'g', 1, 12),
(6, 208, 0, 14, 0, 'Salmão Grelhado', 20, 100, 'g', 59, 0),
(7, 45, 11, 0.1, 1.4, 'Laranja', 0.9, 100, 'g', 0, 9),
(8, 116, 20, 0.1, 4.1, 'Feijão Carioca', 4.8, 100, 'g', 2, 0.5),
(9, 30, 6, 0.3, 2, 'Brócolis Cozido', 2.5, 100, 'g', 33, 1.5);

-- 2. REFEIÇÕES (meals)
INSERT IGNORE INTO `meals` (`id`, `name`, `total_calories`, `total_carbs`, `total_fats`, `total_proteins`, `patient_id`) VALUES 
(1, 'Café da Manhã Fit', 250, 45, 5, 10, 1),
(2, 'Almoço Proteico', 550, 60, 15, 45, 1),
(3, 'Jantar Leve', 300, 20, 8, 30, 2);

-- 3. ITENS DE REFEIÇÃO (meal_items)
INSERT IGNORE INTO `meal_items` (`id`, `quantity`, `food_id`, `meal_id`) VALUES 
(1, 150, 1, 2),
(2, 200, 3, 2),
(3, 100, 8, 2),
(4, 120, 5, 1);

-- 4. PLANOS DE DIETA (diet_plans)
INSERT IGNORE INTO `diet_plans` (`id`, `description`, `end_date`, `goal`, `name`, `start_date`, `patient_id`) VALUES 
(1, 'Plano focado em hipertrofia e alta ingestão de proteínas', '2026-12-31', 'Ganho de Massa', 'Dieta Bulk 2026', '2026-01-01', 1),
(2, 'Redução calórica para perda de gordura mantendo saciedade', '2026-06-30', 'Emagrecimento', 'Dieta Low Carb', '2026-04-01', 2);

-- 5. REFEIÇÕES PLANEJADAS (planned_meals)
INSERT IGNORE INTO `planned_meals` (`id`, `day_of_week`, `meal_time`, `diet_plan_id`, `meal_id`) VALUES 
(1, 'MONDAY', '08:00:00', 1, 1),
(2, 'MONDAY', '12:30:00', 1, 2),
(3, 'TUESDAY', '19:00:00', 2, 3);

-- 6. LOGS DE COMIDA (food_logs)
INSERT IGNORE INTO `food_logs` (`id`, `consumption_date`, `notes`, `quantity`, `food_id`, `patient_id`) VALUES 
(1, CURDATE(), 'Consumido após o treino', 150, 3, 1),
(2, CURDATE(), 'Lanche da tarde', 100, 4, 1);
