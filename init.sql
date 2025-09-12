CREATE DATABASE IF NOT EXISTS jwt_auth_db;
USE jwt_auth_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    action ENUM('READ', 'WRITE', 'DELETE', 'MANAGE', 'VIEW') NOT NULL,
    resource VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_resource (resource),
    INDEX idx_action (action)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT UNSIGNED,
    role_id BIGINT UNSIGNED,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_role (role_id)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT UNSIGNED,
    permission_id BIGINT UNSIGNED,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    INDEX idx_role (role_id),
    INDEX idx_permission (permission_id)
);

-- Insert default roles
INSERT INTO roles (role_name, description) VALUES
('ADMIN', 'System Administrator'),
('USER', 'Regular User'),
('MANAGER', 'Department Manager');

-- Insert default permissions
INSERT INTO permissions (permission_name, resource, action, description) VALUES
('USER_READ', 'USER', 'READ', 'Read user information'),
('USER_WRITE', 'USER', 'WRITE', 'Create and update users'),
('USER_DELETE', 'USER', 'DELETE', 'Delete users'),
('ROLE_MANAGE', 'ROLE', 'MANAGE', 'Manage roles and permissions'),
('REPORT_VIEW', 'REPORT', 'VIEW', 'View reports');

-- Mapping roles and permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), -- ADMIN có tất cả
(2, 1),                                -- USER chỉ đọc user
(3, 1), (3, 5);                         -- MANAGER đọc user + xem report

-- Insert sample users (password = "123456", bcrypt)
INSERT INTO users (username, email, password, full_name) VALUES
('admin', 'admin@example.com', '$2a$10$X5wFBtLrC/sSiVloTrg3BeBm0s2uBuiYnZ7z1vwqhLrm8DgfvZtsa', 'System Administrator'),
('user1', 'user1@example.com', '$2a$10$X5wFBtLrC/sSiVloTrg3BeBm0s2uBuiYnZ7z1vwqhLrm8DgfvZtsa', 'Regular User'),
('manager1', 'manager1@example.com', '$2a$10$X5wFBtLrC/sSiVloTrg3BeBm0s2uBuiYnZ7z1vwqhLrm8DgfvZtsa', 'Department Manager');

-- Gán roles cho users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- user1 -> USER
(3, 3); -- manager1 -> MANAGER
