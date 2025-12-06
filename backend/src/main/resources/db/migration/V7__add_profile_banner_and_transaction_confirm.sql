ALTER TABLE profiles
    ADD COLUMN banner_url VARCHAR(500) NULL AFTER avatar_url;

ALTER TABLE transactions
    ADD COLUMN buyer_confirmed  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN seller_confirmed BOOLEAN NOT NULL DEFAULT FALSE;
