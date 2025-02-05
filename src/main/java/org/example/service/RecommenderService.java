// we do not uwse this code in this project

package org.example.service;

import org.example.config.Neo4jConfig;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * یک سرویس ساده برای توصیه فیلم، صرفاً جهت مثال
 * (در صورت نیاز می‌توانید حذفش کنید)
 */
public class RecommenderService {
    private final Neo4jConfig neo4jConfig;

    public RecommenderService(Neo4jConfig neo4jConfig) {
        this.neo4jConfig = neo4jConfig;
    }

    public List<String> recommendMoviesForUser(int userId) {
        List<String> recommended = new ArrayList<>();
        String query =
                "MATCH (m:Movie)-[r:RATED]->(other:User) " +
                        "WHERE r.rating >= 4 AND NOT ((m)<-[:RATED]-(u:User {id:$userId})) " +
                        "RETURN m.title AS title LIMIT 10";

        try (Session session = neo4jConfig.getDriver().session()) {
            Result result = session.run(query, Values.parameters("userId", userId));
            while (result.hasNext()) {
                Record rec = result.next();
                recommended.add(rec.get("title").asString());
            }
        }
        return recommended;
    }
}
