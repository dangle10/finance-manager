CREATE DATABASE IF NOT EXISTS finance_manager;
USE finance_manager;


CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);


CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    type ENUM('income', 'expense') NOT NULL,
    category VARCHAR(50),
    amount DOUBLE NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE budgets (
    budget_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    month CHAR(7) NOT NULL,           -- e.g. '2025-11'
    category VARCHAR(50),
    limit_amount DOUBLE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE goals (
    goal_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    goal_name VARCHAR(100) NOT NULL,
    target_amount DOUBLE NOT NULL,
    current_amount DOUBLE DEFAULT 0,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50) DEFAULT 'Đang thực hiện', -- Trạng thái mặc định
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


INSERT INTO users (username, password, email) VALUES
('admin', 'admin123', 'admin@example.com'),
('demo', 'demo123', 'demo@example.com');

INSERT INTO transactions (user_id, date, type, category, amount, note) VALUES
(1, '2025-11-01', 'income', 'Salary', 12000000, 'Monthly salary'),
(1, '2025-11-03', 'expense', 'Food', 50000, 'Lunch'),
(2, '2025-11-02', 'expense', 'Transport', 20000, 'Bus ticket');

INSERT INTO budgets (user_id, month, category, limit_amount) VALUES
(1, '2025-11', 'Food', 300000),
(2, '2025-11', 'Transport', 100000);

INSERT INTO goals (user_id, goal_name, target_amount, current_amount, start_date, end_date, status) VALUES
(1, 'Mua Macbook Pro', 45000000, 15000000, '2025-11-01', '2026-06-01', 'Đang thực hiện'),
(1, 'Du lịch Nhật Bản', 30000000, 5000000, '2025-12-15', '2026-08-20', 'Đang thực hiện'),
(1, 'Mua Giày chạy bộ', 2500000, 2500000, '2025-10-01', '2025-11-10', 'Hoàn thành');

