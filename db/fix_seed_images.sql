-- db/fix_seed_images.sql
-- Fix per-car ImageUrl values when migration previously set a generic placeholder
USE FUCarRentingSystemDB;
GO

-- Only overwrite rows that currently point to the generic placeholder to avoid clobbering any real images
-- For each seeded car, join with the producer to ensure correct mapping

UPDATE c
SET c.ImageUrl = '/img/cars/honda-civic.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Honda Civic' AND c.CarModelYear = 2020 AND p.ProducerName LIKE '%Honda%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/honda-accord.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Honda Accord' AND c.CarModelYear = 2021 AND p.ProducerName LIKE '%Honda%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/hyundai-elantra.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Hyundai Elantra' AND c.CarModelYear = 2019 AND p.ProducerName LIKE '%Hyundai%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/hyundai-santafe.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Hyundai Santa Fe' AND c.CarModelYear = 2022 AND p.ProducerName LIKE '%Hyundai%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/kia-sportage.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Kia Sportage' AND c.CarModelYear = 2020 AND p.ProducerName LIKE '%Kia%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/kia-carnival.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Kia Carnival' AND c.CarModelYear = 2021 AND p.ProducerName LIKE '%Kia%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/nissan-altima.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Nissan Altima' AND c.CarModelYear = 2018 AND p.ProducerName LIKE '%Nissan%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/nissan-xtrail.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Nissan X-Trail' AND c.CarModelYear = 2021 AND p.ProducerName LIKE '%Nissan%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/chevrolet-malibu.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Chevrolet Malibu' AND c.CarModelYear = 2019 AND p.ProducerName LIKE '%Chevrolet%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/toyota-corolla.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Toyota Corolla' AND c.CarModelYear = 2020 AND p.ProducerName LIKE '%Toyota%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/toyota-rav4.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Toyota RAV4' AND c.CarModelYear = 2021 AND p.ProducerName LIKE '%Toyota%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/ford-mustang.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Ford Mustang' AND c.CarModelYear = 2022 AND p.ProducerName LIKE '%Ford%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/ford-explorer.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Ford Explorer' AND c.CarModelYear = 2019 AND p.ProducerName LIKE '%Ford%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/mazda-cx5.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Mazda CX-5' AND c.CarModelYear = 2020 AND p.ProducerName LIKE '%Mazda%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/subaru-forester.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Subaru Forester' AND c.CarModelYear = 2021 AND p.ProducerName LIKE '%Subaru%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/bmw-3series.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'BMW 3 Series' AND c.CarModelYear = 2019 AND p.ProducerName LIKE '%BMW%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/mercedes-cclass.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Mercedes C-Class' AND c.CarModelYear = 2020 AND p.ProducerName LIKE '%Mercedes%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/suzuki-swift.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Suzuki Swift' AND c.CarModelYear = 2018 AND p.ProducerName LIKE '%Suzuki%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/mitsubishi-outlander.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Mitsubishi Outlander' AND c.CarModelYear = 2021 AND p.ProducerName LIKE '%Mitsubishi%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/vw-golf.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Volkswagen Golf' AND c.CarModelYear = 2019 AND p.ProducerName LIKE '%Volkswagen%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

UPDATE c
SET c.ImageUrl = '/img/cars/audi-a4.svg'
FROM Car c
JOIN CarProducer p ON c.ProducerID = p.ProducerID
WHERE c.CarName = 'Audi A4' AND c.CarModelYear = 2020 AND p.ProducerName LIKE '%Audi%'
  AND c.ImageUrl = '/img/car-placeholder.svg';

-- Summary check
SELECT CarID, CarName, CarModelYear, ImageUrl FROM Car ORDER BY CarID;

GO
