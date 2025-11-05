-- Test data for booking table
-- Inserts two test bookings (without booker emails). Safe to re-run: check by wa_id.

-- First booking
INSERT INTO booking (wa_id, name, bike, duration, price, status, startdate, enddate)
SELECT '+10000000001', 'Test User 1', 'TestBike-A', 2, 100, 'CONFIRMED', CURRENT_DATE + INTERVAL '7 days', CURRENT_DATE + INTERVAL '9 days'
WHERE NOT EXISTS (SELECT 1 FROM booking WHERE wa_id = '+10000000001');

-- Second booking
INSERT INTO booking (wa_id, name, bike, duration, price, status, startdate, enddate)
SELECT '+10000000002', 'Test User 2', 'TestBike-B', 4, 200, 'CONFIRMED', CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '14 days'
WHERE NOT EXISTS (SELECT 1 FROM booking WHERE wa_id = '+10000000002');

-- Ensure batch columns are null so the booking-reminder job will pick them up
UPDATE booking SET batch_sent_at = NULL, batch_attempts = 0
WHERE wa_id IN ('+10000000001','+10000000002');
