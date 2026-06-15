-- ==========================================================
-- V5 - CREATE REMAINING TABLES
-- ==========================================================

CREATE TABLE IF NOT EXISTS foods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    calories_per100g DOUBLE,
    carbs DOUBLE,
    fats DOUBLE,
    fiber DOUBLE,
    name VARCHAR(255) NOT NULL,
    proteins DOUBLE,
    serving_size DOUBLE,
    serving_unit VARCHAR(50),
    sodium DOUBLE,
    sugar DOUBLE
);

CREATE TABLE IF NOT EXISTS meals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    total_calories DOUBLE,
    total_carbs DOUBLE,
    total_fats DOUBLE,
    total_proteins DOUBLE,
    patient_id BIGINT
);

CREATE TABLE IF NOT EXISTS meal_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantity DOUBLE,
    food_id BIGINT,
    meal_id BIGINT,

    CONSTRAINT fk_mealitem_food
        FOREIGN KEY (food_id)
        REFERENCES foods(id),

    CONSTRAINT fk_mealitem_meal
        FOREIGN KEY (meal_id)
        REFERENCES meals(id)
);

CREATE TABLE IF NOT EXISTS diet_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description TEXT,
    end_date DATE,
    goal VARCHAR(255),
    name VARCHAR(255),
    start_date DATE,
    patient_id BIGINT
);

CREATE TABLE IF NOT EXISTS planned_meals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    day_of_week VARCHAR(20),
    meal_time TIME,
    diet_plan_id BIGINT,
    meal_id BIGINT,

    CONSTRAINT fk_planned_diet
        FOREIGN KEY (diet_plan_id)
        REFERENCES diet_plans(id),

    CONSTRAINT fk_planned_meal
        FOREIGN KEY (meal_id)
        REFERENCES meals(id)
);

CREATE TABLE IF NOT EXISTS food_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    consumption_date DATE,
    notes TEXT,
    quantity DOUBLE,
    food_id BIGINT,
    patient_id BIGINT,

    CONSTRAINT fk_foodlog_food
        FOREIGN KEY (food_id)
        REFERENCES foods(id)
);