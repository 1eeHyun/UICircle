package edu.uic.marketplace.model.user;

public enum UserRole {
    USER("Regular User"),
    PROFESSOR("Professor"),
    ADMIN("Administrator");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
