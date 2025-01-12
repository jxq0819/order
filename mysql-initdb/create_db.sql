CREATE DATABASE IF NOT EXISTS order_db;
USE order_db;

CREATE TABLE orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        origin_latitude DECIMAL(10, 6) NOT NULL,
                        origin_longitude DECIMAL(10, 6) NOT NULL,
                        destination_latitude DECIMAL(10, 6) NOT NULL,
                        destination_longitude DECIMAL(10, 6) NOT NULL,
                        distance INT NOT NULL,
                        status VARCHAR(50) NOT NULL
);