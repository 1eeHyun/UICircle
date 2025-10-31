package edu.uic.marketplace.model.listing;

public enum OfferStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    EXPIRED("Expired");

    private final String description;

    OfferStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}