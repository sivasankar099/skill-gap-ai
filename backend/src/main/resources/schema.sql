-- Drop tables if they exist to allow clean recreations during development
DROP TABLE IF EXISTS learning_resources;
DROP TABLE IF EXISTS skill_analyses;
DROP TABLE IF EXISTS resumes;
DROP TABLE IF EXISTS role_skills;
DROP TABLE IF EXISTS target_roles;
DROP TABLE IF EXISTS users;

-- 1. Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Target Roles Table (Seeded Job Roles)
CREATE TABLE target_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Role Skills Table (Skills associated with each role)
CREATE TABLE role_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    skill_type VARCHAR(20) NOT NULL, -- 'TECHNICAL' or 'SOFT'
    importance VARCHAR(20) NOT NULL, -- 'HIGH' (weight 3), 'MEDIUM' (weight 2), 'LOW' (weight 1)
    FOREIGN KEY (role_id) REFERENCES target_roles(id) ON DELETE CASCADE,
    UNIQUE KEY idx_role_skill (role_id, skill_name)
);

-- 4. Resumes Table (Parsed text, raw binary discarded)
CREATE TABLE resumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    parsed_text LONGTEXT NOT NULL,
    parsed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Skill Analyses Table (Cached analysis, scores, roadmaps, explanations)
CREATE TABLE skill_analyses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_role_id BIGINT NOT NULL,
    skill_score DOUBLE NOT NULL,
    strong_skills JSON NOT NULL, -- Array of strings
    missing_skills JSON NOT NULL, -- Array of strings
    gap_explanation TEXT NOT NULL,
    roadmap JSON NOT NULL, -- Structured 4-week roadmap
    analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_role_id) REFERENCES target_roles(id) ON DELETE CASCADE
);

-- 6. Learning Resources Table (For semantic search / recommendation)
CREATE TABLE learning_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    url VARCHAR(512) NOT NULL,
    resource_type VARCHAR(50) NOT NULL, -- 'DOCUMENTATION', 'VIDEO', 'PRACTICE', 'OFFICIAL'
    skill_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
