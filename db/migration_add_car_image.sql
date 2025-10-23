-- migration_add_car_image.sql
USE FUCarRentingSystemDB;
GO

-- Add ImageUrl column to Car if not exists
IF NOT EXISTS (
    SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Car') AND name = 'ImageUrl'
)
BEGIN
    ALTER TABLE Car ADD ImageUrl NVARCHAR(500) NULL;
END
GO

-- OPTIONAL: update existing cars with placeholder image paths (only if ImageUrl is null)
UPDATE Car SET ImageUrl = '/img/car-placeholder.svg' WHERE ImageUrl IS NULL;
GO
