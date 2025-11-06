-- =========================================================
--  Flyway: V1__init_schema.sql  (MySQL 8)
--  Source: mysqldump (uic_market)  // utf8mb4 / InnoDB
--  Note: No DROP/SET statements; ordered by FK dependencies
-- =========================================================

-- 1) root tables (no FKs)
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` bit(1) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role` enum('ADMIN','PROFESSOR','USER') NOT NULL,
  `status` enum('ACTIVE','DELETED','SUSPENDED') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_phone_number` (`phone_number`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_phone_number` (`phone_number`),
  KEY `idx_users_role` (`role`),
  KEY `idx_users_status` (`status`),
  KEY `idx_users_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `badges` (
  `badge_id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `icon_url` varchar(500) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`badge_id`),
  UNIQUE KEY `UK3uvqm0wd6pysfcyv49hp7mroy` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2) self-FK roots
CREATE TABLE `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_categories_parent_name` (`parent_id`,`name`),
  KEY `idx_categories_parent_id` (`parent_id`),
  KEY `idx_categories_name` (`name`),
  CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`)
    REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3) depends on users/categories
CREATE TABLE `listings` (
  `listing_id` bigint NOT NULL AUTO_INCREMENT,
  `item_condition` enum('FAIR','GOOD','LIKE_NEW','NEW','POOR') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `description` text NOT NULL,
  `favorite_count` int NOT NULL,
  `is_negotiable` bit(1) NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` enum('ACTIVE','DELETED','INACTIVE','SOLD') NOT NULL,
  `title` varchar(100) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `view_count` int NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `seller_id` bigint NOT NULL,
  PRIMARY KEY (`listing_id`),
  KEY `idx_listings_seller_id` (`seller_id`),
  KEY `idx_listings_category_id` (`category_id`),
  KEY `idx_listings_status` (`status`),
  KEY `idx_listings_created_at` (`created_at`),
  KEY `idx_listings_price` (`price`),
  CONSTRAINT `fk_listings_category` FOREIGN KEY (`category_id`)
    REFERENCES `categories` (`category_id`),
  CONSTRAINT `fk_listings_seller` FOREIGN KEY (`seller_id`)
    REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4) simple user-owned tables
CREATE TABLE `profiles` (
  `user_id` bigint NOT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `bio` varchar(500) DEFAULT NULL,
  `buy_count` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `display_name` varchar(50) DEFAULT NULL,
  `major` varchar(100) DEFAULT NULL,
  `sold_count` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_profiles_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `email_subscriptions` (
  `user_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `listing_sold_email` bit(1) NOT NULL,
  `new_message_email` bit(1) NOT NULL,
  `offer_received_email` bit(1) NOT NULL,
  `price_change_email` bit(1) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_email_subscriptions_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `email_verifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `verified_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKq8cpk6dlms2nk528fqwsydjpq` (`token`),
  KEY `idx_email_verifications_user_id` (`user_id`),
  KEY `idx_email_verifications_token` (`token`),
  KEY `idx_email_verifications_expires_at` (`expires_at`),
  CONSTRAINT `fk_email_verifications_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `password_resets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKj3in8q8o0ve0i34pyug2kgibh` (`token`),
  KEY `idx_password_resets_user_id` (`user_id`),
  KEY `idx_password_resets_token` (`token`),
  KEY `idx_password_resets_expires_at` (`expires_at`),
  CONSTRAINT `fk_password_resets_user` FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5) user↔user relations
CREATE TABLE `blocks` (
  `blocked_at` datetime(6) NOT NULL,
  `blocked_id` bigint NOT NULL,
  `blocker_id` bigint NOT NULL,
  PRIMARY KEY (`blocked_id`,`blocker_id`),
  KEY `idx_blocks_blocker_id` (`blocker_id`),
  KEY `idx_blocks_blocked_id` (`blocked_id`),
  CONSTRAINT `fk_blocks_blocked` FOREIGN KEY (`blocked_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_blocks_blocker` FOREIGN KEY (`blocker_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6) listings-related
CREATE TABLE `listing_images` (
  `image_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `display_order` int NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `listing_id` bigint NOT NULL,
  PRIMARY KEY (`image_id`),
  KEY `idx_listing_images_listing_id` (`listing_id`),
  KEY `idx_listing_images_display_order` (`display_order`),
  CONSTRAINT `fk_listing_images_listing` FOREIGN KEY (`listing_id`)
    REFERENCES `listings` (`listing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `favorites` (
  `favorited_at` datetime(6) NOT NULL,
  `listing_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`listing_id`,`user_id`),
  KEY `idx_favorites_user_id` (`user_id`),
  KEY `idx_favorites_listing_id` (`listing_id`),
  CONSTRAINT `fk_favorites_listing` FOREIGN KEY (`listing_id`) REFERENCES `listings` (`listing_id`),
  CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `view_history` (
  `viewed_at` datetime(6) NOT NULL,
  `listing_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`listing_id`,`user_id`),
  KEY `idx_view_history_user_id` (`user_id`),
  KEY `idx_view_history_listing_id` (`listing_id`),
  KEY `idx_view_history_viewed_at` (`viewed_at`),
  CONSTRAINT `fk_view_history_listing` FOREIGN KEY (`listing_id`) REFERENCES `listings` (`listing_id`),
  CONSTRAINT `fk_view_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7) messaging
CREATE TABLE `conversations` (
  `conversation_id` bigint NOT NULL AUTO_INCREMENT,
  `buyer_unread_count` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `last_message_at` datetime(6) DEFAULT NULL,
  `seller_unread_count` int NOT NULL,
  `buyer_id` bigint NOT NULL,
  `listing_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  PRIMARY KEY (`conversation_id`),
  UNIQUE KEY `uk_conversations_listing_buyer_seller` (`listing_id`,`buyer_id`,`seller_id`),
  KEY `idx_conversations_listing_id` (`listing_id`),
  KEY `idx_conversations_seller_id` (`seller_id`),
  KEY `idx_conversations_buyer_id` (`buyer_id`),
  KEY `idx_conversations_last_message_at` (`last_message_at`),
  CONSTRAINT `fk_conversations_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_conversations_listing` FOREIGN KEY (`listing_id`) REFERENCES `listings` (`listing_id`),
  CONSTRAINT `fk_conversations_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `messages` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `body` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `message_type` enum('IMAGE','SYSTEM','TEXT') NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `conversation_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `idx_messages_conversation_id` (`conversation_id`),
  KEY `idx_messages_sender_id` (`sender_id`),
  KEY `idx_messages_created_at` (`created_at`),
  KEY `idx_messages_read_at` (`read_at`),
  CONSTRAINT `fk_messages_conversation` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`),
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 8) notifications/moderation
CREATE TABLE `notifications` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `entity_id` bigint DEFAULT NULL,
  `entity_type` varchar(50) DEFAULT NULL,
  `link_url` varchar(500) DEFAULT NULL,
  `message` varchar(500) NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `type` enum('LISTING_SOLD','NEW_MESSAGE','NEW_REVIEW','OFFER_ACCEPTED','OFFER_REJECTED','PRICE_CHANGE','PRICE_OFFER','SYSTEM') NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `idx_notifications_user_id` (`user_id`),
  KEY `idx_notifications_type` (`type`),
  KEY `idx_notifications_read_at` (`read_at`),
  KEY `idx_notifications_created_at` (`created_at`),
  CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `moderation_actions` (
  `action_id` bigint NOT NULL AUTO_INCREMENT,
  `action_type` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `note` text,
  `target_id` bigint NOT NULL,
  `target_type` varchar(50) NOT NULL,
  `admin_id` bigint NOT NULL,
  PRIMARY KEY (`action_id`),
  KEY `idx_moderation_actions_admin_id` (`admin_id`),
  KEY `idx_moderation_actions_target_type` (`target_type`),
  KEY `idx_moderation_actions_created_at` (`created_at`),
  CONSTRAINT `fk_moderation_actions_admin` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 9) marketplace flows
CREATE TABLE `transactions` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `final_price` decimal(10,2) NOT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','PENDING') NOT NULL,
  `buyer_id` bigint NOT NULL,
  `listing_id` bigint NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `idx_transactions_listing_id` (`listing_id`),
  KEY `idx_transactions_buyer_id` (`buyer_id`),
  KEY `idx_transactions_status` (`status`),
  KEY `idx_transactions_created_at` (`created_at`),
  CONSTRAINT `fk_transactions_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_transactions_listing` FOREIGN KEY (`listing_id`) REFERENCES `listings` (`listing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `price_offers` (
  `offer_id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `message` varchar(500) DEFAULT NULL,
  `status` enum('ACCEPTED','EXPIRED','PENDING','REJECTED') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `buyer_id` bigint NOT NULL,
  `listing_id` bigint NOT NULL,
  PRIMARY KEY (`offer_id`),
  KEY `idx_price_offers_listing_id` (`listing_id`),
  KEY `idx_price_offers_buyer_id` (`buyer_id`),
  KEY `idx_price_offers_status` (`status`),
  CONSTRAINT `fk_price_offers_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_price_offers_listing` FOREIGN KEY (`listing_id`) REFERENCES `listings` (`listing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `reviews` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `reviewed_user_id` bigint NOT NULL,
  `reviewer_id` bigint NOT NULL,
  `transaction_id` bigint NOT NULL,
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `uk_reviews_transaction_reviewer` (`transaction_id`,`reviewer_id`),
  KEY `idx_reviews_transaction_id` (`transaction_id`),
  KEY `idx_reviews_reviewer_id` (`reviewer_id`),
  KEY `idx_reviews_reviewed_user_id` (`reviewed_user_id`),
  KEY `idx_reviews_created_at` (`created_at`),
  CONSTRAINT `fk_reviews_reviewed_user` FOREIGN KEY (`reviewed_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_reviews_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_reviews_transaction` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `reports` (
  `report_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `reason` varchar(100) NOT NULL,
  `resolution_note` text,
  `resolved_at` datetime(6) DEFAULT NULL,
  `status` enum('DISMISSED','PENDING','RESOLVED','UNDER_REVIEW') NOT NULL,
  `target_id` bigint NOT NULL,
  `target_type` varchar(50) NOT NULL,
  `reporter_id` bigint NOT NULL,
  `resolved_by` bigint DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  KEY `idx_reports_reporter_id` (`reporter_id`),
  KEY `idx_reports_target_type` (`target_type`),
  KEY `idx_reports_target_id` (`target_id`),
  KEY `idx_reports_status` (`status`),
  KEY `idx_reports_created_at` (`created_at`),
  KEY `fk_reports_resolved_by` (`resolved_by`),
  CONSTRAINT `fk_reports_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_reports_resolved_by` FOREIGN KEY (`resolved_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 10) many-to-many user↔badge
CREATE TABLE `user_badges` (
  `awarded_at` datetime(6) NOT NULL,
  `badge_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`badge_id`,`user_id`),
  KEY `idx_user_badges_user_id` (`user_id`),
  KEY `idx_user_badges_badge_id` (`badge_id`),
  CONSTRAINT `fk_user_badges_badge` FOREIGN KEY (`badge_id`) REFERENCES `badges` (`badge_id`),
  CONSTRAINT `fk_user_badges_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 11) saved searches
CREATE TABLE `saved_searches` (
  `saved_search_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `filters` text,
  `name` varchar(100) DEFAULT NULL,
  `query` varchar(500) DEFAULT NULL,
  `query_hash` varchar(64) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`saved_search_id`),
  KEY `idx_saved_searches_user_id` (`user_id`),
  KEY `idx_saved_searches_created_at` (`created_at`),
  CONSTRAINT `fk_saved_searches_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
