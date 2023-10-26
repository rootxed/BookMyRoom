-- Création de la base de données
CREATE DATABASE IF NOT EXISTS bookmyroom;
USE bookmyroom;

-- Table Country
CREATE TABLE IF NOT EXISTS country (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
    );

-- Table City
CREATE TABLE IF NOT EXISTS city (
    id INT AUTO_INCREMENT PRIMARY KEY,
    country_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    FOREIGN KEY (country_id) REFERENCES country(id)
    );

-- Table Address
CREATE TABLE IF NOT EXISTS address (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city_id INT NOT NULL,
    address_line VARCHAR(255) NOT NULL,
    FOREIGN KEY (city_id) REFERENCES city(id)
    );

-- Table Building
CREATE TABLE IF NOT EXISTS building (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address_id INT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address(id)
    );

-- Table Hall
CREATE TABLE IF NOT EXISTS hall (
    id INT AUTO_INCREMENT PRIMARY KEY,
    building_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (building_id) REFERENCES building(id)
    );

-- Table Category
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
    );

-- Table HallCategory
CREATE TABLE IF NOT EXISTS hall_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_id INT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (hall_id) REFERENCES hall(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
    );

-- Table Permission
CREATE TABLE IF NOT EXISTS permission (
    id INT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(100) NOT NULL
    );

-- Table Role
CREATE TABLE IF NOT EXISTS role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
    );

-- Table RolePermissions
CREATE TABLE IF NOT EXISTS role_permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
    );

-- Table User
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    address_id INT NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    is_blocked BOOLEAN DEFAULT 0 NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (address_id) REFERENCES address(id)
    );

-- Table Booking
CREATE TABLE IF NOT EXISTS booking (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_id INT NOT NULL,
    user_id INT NOT NULL,
    date_time_in DATETIME NOT NULL,
    date_time_out DATETIME NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    is_canceled BOOLEAN DEFAULT 0 NOT NULL,
    FOREIGN KEY (hall_id) REFERENCES hall(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
    );

-- Table PaymentHistory
CREATE TABLE IF NOT EXISTS payment_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receiver_user_id INT NOT NULL,
    booking_id INT NOT NULL,
    payment_type ENUM('Cash', 'Bancontact', 'Bank Transfer') NOT NULL,
    time_stamp DATETIME NOT NULL,
    FOREIGN KEY (receiver_user_id) REFERENCES user(id),
    FOREIGN KEY (booking_id) REFERENCES booking(id)
    );

-- Table OpeningHours
CREATE TABLE IF NOT EXISTS opening_hours (
    id INT AUTO_INCREMENT PRIMARY KEY,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL
);

-- Table HallSchedule
CREATE TABLE IF NOT EXISTS hall_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_id INT NOT NULL,
    opening_hours_id INT NOT NULL,
    week_day TINYINT NOT NULL,
    beginning_date DATE NOT NULL,
    ending_date DATE,
    is_temporary BOOLEAN NOT NULL,
    FOREIGN KEY (hall_id) REFERENCES hall(id),
    FOREIGN KEY (opening_hours_id) REFERENCES opening_hours(id)
    );