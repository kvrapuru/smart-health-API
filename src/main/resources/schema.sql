-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    profile_picture VARCHAR(255),
    gender VARCHAR(50),
    date_of_birth DATETIME,
    height DOUBLE,
    height_unit VARCHAR(10),
    weight_unit VARCHAR(10),
    created_at DATETIME,
    updated_at DATETIME
);

-- User Goals table
CREATE TABLE IF NOT EXISTS user_goals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goal_type VARCHAR(50) NOT NULL,
    target_value DOUBLE NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    completed_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Activity table
CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description TEXT,
    calories_burned_per_hour DOUBLE,
    category VARCHAR(50),
    intensity VARCHAR(50)
);

-- Activity Log table
CREATE TABLE IF NOT EXISTS activity_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    activity_id BIGINT,
    start_time DATETIME,
    end_time DATETIME,
    duration DOUBLE NOT NULL,
    calories_burned DOUBLE,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE SET NULL
);

-- Food table
CREATE TABLE IF NOT EXISTS food (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    calories DOUBLE,
    protein DOUBLE,
    carbs DOUBLE,
    fat DOUBLE,
    serving_size VARCHAR(50),
    serving_unit VARCHAR(20)
);

-- Food Log table
CREATE TABLE IF NOT EXISTS food_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    food_id BIGINT,
    timestamp DATETIME,
    quantity DOUBLE,
    meal_type VARCHAR(50),
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES food(id) ON DELETE SET NULL
);

-- Cycle Data table
CREATE TABLE IF NOT EXISTS cycle_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    start_date DATE,
    end_date DATE,
    phase VARCHAR(50),
    symptoms TEXT,
    mood VARCHAR(50),
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Weight Log table
CREATE TABLE IF NOT EXISTS weight_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    weight DOUBLE NOT NULL,
    target_weight DOUBLE,
    timestamp DATETIME NOT NULL,
    unit VARCHAR(10) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Water Log table
CREATE TABLE IF NOT EXISTS water_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    timestamp DATETIME NOT NULL,
    unit VARCHAR(10) NOT NULL DEFAULT 'ML',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Steps Log table
CREATE TABLE IF NOT EXISTS steps_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    steps INTEGER NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
); 