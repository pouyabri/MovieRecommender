package org.example.service;

import org.example.config.Neo4jConfig;
import org.example.model.IntentData;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;
import java.util.regex.Pattern;

public class GraphQueryService {
    private final Neo4jConfig neo4jConfig;
    // عدد ثابت برای محدود کردن تعداد رکوردها
    private static final int SUBGRAPH_LIMIT = 5; // هرچقدر بخواهید

    public GraphQueryService(Neo4jConfig neo4jConfig) {
        this.neo4jConfig = neo4jConfig;
    }

    /**
     * بر اساس intentData تصمیم می‌گیرد چه کوئری Cypher اجرا کند
     * و یک زیرگراف کوچک (در قالب JSON) برمی‌گرداند.
     */
    public String getSubgraph(IntentData intentData) {
        String intentType = intentData.getIntentType();
        String entity = intentData.getEntity();

        try (Session session = neo4jConfig.getDriver().session()) {

            if ("recommend_movie".equalsIgnoreCase(intentType)) {
                // اگر کاربر چند فیلم را در entity برگردانده باشد
                // مثلا "Jumanji, Toy Story" یا "Jumanji and Toy Story"
                // می‌توانیم با split جدا کنیم:
                List<Record> allRecords = new ArrayList<>();

                // جدا کردن بر اساس کاما یا واژه and (به صورت غیرحساس به حروف کوچک/بزرگ)
                String[] movieParts = entity.split("(?i)and|,");
                // مثال: "Jumanji, Toy Story" => ["Jumanji", " Toy Story"]

                for (String part : movieParts) {
                    String movieName = part.trim();
                    if (movieName.isEmpty()) {
                        continue;
                    }
                    // کمی تمیز کردن رشته (حرف اول بزرگ، بقیه کوچک) - دلخواه است
                    String fixedEntity = movieName.substring(0,1).toUpperCase()
                            + movieName.substring(1).toLowerCase();

                    // کوئری: فیلم‌هایی شبیه فیلم داده‌شده (از نظر ژانر)، با امتیاز بالا
                    String query =
                            "MATCH (m:Movie)-[:HAS_GENRE]->(g:Genre)<-[:HAS_GENRE]-(other:Movie) " +
                                    "WHERE toLower(m.title) CONTAINS toLower($movieTitle) AND m <> other " +
                                    "OPTIONAL MATCH (other)<-[r:RATED]-(:User) " +
                                    "WITH other, avg(r.rating) AS avgRating " +
                                    "ORDER BY avgRating DESC " +  // مرتب‌سازی فیلم‌های مشابه بر اساس امتیاز
                                    "LIMIT " + SUBGRAPH_LIMIT + " " +
                                    "RETURN other.id AS movieId, other.title AS title, avgRating";

                    // اجرای کوئری برای هر فیلم
                    Result result = session.run(query, Values.parameters("movieTitle", fixedEntity));

                    // جمع کردن نتایج در allRecords
                    while (result.hasNext()) {
                        allRecords.add(result.next());
                    }
                }

                // حالا allRecords حاوی نتایج چند کوئری است
                // برای ساخت یک JSON نهایی از همهٔ آن‌ها:
                return buildJsonFromRecords(allRecords, "recommendedMovies");
            }

            else if ("find_movie_by_genre".equalsIgnoreCase(intentType)) {
                // اگر intent ژانر باشد
                if (entity == null || entity.isEmpty()) {
                    entity = "Comedy";
                }

                String query =
                        "MATCH (m:Movie)-[:HAS_GENRE]->(g:Genre) " +
                                "WHERE toLower(g.name) = toLower($genre) " +
                                "OPTIONAL MATCH (m)<-[r:RATED]-(:User) " +
                                "WITH m, avg(r.rating) AS avgRating " +
                                "ORDER BY avgRating DESC " +
                                "LIMIT " + SUBGRAPH_LIMIT + " " +
                                "RETURN m.id AS movieId, m.title AS title, avgRating";

                Result result = session.run(query, Values.parameters("genre", entity));
                return buildJsonFromResult(result, "moviesByGenre", result);
            }

            else {
                // ناشناخته
                return "{\"error\":\"Unknown intent: " + intentType + "\"}";
            }
        }
    }

    /**
     * ساخت JSON از چندین Record که در یک لیست جمع شده است (مثلاً پس از چند کوئری)
     */
    private String buildJsonFromRecords(List<Record> records, String arrayName) {
        // اگر بخواهید از بازگشت رکوردهای تکراری جلوگیری کنید،
        // می‌توانید با استفاده از یک Set، فقط رکوردهای با movieId منحصربه‌فرد را نگه دارید.
        // اینجا به عنوان نمونه خیلی ساده جلو می‌رویم.

        StringBuilder sb = new StringBuilder();
        sb.append("{\"").append(arrayName).append("\":[");

        List<String> items = new ArrayList<>();
        for (Record rec : records) {
            int movieId = rec.get("movieId").asInt();
            String title = rec.get("title").asString();

            double avgRating = 0.0;
            if (rec.containsKey("avgRating") && !rec.get("avgRating").isNull()) {
                avgRating = rec.get("avgRating").asDouble();
            }

            // ساختن یک قطعه‌ی JSON ساده
            String jsonItem = String.format(
                    "{\"title\":\"%s\"",
                    title
            );
            items.add(jsonItem);
        }

        sb.append(String.join(",", items));
        sb.append("]}");
        return sb.toString();
    }

    /**
     * ساخت JSON از نتیجهٔ یک کوئری منفرد
     * (برای intent find_movie_by_genre، که یک کوئری واحد اجرا می‌کنیم)
     */
    private String buildJsonFromResult(Result result, String arrayName, Result originalResult) {
        // در اینجا می‌توانیم مشابه متد بالا عمل کنیم
        // تا در صورت اجرای یک کوئری واحد هم خروجی JSON ساخته شود.
        List<Record> records = new ArrayList<>();
        while (result.hasNext()) {
            records.add(result.next());
        }
        return buildJsonFromRecords(records, arrayName);
    }
}
