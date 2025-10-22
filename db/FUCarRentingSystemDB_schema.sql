-- FUCarRentingSystemDB_schema.sql
-- Creates database and tables for Car Renting Management System
-- Assumes Microsoft SQL Server

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'FUCarRentingSystemDB')
BEGIN
    CREATE DATABASE FUCarRentingSystemDB;
END
GO

USE FUCarRentingSystemDB;
GO

-- Table: Account
CREATE TABLE Account (
    AccountID INT IDENTITY(1,1) PRIMARY KEY,
    AccountName NVARCHAR(100) NOT NULL,
    Role NVARCHAR(50) NOT NULL
);
GO

-- Table: CarProducer
CREATE TABLE CarProducer (
    ProducerID INT IDENTITY(1,1) PRIMARY KEY,
    ProducerName NVARCHAR(200) NOT NULL,
    Address NVARCHAR(300) NOT NULL,
    Country NVARCHAR(100) NOT NULL
);
GO

-- Table: Car
CREATE TABLE Car (
    CarID INT IDENTITY(1,1) PRIMARY KEY,
    CarName NVARCHAR(200) NOT NULL,
    CarModelYear INT NOT NULL,
    Color NVARCHAR(50) NOT NULL,
    Capacity INT NOT NULL,
    Description NVARCHAR(1000) NOT NULL,
    ImportDate DATE NOT NULL,
    ProducerID INT NOT NULL,
    RentPrice DECIMAL(18,2) NOT NULL,
    Status NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_Car_Producer FOREIGN KEY (ProducerID) REFERENCES CarProducer(ProducerID)
);
GO

-- Table: Customer
CREATE TABLE Customer (
    CustomerID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerName NVARCHAR(200) NOT NULL,
    Mobile NVARCHAR(20) NOT NULL,
    Birthday DATE NOT NULL,
    IdentityCard NVARCHAR(50) NOT NULL,
    LicenceNumber NVARCHAR(50) NOT NULL,
    LicenceDate DATE NOT NULL,
    Email NVARCHAR(200) NOT NULL,
    Password NVARCHAR(200) NOT NULL,
    AccountID INT NOT NULL,
    CONSTRAINT FK_Customer_Account FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);
GO

-- Table: CarRental
CREATE TABLE CarRental (
    CarRentalID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    CarID INT NOT NULL,
    PickupDate DATE NOT NULL,
    ReturnDate DATE NOT NULL,
    RentPrice DECIMAL(18,2) NOT NULL,
    Status NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_CarRental_Customer FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    CONSTRAINT FK_CarRental_Car FOREIGN KEY (CarID) REFERENCES Car(CarID),
    CONSTRAINT CHK_Pickup_Return CHECK (PickupDate < ReturnDate)
);
GO

-- Table: Review
CREATE TABLE Review (
    ReviewID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID INT NOT NULL,
    CarID INT NOT NULL,
    ReviewStar TINYINT NOT NULL CHECK (ReviewStar >= 1 AND ReviewStar <= 5),
    Comment NVARCHAR(1000) NOT NULL,
    CONSTRAINT FK_Review_Customer FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    CONSTRAINT FK_Review_Car FOREIGN KEY (CarID) REFERENCES Car(CarID)
);
GO

-- Sample data
INSERT INTO Account (AccountName, Role) VALUES
('alice', 'CUSTOMER'),
('bob', 'ADMIN');

INSERT INTO CarProducer (ProducerName, Address, Country) VALUES
('Toyota Motor Corporation', '1 Toyota-Cho, Toyota, Aichi, Japan', 'Japan'),
('Ford Motor Company', 'One American Road, Dearborn, MI', 'USA');

INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status) VALUES
('Toyota Camry', 2022, 'White', 5, 'Comfortable midsize sedan', '2023-01-15', 1, 45.00, 'AVAILABLE'),
('Ford Focus', 2021, 'Black', 5, 'Compact hatchback', '2022-06-10', 2, 35.00, 'AVAILABLE');

INSERT INTO Customer (CustomerName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, Email, Password, AccountID) VALUES
('Nguyen Van A', '+84901234567', '1990-05-20', 'ID123456', 'L-987654', '2010-07-15', 'a@example.com', 'passA', 1),
('Tran Thi B', '+84907654321', '1995-11-30', 'ID654321', 'L-123987', '2015-03-01', 'b@example.com', 'passB', 1);

INSERT INTO CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status) VALUES
(1, 1, '2025-11-01', '2025-11-10', 45.00, 'BOOKED'),
(2, 2, '2025-12-05', '2025-12-08', 35.00, 'COMPLETED');

INSERT INTO Review (CustomerID, CarID, ReviewStar, Comment) VALUES
(1, 1, 5, 'Excellent car, smooth ride'),
(2, 2, 4, 'Good value for money');

GO

-- Optional: grant basic permissions (if needed)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO [your_login_here];
