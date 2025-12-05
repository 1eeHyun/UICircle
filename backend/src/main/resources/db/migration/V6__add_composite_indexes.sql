-- Notifications
-- ============================================

CREATE INDEX idx_notifications_user_created
ON notifications(user_id, created_at);

CREATE INDEX idx_notifications_user_read_created
ON notifications(user_id, read_at, created_at);


-- Listings
-- ============================================

CREATE INDEX idx_listings_status_deleted_created
ON listings(status, deleted_at, created_at);

CREATE INDEX idx_listings_category_status_deleted
ON listings(category_id, status, deleted_at);

CREATE INDEX idx_listings_seller_status_deleted
ON listings(seller_id, status, deleted_at);


-- Conversations
-- ============================================

CREATE INDEX idx_conversations_buyer_last_message
ON conversations(buyer_id, last_message_at);

CREATE INDEX idx_conversations_seller_last_message
ON conversations(seller_id, last_message_at);
