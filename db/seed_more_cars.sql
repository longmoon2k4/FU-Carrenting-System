-- seed_more_cars.sql
-- Adds additional car producers and around 20 cars for testing
USE FUCarRentingSystemDB;
GO

-- Add some producers if missing
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Honda Motor Co., Ltd.', '1-1, Minato-ku, Tokyo', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Hyundai Motor Company')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Hyundai Motor Company', '12, Heolleung-ro, Seoul', 'South Korea');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Kia Corporation')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Kia Corporation', 'Seoul, South Korea', 'South Korea');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Nissan Motor Co., Ltd.')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Nissan Motor Co., Ltd.', 'Yokohama, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Chevrolet')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Chevrolet', 'Detroit, MI, USA', 'USA');
GO

IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Mazda Motor Corporation')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Mazda Motor Corporation', 'Hiroshima, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Subaru Corporation')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Subaru Corporation', 'Gunma, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'BMW')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('BMW', 'Munich, Germany', 'Germany');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Mercedes-Benz')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Mercedes-Benz', 'Stuttgart, Germany', 'Germany');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Suzuki')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Suzuki', 'Hamamatsu, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Mitsubishi')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Mitsubishi', 'Tokyo, Japan', 'Japan');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Volkswagen')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Volkswagen', 'Wolfsburg, Germany', 'Germany');
IF NOT EXISTS (SELECT 1 FROM CarProducer WHERE ProducerName = 'Audi')
    INSERT INTO CarProducer (ProducerName, Address, Country) VALUES ('Audi', 'Ingolstadt, Germany', 'Germany');
GO

-- Insert ~20 cars (check duplicates)
-- Format: CarName, Year, Color, Capacity, Description, ImportDate, ProducerName, RentPrice, Status

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Honda Civic' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Honda Civic', 2020, 'Blue', 5, 'Reliable compact sedan', '2022-03-10', ProducerID, 38.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Honda Accord' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Honda Accord', 2021, 'Silver', 5, 'Comfortable family sedan', '2023-02-12', ProducerID, 50.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Honda Motor Co., Ltd.';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Hyundai Elantra' AND CarModelYear = 2019 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Hyundai Motor Company'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Hyundai Elantra', 2019, 'White', 5, 'Economical compact', '2021-06-01', ProducerID, 30.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Hyundai Motor Company';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Hyundai Santa Fe' AND CarModelYear = 2022 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Hyundai Motor Company'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Hyundai Santa Fe', 2022, 'Gray', 7, 'Spacious SUV', '2023-08-15', ProducerID, 65.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Hyundai Motor Company';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Kia Sportage' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Kia Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Kia Sportage', 2020, 'Red', 5, 'Comfortable crossover', '2022-09-10', ProducerID, 42.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Kia Corporation';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Kia Carnival' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Kia Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Kia Carnival', 2021, 'Black', 8, 'Family MPV with space', '2023-03-05', ProducerID, 70.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Kia Corporation';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Nissan Altima' AND CarModelYear = 2018 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Nissan Motor Co., Ltd.'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Nissan Altima', 2018, 'Silver', 5, 'Smooth highway cruiser', '2020-11-10', ProducerID, 32.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Nissan Motor Co., Ltd.';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Nissan X-Trail' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Nissan Motor Co., Ltd.'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Nissan X-Trail', 2021, 'Blue', 7, 'Reliable off-road capable SUV', '2022-12-01', ProducerID, 60.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Nissan Motor Co., Ltd.';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Chevrolet Malibu' AND CarModelYear = 2019 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Chevrolet'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Chevrolet Malibu', 2019, 'White', 5, 'Comfortable commuter sedan', '2021-04-20', ProducerID, 34.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Chevrolet';

-- Additional cars to reach ~20
IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Toyota Corolla' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Toyota Corolla', 2020, 'Silver', 5, 'Popular compact sedan', '2022-01-20', ProducerID, 36.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Toyota RAV4' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Toyota RAV4', 2021, 'Green', 5, 'Reliable compact SUV', '2023-05-14', ProducerID, 55.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Toyota Motor Corporation';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Ford Mustang' AND CarModelYear = 2022 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Ford Motor Company'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Ford Mustang', 2022, 'Yellow', 4, 'Sporty coupe', '2023-07-01', ProducerID, 120.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Ford Motor Company';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Ford Explorer' AND CarModelYear = 2019 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Ford Motor Company'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Ford Explorer', 2019, 'Black', 7, 'Full-size SUV', '2021-08-10', ProducerID, 75.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Ford Motor Company';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Mazda CX-5' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Mazda Motor Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Mazda CX-5', 2020, 'Red', 5, 'Stylish crossover', '2022-05-02', ProducerID, 48.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Mazda Motor Corporation';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Subaru Forester' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Subaru Corporation'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Subaru Forester', 2021, 'Green', 5, 'All-wheel drive SUV', '2022-10-10', ProducerID, 53.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Subaru Corporation';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'BMW 3 Series' AND CarModelYear = 2019 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'BMW'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'BMW 3 Series', 2019, 'White', 5, 'Premium compact executive', '2021-02-20', ProducerID, 110.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'BMW';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Mercedes C-Class' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Mercedes-Benz'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Mercedes C-Class', 2020, 'Silver', 5, 'Luxury compact sedan', '2022-07-07', ProducerID, 130.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Mercedes-Benz';

-- A few economy models
IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Suzuki Swift' AND CarModelYear = 2018 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Suzuki'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Suzuki Swift', 2018, 'Blue', 4, 'Small city car', '2019-03-12', ProducerID, 22.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Suzuki';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Mitsubishi Outlander' AND CarModelYear = 2021 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Mitsubishi'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Mitsubishi Outlander', 2021, 'Silver', 7, 'Family SUV', '2023-04-01', ProducerID, 58.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Mitsubishi';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Volkswagen Golf' AND CarModelYear = 2019 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Volkswagen'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Volkswagen Golf', 2019, 'Gray', 5, 'Practical hatchback', '2021-05-25', ProducerID, 37.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Volkswagen';

IF NOT EXISTS (SELECT 1 FROM Car WHERE CarName = 'Audi A4' AND CarModelYear = 2020 AND ProducerID = (SELECT ProducerID FROM CarProducer WHERE ProducerName = 'Audi'))
    INSERT INTO Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
    SELECT 'Audi A4', 2020, 'Black', 5, 'Premium compact', '2022-06-06', ProducerID, 115.00, 'AVAILABLE' FROM CarProducer WHERE ProducerName = 'Audi';

GO
