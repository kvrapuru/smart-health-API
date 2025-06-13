DROP DATABASE IF EXISTS smart_health;
CREATE DATABASE smart_health;
USE smart_health;

-- Drop all tables in correct order (child tables first)
DROP TABLE IF EXISTS activity_log;
DROP TABLE IF EXISTS food_log;
DROP TABLE IF EXISTS user_goals;
DROP TABLE IF EXISTS cycle_data;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS food;
DROP TABLE IF EXISTS users;

-- Run schema to create tables
SOURCE src/main/resources/schema.sql;

-- Run sample data
SOURCE src/main/resources/sample-data.sql; 