-- ============================================================================
-- STEP 1: Add target_public_id column (idempotent)
-- ============================================================================

SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'reports'
      AND COLUMN_NAME  = 'target_public_id'
);

SET @add_col_sql := IF(@col_exists = 0,
    'ALTER TABLE reports ADD COLUMN target_public_id VARCHAR(36) AFTER target_type',
    'SELECT 1'
);

PREPARE stmt FROM @add_col_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================================
-- STEP 2: target_id -> target_public_id migration
-- ============================================================================

SET @target_id_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'reports'
      AND COLUMN_NAME  = 'target_id'
);

SET @listings_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'listings'
);

SET @users_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'users'
);

SET @messages_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'messages'
);

SET @reviews_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'reviews'
);

SET @price_offers_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'price_offers'
);

-- LISTING migration
SET @listing_migration_sql := IF(
    @target_id_exists > 0 AND @listings_exists > 0,
    'UPDATE reports r
     JOIN listings l ON r.target_id = l.listing_id
     SET r.target_public_id = l.public_id
     WHERE (r.target_type = ''LISTING'' OR r.target_type = ''listing'')
       AND r.target_public_id IS NULL',
    'SELECT 1'
);

PREPARE stmt FROM @listing_migration_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- USER migration
SET @user_migration_sql := IF(
    @target_id_exists > 0 AND @users_exists > 0,
    'UPDATE reports r
     JOIN users u ON r.target_id = u.user_id
     SET r.target_public_id = u.public_id
     WHERE (r.target_type = ''USER'' OR r.target_type = ''user'')
       AND r.target_public_id IS NULL',
    'SELECT 1'
);

PREPARE stmt FROM @user_migration_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- MESSAGE migration
SET @message_migration_sql := IF(
    @target_id_exists > 0 AND @messages_exists > 0,
    'UPDATE reports r
     JOIN messages m ON r.target_id = m.message_id
     SET r.target_public_id = m.public_id
     WHERE (r.target_type = ''MESSAGE'' OR r.target_type = ''message'')
       AND r.target_public_id IS NULL',
    'SELECT 1'
);

PREPARE stmt FROM @message_migration_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- REVIEW migration
SET @review_migration_sql := IF(
    @target_id_exists > 0 AND @reviews_exists > 0,
    'UPDATE reports r
     JOIN reviews rev ON r.target_id = rev.review_id
     SET r.target_public_id = rev.public_id
     WHERE (r.target_type = ''REVIEW'' OR r.target_type = ''review'')
       AND r.target_public_id IS NULL',
    'SELECT 1'
);

PREPARE stmt FROM @review_migration_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- PRICE_OFFER migration
SET @price_offer_migration_sql := IF(
    @target_id_exists > 0 AND @price_offers_exists > 0,
    'UPDATE reports r
     JOIN price_offers po ON r.target_id = po.offer_id
     SET r.target_public_id = po.public_id
     WHERE (r.target_type = ''PRICE_OFFER'' OR r.target_type = ''price_offer'')
       AND r.target_public_id IS NULL',
    'SELECT 1'
);

PREPARE stmt FROM @price_offer_migration_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================================
-- STEP 3: Handle orphaned records
-- ============================================================================

UPDATE reports
SET status          = 'RESOLVED',
    resolution_note = 'Target entity was deleted before migration',
    resolved_at     = NOW()
WHERE target_public_id IS NULL
  AND status = 'PENDING'
  AND @target_id_exists > 0;

-- ============================================================================
-- STEP 4: Drop target_id column (idempotent)
-- ============================================================================

SET @drop_sql := IF(@target_id_exists > 0,
    'ALTER TABLE reports DROP COLUMN target_id',
    'SELECT 1'
);

PREPARE stmt FROM @drop_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================================
-- STEP 5: Create indexes (idempotent)
-- ============================================================================

SET @idx1_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'reports'
      AND INDEX_NAME   = 'idx_reports_target_public_id'
);

SET @create_idx1_sql := IF(@idx1_exists = 0,
    'CREATE INDEX idx_reports_target_public_id ON reports(target_public_id)',
    'SELECT 1'
);

PREPARE stmt FROM @create_idx1_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx2_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'reports'
      AND INDEX_NAME   = 'idx_reports_target_type_public_id'
);

SET @create_idx2_sql := IF(@idx2_exists = 0,
    'CREATE INDEX idx_reports_target_type_public_id ON reports(target_type, target_public_id)',
    'SELECT 1'
);

PREPARE stmt FROM @create_idx2_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
