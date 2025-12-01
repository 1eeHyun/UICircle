package edu.uic.marketplace.model.moderation;

/**
 * Types of entities that can be reported
 */
public enum ReportTargetType {
    // Report a listing (product/item)
    LISTING("Listing"),

    // Report a user profile
    USER("User"),

    // Report a message in conversation
    MESSAGE("Message"),

    // Report a review
    REVIEW("Review"),

    // Report a price offer
    PRICE_OFFER("Price Offer");

    private final String displayName;

    ReportTargetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
