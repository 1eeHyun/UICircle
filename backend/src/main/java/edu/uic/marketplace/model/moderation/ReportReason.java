package edu.uic.marketplace.model.moderation;

public enum ReportReason {
    // Spam or misleading content
    SPAM("Spam or misleading content"),

    // Inappropriate or offensive content
    INAPPROPRIATE("Inappropriate or offensive content"),

    // Scam or fraudulent activity
    SCAM("Scam or fraudulent activity"),

    // Prohibited items
    PROHIBITED_ITEM("Prohibited items"),

    // Harassment or bullying
    HARASSMENT("Harassment or bullying"),

    // Counterfeit or stolen goods
    COUNTERFEIT("Counterfeit or stolen goods"),

    // Price gouging
    PRICE_GOUGING("Price gouging"),

    // Misleading description
    MISLEADING("Misleading description"),

    // Duplicate listing
    DUPLICATE("Duplicate listing"),

    // Other reason
    OTHER("Other");

    private final String displayName;

    ReportReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
