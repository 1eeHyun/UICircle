package edu.uic.marketplace.model.listing;

public enum ItemCondition {
    NEW("Brand New"),
    LIKE_NEW("Like New"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor");

    private final String description;

    ItemCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
