ALTER TABLE accounts
    ALTER COLUMN updated_at
    TYPE timestamp without time zone
    USING updated_at AT TIME ZONE 'Europe/Moscow';

ALTER TABLE orders
    ALTER COLUMN updated_at
    TYPE timestamp without time zone
    USING updated_at AT TIME ZONE 'Europe/Moscow';