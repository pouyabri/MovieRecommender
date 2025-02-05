package org.example.model;

public class IntentData {
    private String intentType;      // مثلاً "recommend_movie", "find_movie_by_genre", ...
    private String entity;          // پارامتری استخراج‌شده از پرامپت (مثل ژانر یا userId)
    private String originalPrompt;  // خودِ پرامپت کاربر

    public IntentData(String intentType, String entity, String originalPrompt) {
        this.intentType = intentType;
        this.entity = entity;
        this.originalPrompt = originalPrompt;
    }

    public String getIntentType() {
        return intentType;
    }
    public void setIntentType(String intentType) {
        this.intentType = intentType;
    }
    public String getEntity() {
        return entity;
    }
    public void setEntity(String entity) {
        this.entity = entity;
    }
    public String getOriginalPrompt() {
        return originalPrompt;
    }
    public void setOriginalPrompt(String originalPrompt) {
        this.originalPrompt = originalPrompt;
    }
}