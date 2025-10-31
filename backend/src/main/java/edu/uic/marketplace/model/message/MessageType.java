package edu.uic.marketplace.model.message;

public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    SYSTEM("System");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}