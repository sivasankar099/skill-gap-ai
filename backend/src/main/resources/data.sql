-- Seed Target Roles
INSERT INTO target_roles (id, name, description, version) VALUES
(1, 'Backend Developer', 'Responsible for server-side logic, database interactions, API design, and system integration.', 1),
(2, 'Frontend Developer', 'Focuses on building responsive user interfaces, styling, state management, and user interaction.', 1),
(3, 'Full Stack Developer', 'Equipped to work on both client and server-side components, managing databases, business logic, and UI.', 1),
(4, 'Data Analyst', 'Specializes in processing data, generating statistics, creating visualizations, and finding business insights.', 1);

-- Seed Role Skills for Backend Developer (ID = 1)
INSERT INTO role_skills (role_id, skill_name, skill_type, importance) VALUES
(1, 'Java', 'TECHNICAL', 'HIGH'),
(1, 'Spring Boot', 'TECHNICAL', 'HIGH'),
(1, 'MySQL', 'TECHNICAL', 'HIGH'),
(1, 'REST APIs', 'TECHNICAL', 'MEDIUM'),
(1, 'Git', 'TECHNICAL', 'MEDIUM'),
(1, 'Docker', 'TECHNICAL', 'MEDIUM'),
(1, 'Unit Testing', 'TECHNICAL', 'MEDIUM'),
(1, 'Problem Solving', 'SOFT', 'HIGH'),
(1, 'Communication', 'SOFT', 'MEDIUM');

-- Seed Role Skills for Frontend Developer (ID = 2)
INSERT INTO role_skills (role_id, skill_name, skill_type, importance) VALUES
(2, 'JavaScript', 'TECHNICAL', 'HIGH'),
(2, 'React JS', 'TECHNICAL', 'HIGH'),
(2, 'HTML & CSS', 'TECHNICAL', 'HIGH'),
(2, 'Tailwind CSS', 'TECHNICAL', 'MEDIUM'),
(2, 'TypeScript', 'TECHNICAL', 'MEDIUM'),
(2, 'Git', 'TECHNICAL', 'MEDIUM'),
(2, 'Responsive Design', 'TECHNICAL', 'MEDIUM'),
(2, 'Problem Solving', 'SOFT', 'HIGH'),
(2, 'Teamwork', 'SOFT', 'MEDIUM');

-- Seed Role Skills for Full Stack Developer (ID = 3)
INSERT INTO role_skills (role_id, skill_name, skill_type, importance) VALUES
(3, 'Java', 'TECHNICAL', 'HIGH'),
(3, 'Spring Boot', 'TECHNICAL', 'HIGH'),
(3, 'React JS', 'TECHNICAL', 'HIGH'),
(3, 'MySQL', 'TECHNICAL', 'HIGH'),
(3, 'JavaScript', 'TECHNICAL', 'HIGH'),
(3, 'REST APIs', 'TECHNICAL', 'HIGH'),
(3, 'Git', 'TECHNICAL', 'MEDIUM'),
(3, 'Problem Solving', 'SOFT', 'HIGH'),
(3, 'Communication', 'SOFT', 'MEDIUM');

-- Seed Role Skills for Data Analyst (ID = 4)
INSERT INTO role_skills (role_id, skill_name, skill_type, importance) VALUES
(4, 'Python', 'TECHNICAL', 'HIGH'),
(4, 'SQL', 'TECHNICAL', 'HIGH'),
(4, 'Excel', 'TECHNICAL', 'HIGH'),
(4, 'Tableau', 'TECHNICAL', 'MEDIUM'),
(4, 'Power BI', 'TECHNICAL', 'MEDIUM'),
(4, 'Statistics', 'TECHNICAL', 'HIGH'),
(4, 'Data Visualization', 'TECHNICAL', 'HIGH'),
(4, 'Critical Thinking', 'SOFT', 'HIGH'),
(4, 'Presentation Skills', 'SOFT', 'MEDIUM');

-- Seed Learning Resources (Documentation, Videos, Practice Sites, Official Guides)
INSERT INTO learning_resources (title, description, url, resource_type, skill_name) VALUES
-- Java Resources
('Official Java Documentation', 'Core documentation for Java Platform Standard Edition by Oracle.', 'https://docs.oracle.com/en/java/', 'OFFICIAL', 'Java'),
('Java Programming Masterclass', 'Comprehensive video series for mastering Java fundamentals and OOP principles.', 'https://www.youtube.com/watch?v=A74TOX803D0', 'VIDEO', 'Java'),
('Java Coding Practice - HackerRank', 'Practice Java syntax, logic, and standard libraries on HackerRank.', 'https://www.hackerrank.com/domains/java', 'PRACTICE', 'Java'),

-- Spring Boot Resources
('Spring Boot Reference Documentation', 'Official reference guide covering deployment, configuration, and starters.', 'https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/', 'DOCUMENTATION', 'Spring Boot'),
('Spring Boot Tutorial for Beginners', 'Full course building RESTful APIs using Spring Boot 3.', 'https://www.youtube.com/watch?v=35EQXmHKZYs', 'VIDEO', 'Spring Boot'),

-- MySQL / SQL Resources
('MySQL Official Documentation', 'Standard reference manual for MySQL database administration and queries.', 'https://dev.mysql.com/doc/', 'DOCUMENTATION', 'MySQL'),
('SQL Exercises on HackerRank', 'Practice SQL SELECT queries, joins, aggregates, and subqueries.', 'https://www.hackerrank.com/domains/sql', 'PRACTICE', 'MySQL'),
('SQL Exercises on HackerRank (Data Analyst)', 'Practice SQL SELECT queries, joins, aggregates, and subqueries.', 'https://www.hackerrank.com/domains/sql', 'PRACTICE', 'SQL'),
('PostgreSQL Tutorial - Mode', 'Interactive tutorials for SQL querying and database optimization.', 'https://mode.com/sql-tutorial/', 'DOCUMENTATION', 'SQL'),

-- React JS / Frontend Resources
('React Official Docs (New)', 'Official React guides, tutorial, and component documentation.', 'https://react.dev', 'OFFICIAL', 'React JS'),
('React JS Crash Course', 'Step-by-step React project build including hooks and state management.', 'https://www.youtube.com/watch?v=w7ejDZ8IBW8', 'VIDEO', 'React JS'),
('JavaScript Web Development MDN', 'Standard reference for Mozilla Developer Network covering client-side JavaScript.', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript', 'DOCUMENTATION', 'JavaScript'),
('JavaScript Info Tutorial', 'Granular, deep tutorials on the JavaScript engine, event loop, and prototypes.', 'https://javascript.info', 'DOCUMENTATION', 'JavaScript'),
('LeetCode JavaScript Exercises', 'Complete JavaScript coding challenges, arrays, callbacks, and closures.', 'https://leetcode.com/problemset/javascript/', 'PRACTICE', 'JavaScript'),

-- REST APIs
('REST API Architectural Pattern', 'Explain REST design principles, methods, headers, and status codes.', 'https://developer.mozilla.org/en-US/docs/Glossary/REST', 'DOCUMENTATION', 'REST APIs'),

-- Python
('Python Documentation', 'Official Python tutorials, references, and libraries guide.', 'https://docs.python.org/3/', 'OFFICIAL', 'Python'),
('Python for Everybody Course', 'Learn Python variables, data structures, and APIs step by step.', 'https://www.youtube.com/watch?v=8DvywoWv6fI', 'VIDEO', 'Python'),

-- Docker
('Docker Reference Guide', 'Docker container creation, images, dockerfile syntax, and CLI reference.', 'https://docs.docker.com/', 'DOCUMENTATION', 'Docker'),

-- Tailwind CSS
('Tailwind CSS Docs', 'Utility-first framework documentation for rapid styling.', 'https://tailwindcss.com/docs', 'DOCUMENTATION', 'Tailwind CSS'),

-- Git
('Git Cheat Sheet & Guides', 'Pro Git book online and common branching commands.', 'https://git-scm.com/doc', 'DOCUMENTATION', 'Git'),

-- Soft Skills
('Effective Communication Skills', 'Official guide to verbal and non-verbal professional communication.', 'https://www.coursera.org/learn/communication-skills-for-engineers', 'OFFICIAL', 'Communication'),
('Structured Problem Solving Guide', 'Techniques for logic trees, problem dissection, and analytical decision making.', 'https://www.mckinsey.com/capabilities/operations/our-insights', 'DOCUMENTATION', 'Problem Solving');
