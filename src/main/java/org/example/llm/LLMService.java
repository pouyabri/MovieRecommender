package org.example.llm;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

/**
 * LLMService: ارتباط با API مدل Llama3-70B در Groq
 */
public class LLMService {
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final String apiKey;

    public LLMService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String sendChatPrompt(String systemPrompt, String userPrompt) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "llama3-70b-8192");

        JSONArray messages = new JSONArray();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.put(systemMsg);
        }

        if (userPrompt != null && !userPrompt.isEmpty()) {
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);
            messages.put(userMsg);
        }

        jsonBody.put("messages", messages);
        jsonBody.put("temperature", 1);
        jsonBody.put("max_completion_tokens", 1024);
        jsonBody.put("top_p", 1);
        jsonBody.put("stream", false);

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(GROQ_API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Groq API Error: " + response;
            }
            String responseBody = response.body().string();
            return parseGroqResponse(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error calling Groq API: " + e.getMessage();
        }
    }

    private String parseGroqResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray choices = json.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject messageObj = choice.getJSONObject("message");
                return messageObj.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }
}


// old version of this code


//package org.example.llm;
//
//import okhttp3.*;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import java.io.IOException;
//
///**
// * LLMService: ارتباط با API مدل Llama3-70B در Groq
// */
//public class LLMService {
//    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
//    private final String apiKey;
//
//    public LLMService(String apiKey) {
//        this.apiKey = apiKey;
//    }
//
//    /**
//     * ارسال درخواست به API Groq
//     */
//    public String sendChatPrompt(String systemPrompt, String userPrompt) {
//        OkHttpClient client = new OkHttpClient();
//
//        // ایجاد JSON درخواست
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("model", "llama3-70b-8192");
//
//        JSONArray messages = new JSONArray();
//
//        if (systemPrompt != null && !systemPrompt.isEmpty()) {
//            JSONObject systemMsg = new JSONObject();
//            systemMsg.put("role", "system");
//            systemMsg.put("content", systemPrompt);
//            messages.put(systemMsg);
//        }
//
//        if (userPrompt != null && !userPrompt.isEmpty()) {
//            JSONObject userMsg = new JSONObject();
//            userMsg.put("role", "user");
//            userMsg.put("content", userPrompt);
//            messages.put(userMsg);
//        }
//
//        jsonBody.put("messages", messages);
//        jsonBody.put("temperature", 1);
//        jsonBody.put("max_completion_tokens", 1024);
//        jsonBody.put("top_p", 1);
//        jsonBody.put("stream", false); // می‌توان `true` کرد ولی مدیریت استریم نیاز به تغییر در کد دارد
//
//        // ساخت RequestBody
//        RequestBody body = RequestBody.create(
//                jsonBody.toString(),
//                MediaType.parse("application/json; charset=utf-8")
//        );
//
//        // ایجاد درخواست HTTP
//        Request request = new Request.Builder()
//                .url(GROQ_API_URL)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer " + apiKey)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                return "Groq API Error: " + response;
//            }
//            String responseBody = response.body().string();
//            return parseGroqResponse(responseBody);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error calling Groq API: " + e.getMessage();
//        }
//    }
//
//    /**
//     * استخراج متن پاسخ از JSON دریافتی از API Groq
//     */
//    private String parseGroqResponse(String responseBody) {
//        try {
//            JSONObject json = new JSONObject(responseBody);
//            JSONArray choices = json.getJSONArray("choices");
//            if (choices.length() > 0) {
//                JSONObject choice = choices.getJSONObject(0);
//                JSONObject messageObj = choice.getJSONObject("message");
//                return messageObj.getString("content");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return responseBody; // بازگشت کامل JSON در صورت خطا
//    }
//}