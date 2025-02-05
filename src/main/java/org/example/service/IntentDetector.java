package org.example.service;

import org.example.llm.LLMService;
import org.example.model.IntentData;
import org.json.JSONArray;
import org.json.JSONObject;

public class IntentDetector {
    private final LLMService llmService;

    public IntentDetector(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * ارسال پرامپت کاربر به LLM برای تشخیص intent و entity
     */
    public IntentData detectIntent(String userPrompt) {
        // یک systemPrompt ساده می‌سازیم تا مدل را راهنمایی کنیم
        String systemPrompt =
                "You are an intent classifier for a movie recommender system.\n" +
                        "Given the user's message, extract:\n" +
                        " - The intent (e.g., recommend_movie, find_movie_by_genre, ...)\n" +
                        " - Any relevant entity (e.g., a genre name or a user ID)\n" +
                        "Output the result in JSON with keys 'intent' and 'entity'.\n";

        // پیام را به LLMService می‌فرستیم
        String llmResponse = llmService.sendChatPrompt(systemPrompt, userPrompt);

        // JSON پاسخ را پارس می‌کنیم
        String intentType = "unknown";
        String entity = null;
        try {
            JSONObject obj = new JSONObject(llmResponse);

            if (obj.has("intent")) {
                intentType = obj.getString("intent");
            }

            if (obj.has("entity")) {
                Object ent = obj.get("entity");  // ممکن است String یا JSONArray باشد

                if (ent instanceof String) {
                    // اگر یک رشته باشد
                    entity = (String) ent;
                } else if (ent instanceof JSONArray) {
                    // اگر آرایه‌ای از رشته‌ها باشد
                    JSONArray arr = (JSONArray) ent;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < arr.length(); i++) {
                        if (i > 0) sb.append(", ");  // جداکننده
                        sb.append(arr.getString(i));
                    }
                    entity = sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new IntentData(intentType, entity, userPrompt);
    }
}
