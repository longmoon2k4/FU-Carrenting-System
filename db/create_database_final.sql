-- create_database_final.sql
-- Consolidated, idempotent SQL to create the FUCarRentingSystemDB schema and seed data.
-- Target: Microsoft SQL Server (T-SQL)
-- IMPORTANT: Review and backup your environment before running. Run on a safe environment first.

SET NOCOUNT ON;

-- 1) Create database if missing
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'FUCarRentingSystemDB')
BEGIN
    PRINT 'Creating database FUCarRentingSystemDB...';
    CREATE DATABASE FUCarRentingSystemDB;
END
GO

USE FUCarRentingSystemDB;
GO

-- 2) Create tables (idempotent: check existence before create)

IF OBJECT_ID('dbo.Account', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Account (
        AccountID INT IDENTITY(1,1) PRIMARY KEY,
        AccountName NVARCHAR(100) NOT NULL,
        Role NVARCHAR(50) NOT NULL
    );
END
GO

IF OBJECT_ID('dbo.CarProducer', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.CarProducer (
        ProducerID INT IDENTITY(1,1) PRIMARY KEY,
        ProducerName NVARCHAR(200) NOT NULL,
        Address NVARCHAR(300) NOT NULL,
        Country NVARCHAR(100) NOT NULL
    );
END
GO

IF OBJECT_ID('dbo.Car', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Car (
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
        ImageUrl NVARCHAR(500) NULL,
        CONSTRAINT FK_Car_Producer FOREIGN KEY (ProducerID) REFERENCES dbo.CarProducer(ProducerID)
    );
END
GO

IF OBJECT_ID('dbo.Customer', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Customer (
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
        CONSTRAINT FK_Customer_Account FOREIGN KEY (AccountID) REFERENCES dbo.Account(AccountID)
    );
END
GO

IF OBJECT_ID('dbo.CarRental', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.CarRental (
        CarRentalID INT IDENTITY(1,1) PRIMARY KEY,
        CustomerID INT NOT NULL,
        CarID INT NOT NULL,
        PickupDate DATE NOT NULL,
        ReturnDate DATE NOT NULL,
        RentPrice DECIMAL(18,2) NOT NULL,
        Status NVARCHAR(50) NOT NULL,
        CONSTRAINT FK_CarRental_Customer FOREIGN KEY (CustomerID) REFERENCES dbo.Customer(CustomerID),
        CONSTRAINT FK_CarRental_Car FOREIGN KEY (CarID) REFERENCES dbo.Car(CarID),
        CONSTRAINT CHK_Pickup_Return CHECK (PickupDate < ReturnDate)
    );
END
GO

IF OBJECT_ID('dbo.Review', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Review (
        ReviewID INT IDENTITY(1,1) PRIMARY KEY,
        CustomerID INT NOT NULL,
        CarID INT NOT NULL,
        ReviewStar TINYINT NOT NULL CHECK (ReviewStar >= 1 AND ReviewStar <= 5),
        Comment NVARCHAR(1000) NOT NULL,
        CONSTRAINT FK_Review_Customer FOREIGN KEY (CustomerID) REFERENCES dbo.Customer(CustomerID),
        CONSTRAINT FK_Review_Car FOREIGN KEY (CarID) REFERENCES dbo.Car(CarID)
    );
END
GO

-- 3) Add unique constraints if missing (idempotent checks)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Account_AccountName' AND object_id = OBJECT_ID('dbo.Account'))
BEGIN
    ALTER TABLE dbo.Account ADD CONSTRAINT UQ_Account_AccountName UNIQUE (AccountName);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_CarProducer_ProducerName' AND object_id = OBJECT_ID('dbo.CarProducer'))
BEGIN
    ALTER TABLE dbo.CarProducer ADD CONSTRAINT UQ_CarProducer_ProducerName UNIQUE (ProducerName);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Customer_Email' AND object_id = OBJECT_ID('dbo.Customer'))
BEGIN
    ALTER TABLE dbo.Customer ADD CONSTRAINT UQ_Customer_Email UNIQUE (Email);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Customer_IdentityCard' AND object_id = OBJECT_ID('dbo.Customer'))
BEGIN
    ALTER TABLE dbo.Customer ADD CONSTRAINT UQ_Customer_IdentityCard UNIQUE (IdentityCard);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Customer_LicenceNumber' AND object_id = OBJECT_ID('dbo.Customer'))
BEGIN
    ALTER TABLE dbo.Customer ADD CONSTRAINT UQ_Customer_LicenceNumber UNIQUE (LicenceNumber);
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Car_Name_Year_Producer' AND object_id = OBJECT_ID('dbo.Car'))
BEGIN
    ALTER TABLE dbo.Car ADD CONSTRAINT UQ_Car_Name_Year_Producer UNIQUE (CarName, CarModelYear, ProducerID);
END
GO

-- 4) Seed base data (idempotent inserts)
PRINT 'Seeding base data...';

-- Accounts
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE AccountName = 'alice')
    INSERT INTO dbo.Account (AccountName, Role) VALUES ('alice', 'CUSTOMER');
IF NOT EXISTS (SELECT 1 FROM dbo.Account WHERE AccountName = 'bob')
    INSERT INTO dbo.Account (AccountName, Role) VALUES ('bob', 'ADMIN');

-- Car producers
IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = 'Toyota Motor Corporation')
    INSERT INTO dbo.CarProducer (ProducerName, Address, Country) VALUES ('Toyota Motor Corporation', '1 Toyota-Cho, Toyota, Aichi, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = 'Ford Motor Company')
    INSERT INTO dbo.CarProducer (ProducerName, Address, Country) VALUES ('Ford Motor Company', 'One American Road, Dearborn, MI', 'USA');

-- More producers (from seed_more_cars)
DECLARE @producers TABLE (ProducerName NVARCHAR(200));
INSERT INTO @producers (ProducerName)
VALUES ('Honda Motor Co., Ltd.'), ('Hyundai Motor Company'), ('Kia Corporation'), ('Nissan Motor Co., Ltd.'), ('Chevrolet'),
       ('Mazda Motor Corporation'), ('Subaru Corporation'), ('BMW'), ('Mercedes-Benz'), ('Suzuki'), ('Mitsubishi'), ('Volkswagen'), ('Audi');

DECLARE @p NVARCHAR(200);
DECLARE prod_cursor CURSOR FOR SELECT ProducerName FROM @producers;
OPEN prod_cursor;
FETCH NEXT FROM prod_cursor INTO @p;
WHILE @@FETCH_STATUS = 0
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.CarProducer WHERE ProducerName = @p)
        INSERT INTO dbo.CarProducer (ProducerName, Address, Country) VALUES (@p, @p + ' address', 'Unknown');
    FETCH NEXT FROM prod_cursor INTO @p;
END
CLOSE prod_cursor; DEALLOCATE prod_cursor;

-- Cars (some example seed rows; use the same checks as existing seed files)
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022 AND ProducerID = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Toyota Motor Corporation'))
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status, ImageUrl)
    SELECT 'Toyota Camry', 2022, 'White', 5, 'Comfortable midsize sedan', '2023-01-15', ProducerID, 45.00, 'AVAILABLE', '/img/cars/toyota-camry.svg'
    FROM dbo.CarProducer WHERE ProducerName = 'Toyota Motor Corporation';

IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Ford Focus' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Ford Motor Company'))
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status, ImageUrl)
    SELECT 'Ford Focus', 2021, 'Black', 5, 'Compact hatchback', '2022-06-10', ProducerID, 35.00, 'AVAILABLE', '/img/cars/ford-focus.svg'
    FROM dbo.CarProducer WHERE ProducerName = 'Ford Motor Company';

-- Add a few more cars from seed_more_cars (abbreviated for brevity)
IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Honda Civic' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.'))
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status, ImageUrl)
    SELECT 'Honda Civic', 2020, 'Blue', 5, 'Reliable compact sedan', '2022-03-10', ProducerID, 38.00, 'AVAILABLE', '/img/cars/honda-civic.svg' FROM dbo.CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.';

IF NOT EXISTS (SELECT 1 FROM dbo.Car WHERE CarName = 'Toyota Corolla' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM dbo.CarProducer WHERE ProducerName = 'Toyota Motor Corporation'))
    INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status, ImageUrl)
    SELECT 'Toyota Corolla', 2020, 'Silver', 5, 'Popular compact sedan', '2022-01-20', ProducerID, 36.00, 'AVAILABLE', '/img/cars/toyota-corolla.svg' FROM dbo.CarProducer WHERE ProducerName = 'Toyota Motor Corporation';

-- Customers (example)
IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE Email = 'a@example.com')
    INSERT INTO dbo.Customer (CustomerName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, Email, Password, AccountID)
    VALUES ('Nguyen Van A', '+84901234567', '1990-05-20', 'ID123456', 'L-987654', '2010-07-15', 'a@example.com', 'passA',
            (SELECT AccountID FROM dbo.Account WHERE AccountName = 'alice'));
IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE Email = 'b@example.com')
    INSERT INTO dbo.Customer (CustomerName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, Email, Password, AccountID)
    VALUES ('Tran Thi B', '+84907654321', '1995-11-30', 'ID654321', 'L-123987', '2015-03-01', 'b@example.com', 'passB',
            (SELECT AccountID FROM dbo.Account WHERE AccountName = 'bob'));

-- Rentals and reviews (idempotent)
IF NOT EXISTS (
        SELECT 1 FROM dbo.CarRental cr
        WHERE cr.CustomerID = (SELECT CustomerID FROM dbo.Customer WHERE Email = 'a@example.com')
            AND cr.CarID = (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022)
            AND cr.PickupDate = '2025-11-01'
)
BEGIN
        INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
        SELECT (SELECT CustomerID FROM dbo.Customer WHERE Email = 'a@example.com'),
                     (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022),
                     '2025-11-01','2025-11-10',45.00,'BOOKED';
END

IF NOT EXISTS (
        SELECT 1 FROM dbo.Review r
        WHERE r.CustomerID = (SELECT CustomerID FROM dbo.Customer WHERE Email = 'a@example.com')
            AND r.CarID = (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022)
            AND r.Comment = 'Excellent car, smooth ride'
)
BEGIN
        INSERT INTO dbo.Review (CustomerID, CarID, ReviewStar, Comment)
        SELECT (SELECT CustomerID FROM dbo.Customer WHERE Email = 'a@example.com'),
                     (SELECT CarID FROM dbo.Car WHERE CarName = 'Toyota Camry' AND CarModelYear = 2022),
                     5,'Excellent car, smooth ride';
END
GO

PRINT 'Database creation and seeding finished. Run cleanup_deduplicate.sql if needed to remove duplicates.';
