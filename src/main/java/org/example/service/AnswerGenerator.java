package org.example.service;

import org.example.llm.LLMService;

public class AnswerGenerator {
    private final LLMService llmService;

    public AnswerGenerator(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * زیرگراف (در قالب JSON) + پرامپت کاربر را به LLM می‌دهیم تا پاسخ نهایی را برگرداند.
     */
    public String generateAnswer(String originalPrompt, String subgraphJson) {
        String systemPrompt =
                "You are a movie domain assistant specializing in recommending movies. " +
                        "The following JSON data contains movies that match the user's request.\n\n" +
                        "User's question: \"" + originalPrompt + "\"\n\n" +
                        "Here is a list of movies directly matching the request (extracted from the knowledge graph):\n" +
                        subgraphJson + "\n\n" +
                        "Do not say that the list is incomplete. Just assume that these are the best matching movies.\n" +
                        "Do not try to describe movies just write down the datas that came from knowledge graph.\n"+
                        "Do not show ratings.";


        // پیام system را می‌فرستیم؛ userPrompt را می‌توانیم خالی بگذاریم
        String finalAnswer = llmService.sendChatPrompt(systemPrompt, "");
        return finalAnswer;
    }
}
