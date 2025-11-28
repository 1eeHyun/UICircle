ALTER TABLE conversations
    ADD COLUMN seller_deleted_at TIMESTAMP NULL,
    ADD COLUMN buyer_deleted_at TIMESTAMP NULL;