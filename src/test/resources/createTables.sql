-- Drop tables

DROP TABLE IF EXISTS material;

-- Create tables

CREATE TABLE material (
	id INTEGER AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(50) NOT NULL
);