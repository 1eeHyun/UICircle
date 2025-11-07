package edu.uic.marketplace.model.listing;

public enum ListingStatus {
    ACTIVE("Active"),
    SOLD("Sold"),

    DELETED("Delete"),
    INACTIVE("Inactive");

    private final String description;

    ListingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}