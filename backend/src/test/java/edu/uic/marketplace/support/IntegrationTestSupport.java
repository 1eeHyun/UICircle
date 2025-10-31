package edu.uic.marketplace.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Integration test base class using H2 in-memory database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Disable foreign key checks
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            
            // Get all tables and truncate them
            stmt.execute("TRUNCATE TABLE user_badges");
            stmt.execute("TRUNCATE TABLE badges");
            stmt.execute("TRUNCATE TABLE profiles");
            stmt.execute("TRUNCATE TABLE email_verifications");
            stmt.execute("TRUNCATE TABLE password_resets");
            stmt.execute("TRUNCATE TABLE listing_images");
            stmt.execute("TRUNCATE TABLE favorites");
            stmt.execute("TRUNCATE TABLE price_offers");
            stmt.execute("TRUNCATE TABLE listings");
            stmt.execute("TRUNCATE TABLE categories");
            stmt.execute("TRUNCATE TABLE messages");
            stmt.execute("TRUNCATE TABLE conversations");
            stmt.execute("TRUNCATE TABLE reviews");
            stmt.execute("TRUNCATE TABLE transactions");
            stmt.execute("TRUNCATE TABLE notifications");
            stmt.execute("TRUNCATE TABLE email_subscriptions");
            stmt.execute("TRUNCATE TABLE blocks");
            stmt.execute("TRUNCATE TABLE reports");
            stmt.execute("TRUNCATE TABLE moderation_actions");
            stmt.execute("TRUNCATE TABLE saved_searches");
            stmt.execute("TRUNCATE TABLE view_history");
            stmt.execute("TRUNCATE TABLE users");
            
            // Re-enable foreign key checks
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }
}
