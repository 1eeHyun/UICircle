-- Notifications: user_id + created_at
CREATE INDEX IF NOT EXISTS idx_notifications_user_created
ON notifications(user_id, created_at);

-- Notifications: user_id + read_at + created_at
CREATE INDEX IF NOT EXISTS idx_notifications_user_read_created
ON notifications(user_id, read_at, created_at);

-- Listings: status + deleted_at + created_at
CREATE INDEX IF NOT EXISTS idx_listings_status_deleted_created
ON listings(status, deleted_at, created_at);

-- Listings: category_id + status + deleted_at
CREATE INDEX IF NOT EXISTS idx_listings_category_status_deleted
ON listings(category_id, status, deleted_at);

-- Listings: seller_id + status + deleted_at
CREATE INDEX IF NOT EXISTS idx_listings_seller_status_deleted
ON listings(seller_id, status, deleted_at);

-- Conversations: buyer_id + buyer_deleted_at + last_message_at
CREATE INDEX IF NOT EXISTS idx_conversations_buyer_deleted_last
ON conversations(buyer_id, buyer_deleted_at, last_message_at);

-- Conversations: seller_id + seller_deleted_at + last_message_at
CREATE INDEX IF NOT EXISTS idx_conversations_seller_deleted_last
ON conversations(seller_id, seller_deleted_at, last_message_at);