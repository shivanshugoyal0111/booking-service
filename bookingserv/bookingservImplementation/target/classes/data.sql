DROP TABLE IF EXISTS bookings;

CREATE TABLE bookings (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  first_name VARCHAR(250) NOT NULL,
  last_name VARCHAR(250) NOT NULL,
  date_of_birth DATE NOT NULL,
  checkin_datetime datetime NOT NULL,
  checkout_datetime datetime NOT NULL,
  total_price DECIMAL(10,2) NOT NULL,
  deposit DECIMAL(10,2) NOT NULL,
  address_details text NOT NULL
);

DROP TABLE IF EXISTS idempotency_check;

CREATE TABLE idempotency_check (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  idempotency_key VARCHAR(100) NOT NULL,
  status bool NOT NULL
);