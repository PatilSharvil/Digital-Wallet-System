-- Create the database
CREATE DATABASE IF NOT EXISTS digital_wallet_db;
USE digital_wallet_db;
drop database digital_wallet_db;
-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(64) NOT NULL,  -- For SHA-256 hashed passwords
    balance DECIMAL(10,2) DEFAULT 0.00,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table
-- Create the transactions table with the correct structure
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Services table
-- Create services table if it doesn't exist

CREATE TABLE IF NOT EXISTS services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(100) NOT NULL,
    service_provider VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_service (service_name, service_provider)
);

-- Insert some sample services
INSERT INTO services (service_name, service_provider, description) VALUES
('Electricity', 'State Power Corp', 'Electricity bill payment'),
('Electricity', 'City Power Ltd', 'Electricity bill payment'),
('Water', 'Municipal Water Board', 'Water bill payment'),
('Gas', 'City Gas Corp', 'Gas bill payment'),
('Internet', 'Broadband Corp', 'Internet bill payment'),
('Internet', 'FiberNet Ltd', 'Internet bill payment'),
('Mobile', 'TelecomOne', 'Mobile bill payment'),
('Mobile', 'CellularPlus', 'Mobile bill payment'),
('DTH TV', 'SatelliteTV Plus', 'DTH TV bill payment'),
('DTH TV', 'DigitalView', 'DTH TV bill payment');

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    service_id INT,
    amount DECIMAL(10,2) NOT NULL,
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- Fraud logs table
CREATE TABLE IF NOT EXISTS fraud_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    issue VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, name, email, password, role) 
VALUES ('admin', 'Admin', 'admin@digitalwallet.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN');
