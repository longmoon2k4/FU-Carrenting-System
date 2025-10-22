-- cleanup_deduplicate.sql
-- Purpose: Remove duplicated data caused by re-running seed script without guards.
-- Target: Microsoft SQL Server
-- Safe approach: for each natural key, keep the smallest ID (KeepID),
-- update child foreign keys to that KeepID, then delete duplicate rows.

SET NOCOUNT ON;

IF DB_ID('FUCarRentingSystemDB') IS NULL
BEGIN
    PRINT 'Database FUCarRentingSystemDB does not exist.';
    RETURN;
END
GO

USE FUCarRentingSystemDB;
GO

-- 1) ACCOUNT: dedupe by AccountName
BEGIN TRAN;

IF OBJECT_ID('tempdb..#AccountKeep') IS NOT NULL DROP TABLE #AccountKeep;
SELECT MIN(AccountID) AS KeepID, AccountName
INTO #AccountKeep
FROM dbo.Account
GROUP BY AccountName;

-- Update FK in Customer
UPDATE c
SET c.AccountID = k.KeepID
FROM dbo.Customer c
JOIN dbo.Account a ON a.AccountID = c.AccountID
JOIN #AccountKeep k ON k.AccountName = a.AccountName AND a.AccountID <> k.KeepID;

-- Delete duplicates
DELETE a
FROM dbo.Account a
JOIN #AccountKeep k ON k.AccountName = a.AccountName AND a.AccountID <> k.KeepID;

DROP TABLE #AccountKeep;
COMMIT TRAN;
GO

-- 2) CAR PRODUCER: dedupe by ProducerName
BEGIN TRAN;

IF OBJECT_ID('tempdb..#ProducerKeep') IS NOT NULL DROP TABLE #ProducerKeep;
SELECT MIN(ProducerID) AS KeepID, ProducerName
INTO #ProducerKeep
FROM dbo.CarProducer
GROUP BY ProducerName;

-- Update FK in Car
UPDATE c
SET c.ProducerID = k.KeepID
FROM dbo.Car c
JOIN dbo.CarProducer p ON p.ProducerID = c.ProducerID
JOIN #ProducerKeep k ON k.ProducerName = p.ProducerName AND p.ProducerID <> k.KeepID;

-- Delete duplicates
DELETE p
FROM dbo.CarProducer p
JOIN #ProducerKeep k ON k.ProducerName = p.ProducerName AND p.ProducerID <> k.KeepID;

DROP TABLE #ProducerKeep;
COMMIT TRAN;
GO

-- 3) CAR: dedupe by (CarName, CarModelYear, ProducerID)
-- Assumes producers already deduped
BEGIN TRAN;

IF OBJECT_ID('tempdb..#CarKeep') IS NOT NULL DROP TABLE #CarKeep;
SELECT MIN(CarID) AS KeepID, CarName, CarModelYear, ProducerID
INTO #CarKeep
FROM dbo.Car
GROUP BY CarName, CarModelYear, ProducerID;

-- Update FK in CarRental
UPDATE cr
SET cr.CarID = k.KeepID
FROM dbo.CarRental cr
JOIN dbo.Car c ON c.CarID = cr.CarID
JOIN #CarKeep k ON k.CarName = c.CarName AND k.CarModelYear = c.CarModelYear AND k.ProducerID = c.ProducerID
    AND c.CarID <> k.KeepID;

-- Update FK in Review
UPDATE r
SET r.CarID = k.KeepID
FROM dbo.Review r
JOIN dbo.Car c ON c.CarID = r.CarID
JOIN #CarKeep k ON k.CarName = c.CarName AND k.CarModelYear = c.CarModelYear AND k.ProducerID = c.ProducerID
    AND c.CarID <> k.KeepID;

-- Delete duplicates
DELETE c
FROM dbo.Car c
JOIN #CarKeep k ON k.CarName = c.CarName AND k.CarModelYear = c.CarModelYear AND k.ProducerID = c.ProducerID
    AND c.CarID <> k.KeepID;

DROP TABLE #CarKeep;
COMMIT TRAN;
GO

-- 4) CUSTOMER: dedupe pass 1 by Email (primary natural key)
BEGIN TRAN;

IF OBJECT_ID('tempdb..#CustomerKeep') IS NOT NULL DROP TABLE #CustomerKeep;
SELECT MIN(CustomerID) AS KeepID, Email
INTO #CustomerKeep
FROM dbo.Customer
GROUP BY Email;

-- Update FKs in CarRental and Review
UPDATE cr
SET cr.CustomerID = k.KeepID
FROM dbo.CarRental cr
JOIN dbo.Customer c ON c.CustomerID = cr.CustomerID
JOIN #CustomerKeep k ON k.Email = c.Email AND c.CustomerID <> k.KeepID;

UPDATE r
SET r.CustomerID = k.KeepID
FROM dbo.Review r
JOIN dbo.Customer c ON c.CustomerID = r.CustomerID
JOIN #CustomerKeep k ON k.Email = c.Email AND c.CustomerID <> k.KeepID;

-- Delete duplicates
DELETE c
FROM dbo.Customer c
JOIN #CustomerKeep k ON k.Email = c.Email AND c.CustomerID <> k.KeepID;

DROP TABLE #CustomerKeep;
COMMIT TRAN;
GO

-- 4b) CUSTOMER: dedupe pass 2 by IdentityCard (if any left)
BEGIN TRAN;

IF OBJECT_ID('tempdb..#CustomerKeep2') IS NOT NULL DROP TABLE #CustomerKeep2;
SELECT MIN(CustomerID) AS KeepID, IdentityCard
INTO #CustomerKeep2
FROM dbo.Customer
GROUP BY IdentityCard;

UPDATE cr
SET cr.CustomerID = k.KeepID
FROM dbo.CarRental cr
JOIN dbo.Customer c ON c.CustomerID = cr.CustomerID
JOIN #CustomerKeep2 k ON k.IdentityCard = c.IdentityCard AND c.CustomerID <> k.KeepID;

UPDATE r
SET r.CustomerID = k.KeepID
FROM dbo.Review r
JOIN dbo.Customer c ON c.CustomerID = r.CustomerID
JOIN #CustomerKeep2 k ON k.IdentityCard = c.IdentityCard AND c.CustomerID <> k.KeepID;

DELETE c
FROM dbo.Customer c
JOIN #CustomerKeep2 k ON k.IdentityCard = c.IdentityCard AND c.CustomerID <> k.KeepID;

DROP TABLE #CustomerKeep2;
COMMIT TRAN;
GO

-- 4c) CUSTOMER: dedupe pass 3 by LicenceNumber (if any left)
BEGIN TRAN;

IF OBJECT_ID('tempdb..#CustomerKeep3') IS NOT NULL DROP TABLE #CustomerKeep3;
SELECT MIN(CustomerID) AS KeepID, LicenceNumber
INTO #CustomerKeep3
FROM dbo.Customer
GROUP BY LicenceNumber;

UPDATE cr
SET cr.CustomerID = k.KeepID
FROM dbo.CarRental cr
JOIN dbo.Customer c ON c.CustomerID = cr.CustomerID
JOIN #CustomerKeep3 k ON k.LicenceNumber = c.LicenceNumber AND c.CustomerID <> k.KeepID;

UPDATE r
SET r.CustomerID = k.KeepID
FROM dbo.Review r
JOIN dbo.Customer c ON c.CustomerID = r.CustomerID
JOIN #CustomerKeep3 k ON k.LicenceNumber = c.LicenceNumber AND c.CustomerID <> k.KeepID;

DELETE c
FROM dbo.Customer c
JOIN #CustomerKeep3 k ON k.LicenceNumber = c.LicenceNumber AND c.CustomerID <> k.KeepID;

DROP TABLE #CustomerKeep3;
COMMIT TRAN;
GO

-- 5) CAR RENTAL: collapse exact duplicates (all columns equal)
BEGIN TRAN;

IF OBJECT_ID('tempdb..#CarRentalKeep') IS NOT NULL DROP TABLE #CarRentalKeep;
SELECT MIN(CarRentalID) AS KeepID, CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status
INTO #CarRentalKeep
FROM dbo.CarRental
GROUP BY CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status;

DELETE cr
FROM dbo.CarRental cr
JOIN #CarRentalKeep k ON k.CustomerID = cr.CustomerID
    AND k.CarID = cr.CarID
    AND k.PickupDate = cr.PickupDate
    AND k.ReturnDate = cr.ReturnDate
    AND k.RentPrice = cr.RentPrice
    AND k.Status = cr.Status
    AND cr.CarRentalID <> k.KeepID;

DROP TABLE #CarRentalKeep;
COMMIT TRAN;
GO

-- 6) REVIEW: collapse exact duplicates (all columns equal)
BEGIN TRAN;

IF OBJECT_ID('tempdb..#ReviewKeep') IS NOT NULL DROP TABLE #ReviewKeep;
SELECT MIN(ReviewID) AS KeepID, CustomerID, CarID, ReviewStar, Comment
INTO #ReviewKeep
FROM dbo.Review
GROUP BY CustomerID, CarID, ReviewStar, Comment;

DELETE r
FROM dbo.Review r
JOIN #ReviewKeep k ON k.CustomerID = r.CustomerID
    AND k.CarID = r.CarID
    AND k.ReviewStar = r.ReviewStar
    AND k.Comment = r.Comment
    AND r.ReviewID <> k.KeepID;

DROP TABLE #ReviewKeep;
COMMIT TRAN;
GO

PRINT 'Duplicate cleanup completed.';
