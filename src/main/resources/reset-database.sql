DROP DATABASE IF EXISTS smart_health;
CREATE DATABASE smart_health;
USE smart_health;

-- Drop all tables in correct order (to handle foreign key constraints)
DROP TABLE IF EXISTS cycle_data;
DROP TABLE IF EXISTS food_log;
DROP TABLE IF EXISTS food;
DROP TABLE IF EXISTS activity_log;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS user_goals;
DROP TABLE IF EXISTS users;

-- Recreate tables with new schema
CREATE TABLE users (
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

-- Run schema to create tables
SOURCE src/main/resources/schema.sql;

-- Run sample data
SOURCE src/main/resources/sample-data.sql; 