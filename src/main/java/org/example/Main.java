package org.example;

import org.example.config.Neo4jConfig;
import org.example.llm.LLMService;
import org.example.service.*;
import org.example.model.IntentData;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // اتصال به Neo4j
        Neo4jConfig config = new Neo4jConfig("bolt://localhost:7687", "neo4j", "12345678");

//        loader.loadMovies("src/main/resources/movies.csv");
//        System.out.println("movies loaded...");
//
//        loader.loadRatings("src/main/resources/ratings.csv");
//        System.out.println("Data loaded into Neo4j successfully!");


        // ساخت سرویس‌های مورد نیاز
        String groqApiKey = "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";
        LLMService llmService = new LLMService(groqApiKey);
        IntentDetector intentDetector = new IntentDetector(llmService);
        GraphQueryService graphQueryService = new GraphQueryService(config);
        AnswerGenerator answerGenerator = new AnswerGenerator(llmService);

        // دریافت ورودی از کاربر
        Scanner scanner = new Scanner(System.in);
        String userPrompt = scanner.nextLine();

        // تشخیص intent و entity
        IntentData intentData = intentDetector.detectIntent(userPrompt);

        // دریافت داده‌های زیرگراف از گراف
        String subgraphJson = graphQueryService.getSubgraph(intentData);

        // تولید پاسخ نهایی
        String finalAnswer = answerGenerator.generateAnswer(userPrompt, subgraphJson);
        System.out.println(finalAnswer);

        // بستن اتصال به Neo4j
        config.close();
        scanner.close();
    }
}

//main code with extra explanation

//package org.example;
//
//import org.example.config.Neo4jConfig;
//import org.example.llm.LLMService;
//import org.example.model.IntentData;
//import org.example.service.*;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("Program started...");
//
//        // فرض بر اینکه می‌خواهید به Neo4j وصل شوید:
//        String uri = "bolt://localhost:7687";
//        String username = "neo4j";
//        String password = "12345678";
//
//        Neo4jConfig config = new Neo4jConfig(uri, username, password);
//        System.out.println("Neo4jConfig created...");
//
//        // بارگذاری دیتاست CSV
//        GraphLoader loader = new GraphLoader(config);
//        System.out.println("GraphLoader created...");
//
//        loader.loadMovies("C:/Users/PouyaBRI/Desktop/ml-latest-small/ml-latest-small/movies.csv");
//        System.out.println("movies loaded...");
//
//        loader.loadRatings("C:/Users/PouyaBRI/Desktop/ml-latest-small/ml-latest-small/ratings.csv");
//        System.out.println("Data loaded into Neo4j successfully!");
//
//        // ساخت سرویس‌های LLM و Intent
//        String groqApiKey = "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";
//        LLMService llmService = new LLMService(groqApiKey);
//
//        IntentDetector intentDetector = new IntentDetector(llmService);
//        GraphQueryService graphQueryService = new GraphQueryService(config);
//        AnswerGenerator answerGenerator = new AnswerGenerator(llmService);
//
//        // پیام خوشامد
//        System.out.println("Ask me about movies! (e.g. 'Recommend movies' or 'Find me comedy movies')");
//
//        // گرفتن ورودی از کاربر
//        Scanner sc = new Scanner(System.in);
//        String userPrompt = sc.nextLine();
//
//        // تشخیص Intent
//        IntentData intentData = intentDetector.detectIntent(userPrompt);
//        System.out.println("Detected intent: " + intentData.getIntentType() +
//                ", entity: " + intentData.getEntity());
//
//        // کوئری به گراف
//        String subgraphJson = graphQueryService.getSubgraph(intentData);
//        System.out.println("Subgraph JSON = " + subgraphJson);
//
//        // تولید پاسخ
//        String finalAnswer = answerGenerator.generateAnswer(userPrompt, subgraphJson);
//        System.out.println(finalAnswer);
//
//        // پایان
//        config.close();
//        sc.close();
//    }
//}
