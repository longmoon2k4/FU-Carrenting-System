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

-- Idempotency & data quality: add UNIQUE constraints on natural keys to prevent duplicates
IF NOT EXISTS (
    SELECT 1 FROM sys.indexes WHERE name = 'UQ_Account_AccountName' AND object_id = OBJECT_ID('dbo.Account')
)
BEGIN
    ALTER TABLE dbo.Account ADD CONSTRAINT UQ_Account_AccountName UNIQUE (AccountName);
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes WHERE name = 'UQ_CarProducer_ProducerName' AND object_id = OBJECT_ID('dbo.CarProducer')
)
BEGIN
    ALTER TABLE dbo.CarProducer ADD CONSTRAINT UQ_CarProducer_ProducerName UNIQUE (ProducerName);
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes WHERE name = 'UQ_Customer_Email' AND object_id = OBJECT_ID('dbo.Customer')
)
BEGIN
    ALTER TABLE dbo.Customer ADD CONSTRAINT UQ_Customer_Email UNIQUE (Email);
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes WHERE name = 'UQ_Customer_IdentityCard' AND object_id = OBJECT_ID('dbo.Customer')
)
BEGIN
    ALTER TABLE dbo.Customer ADD CONSTRAINT UQ_Customer_IdentityCard UNIQUE (IdentityCard);
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes WHERE name = 'UQ_Customer_LicenceNumber' AND object_id = OBJECT_ID('dbo.Customer')
)
BEGIN
    ALTER TABLE dbo.Customer ADD CONSTRAINT UQ_Customer_LicenceNumber UNIQUE (LicenceNumber);
END
GO

-- For Cars, avoid duplicate seed rows for the same model-year and producer
IF NOT EXISTS (
    SELECT 1 FROM sys.indexes WHERE name = 'UQ_Car_Name_Year_Producer' AND object_id = OBJECT_ID('dbo.Car')
)
BEGIN
    ALTER TABLE dbo.Car ADD CONSTRAINT UQ_Car_Name_Year_Producer UNIQUE (CarName, CarModelYear, ProducerID);
END
GO

-- Sample data
IF NOT EXISTS (SELECT 1 FROM Account WHERE AccountName = 'alice')
    INSERT INTO Account (AccountName, Role) VALUES ('alice', 'CUSTOMER');
IF NOT EXISTS (SELECT 1 FROM Account WHERE AccountName = 'bob')
    INSERT INTO Account (AccountName, Role) VALUES ('bob', 'ADMIN');

IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Toyota Motor Corporation', '1 Toyota-Cho, Toyota, Aichi, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Ford Motor Company')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Ford Motor Company', 'One American Road, Dearborn, MI', 'USA');

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Toyota Camry', 2022, 'White', 5, 'Comfortable midsize sedan', '2023-01-15', ProducerID, 45.00, 'AVAILABLE'
    FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation';
IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Ford Focus' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Ford Motor Company'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Ford Focus', 2021, 'Black', 5, 'Compact hatchback', '2022-06-10', ProducerID, 35.00, 'AVAILABLE'
    FROM CarProducer WHERE ProducerName = 'Ford Motor Company';

IF NOT EXISTS (SELECT 1 FROM Customer WHERE Email = 'a@example.com')
    INSERT INTO Customer (CustomerName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, Email, Password, AccountID)
    VALUES ('Nguyen Van A', '+84901234567', '1990-05-20', 'ID123456', 'L-987654', '2010-07-15', 'a@example.com', 'passA',
            (SELECT AccountID FROM Account WHERE AccountName = 'alice'));
IF NOT EXISTS (SELECT 1 FROM Customer WHERE Email = 'b@example.com')
    INSERT INTO Customer (CustomerName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, Email, Password, AccountID)
    VALUES ('Tran Thi B', '+84907654321', '1995-11-30', 'ID654321', 'L-123987', '2015-03-01', 'b@example.com', 'passB',
            (SELECT AccountID FROM Account WHERE AccountName = 'alice'));

IF NOT EXISTS (
        SELECT 1 FROM CarRental cr
        WHERE cr.CustomerID = (SELECT CustomerID FROM Customer WHERE Email = 'a@example.com')
            AND cr.CarID = (SELECT CarID FROM Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022)
            AND cr.PickupDate = '2025-11-01'
)
BEGIN
        INSERT INTO CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
        SELECT (SELECT CustomerID FROM Customer WHERE Email = 'a@example.com'),
                     (SELECT CarID FROM Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022),
                     '2025-11-01','2025-11-10',45.00,'BOOKED';
END

IF NOT EXISTS (
        SELECT 1 FROM CarRental cr
        WHERE cr.CustomerID = (SELECT CustomerID FROM Customer WHERE Email = 'b@example.com')
            AND cr.CarID = (SELECT CarID FROM Car WHERE CarName = 'Ford Focus' AND CarModelYear = 2021)
            AND cr.PickupDate = '2025-12-05'
)
BEGIN
        INSERT INTO CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
        SELECT (SELECT CustomerID FROM Customer WHERE Email = 'b@example.com'),
                     (SELECT CarID FROM Car WHERE CarName = 'Ford Focus' AND CarModelYear = 2021),
                     '2025-12-05','2025-12-08',35.00,'COMPLETED';
END

IF NOT EXISTS (
        SELECT 1 FROM Review r
        WHERE r.CustomerID = (SELECT CustomerID FROM Customer WHERE Email = 'a@example.com')
            AND r.CarID = (SELECT CarID FROM Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022)
            AND r.Comment = 'Excellent car, smooth ride'
)
BEGIN
        INSERT INTO Review (CustomerID, CarID, ReviewStar, Comment)
        SELECT (SELECT CustomerID FROM Customer WHERE Email = 'a@example.com'),
                     (SELECT CarID FROM Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022),
                     5,'Excellent car, smooth ride';
END

IF NOT EXISTS (
        SELECT 1 FROM Review r
        WHERE r.CustomerID = (SELECT CustomerID FROM Customer WHERE Email = 'b@example.com')
            AND r.CarID = (SELECT CarID FROM Car WHERE CarName = 'Ford Focus' AND CarModelYear = 2021)
            AND r.Comment = 'Good value for money'
)
BEGIN
        INSERT INTO Review (CustomerID, CarID, ReviewStar, Comment)
        SELECT (SELECT CustomerID FROM Customer WHERE Email = 'b@example.com'),
                     (SELECT CarID FROM Car WHERE CarName = 'Ford Focus' AND CarModelYear = 2021),
                     4,'Good value for money';
END

GO

-- Optional: grant basic permissions (if needed)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO [your_login_here];
