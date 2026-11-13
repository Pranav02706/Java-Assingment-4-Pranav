
CREATE DATABASE campus_events;

USE campus_events;

CREATE TABLE registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    register_number VARCHAR(30) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (register_number, event_name)
);
