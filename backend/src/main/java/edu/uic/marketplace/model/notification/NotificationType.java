package edu.uic.marketplace.model.notification;

public enum NotificationType {
    NEW_MESSAGE("New Message"),
    PRICE_OFFER("Price Offer"),
    OFFER_ACCEPTED("Offer Accepted"),
    OFFER_REJECTED("Offer Rejected"),
    LISTING_SOLD("Listing Sold"),
    NEW_REVIEW("New Review"),
    PRICE_CHANGE("Price Change"),
    SYSTEM("System Notification");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}