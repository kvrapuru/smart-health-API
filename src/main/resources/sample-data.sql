-- Insert sample users
INSERT INTO users (name, email, password, google_id, profile_picture, gender, date_of_birth, height, height_unit, weight_unit, created_at, updated_at)
VALUES
('Krishna', 'Krishna@gmail.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'krishna1234', NULL, 'Female', '1990-05-10 00:00:00', 165, 'CM', 'KG', NOW(), NOW()),
('Bob Johnson', 'bob@example.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'bob456', NULL, 'Male', '1985-08-20 00:00:00', 180, 'CM', 'KG', NOW(), NOW());

-- Insert sample activities
INSERT INTO activity (name, description, calories_burned_per_hour, category, intensity)
VALUES
('Running', 'Outdoor running', 600, 'Cardio', 'High'),
('Yoga', 'Morning yoga session', 200, 'Flexibility', 'Low');

-- Insert sample foods
INSERT INTO food (name, calories, protein, carbs, fat, serving_size, serving_unit)
VALUES
('Apple', 52, 0.3, 14, 0.2, '1 medium', 'piece'),
('Chicken Breast', 165, 31, 0, 3.6, '100', 'g');

-- Insert sample user goals
INSERT INTO user_goals (user_id, goal_type, target_value, start_date, end_date, description, status, completed_date)
VALUES
(1, 'WEIGHT', 65, '2024-06-01 00:00:00', '2024-12-31 00:00:00', 'Reach 65kg by end of year', 'ACTIVE', NULL),
(2, 'NUTRITION', 2000, '2024-06-01 00:00:00', '2024-12-31 00:00:00', 'Maintain 2000 kcal/day', 'ACTIVE', NULL);

-- Insert sample activity logs
INSERT INTO activity_log (user_id, activity_id, start_time, end_time, duration, calories_burned, notes)
VALUES
(1, 1, '2024-06-10 07:00:00', '2024-06-10 07:30:00', 30, 300, 'Morning run'),
(2, 2, '2024-06-10 08:00:00', '2024-06-10 08:45:00', 45, 150, 'Yoga session');

-- Insert sample food logs
INSERT INTO food_log (user_id, food_id, timestamp, quantity, meal_type, notes)
VALUES
(1, 1, '2024-06-10 08:00:00', 1, 'Breakfast', 'Had an apple'),
(2, 2, '2024-06-10 13:00:00', 1, 'Lunch', 'Chicken breast for lunch');

-- Insert sample cycle data
INSERT INTO cycle_data (user_id, start_date, end_date, phase, symptoms, mood, notes)
VALUES
(1, '2024-06-01', '2024-06-05', 'Menstrual', 'Cramps', 'Tired', 'Rested at home'),
(1, '2024-06-06', '2024-06-14', 'Follicular', 'Energetic', 'Happy', 'Felt good');