package org.example.service;

import org.example.config.Neo4jConfig;
import org.example.model.Genre;
import org.example.model.Movie;
import org.example.model.User;
import org.example.utils.CsvUtils;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.List;

public class GraphLoader {
    private final Neo4jConfig neo4jConfig;

    public GraphLoader(Neo4jConfig neo4jConfig) {
        this.neo4jConfig = neo4jConfig;
    }

    /**
     * بارگذاری لیست فیلم‌ها از فایل CSV با ساختار movieId,title,genres
     */
    public void loadMovies(String moviesCsvPath) {
        List<String[]> rows = CsvUtils.readCsv(moviesCsvPath);

        try (Session session = neo4jConfig.getDriver().session()) {
            for (String[] row : rows) {
                int movieId = Integer.parseInt(row[0]);
                String title = row[1];
                String genresStr = row[2];

                Movie movie = new Movie(movieId, title);
                String[] genreArr = genresStr.split("\\|");

                session.writeTransaction(tx -> {
                    tx.run("MERGE (m:Movie {id:$mId}) " +
                                    "ON CREATE SET m.title=$title",
                            Values.parameters("mId", movie.getId(), "title", movie.getTitle()));

                    // درج ژانرها
                    for (String g : genreArr) {
                        Genre genre = new Genre(g);
                        tx.run("MERGE (gen:Genre {name:$gName})",
                                Values.parameters("gName", genre.getName()));
                        // رابطه HAS_GENRE
                        tx.run("MATCH (m:Movie {id:$mId}), (gen:Genre {name:$gName}) " +
                                        "MERGE (m)-[:HAS_GENRE]->(gen)",
                                Values.parameters("mId", movie.getId(), "gName", genre.getName()));
                    }
                    return null;
                });
            }
        }
    }

    /**
     * بارگذاری امتیاز (رابطه RATED) از فایل CSV با ساختار userId,movieId,rating,timestamp
     */
    public void loadRatings(String ratingsCsvPath) {
        List<String[]> rows = CsvUtils.readCsv(ratingsCsvPath);

        try (Session session = neo4jConfig.getDriver().session()) {
            for (String[] row : rows) {
                int userId = Integer.parseInt(row[0]);
                int movieId = Integer.parseInt(row[1]);
                double rating = Double.parseDouble(row[2]);
                long timestamp = Long.parseLong(row[3]);

                User user = new User(userId);

                session.writeTransaction(tx -> {
                    tx.run("MERGE (u:User {id:$uId})",
                            Values.parameters("uId", user.getId()));
                    tx.run("MERGE (m:Movie {id:$mId})",
                            Values.parameters("mId", movieId));
                    tx.run("MATCH (u:User {id:$uId}), (m:Movie {id:$mId}) " +
                                    "CREATE (u)-[:RATED {rating:$r, ts:$ts}]->(m)",
                            Values.parameters("uId", user.getId(), "mId", movieId,
                                    "r", rating, "ts", timestamp));
                    return null;
                });
            }
        }
    }
}
