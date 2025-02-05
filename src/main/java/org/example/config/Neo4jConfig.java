package org.example.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 * کلاس پیکربندی برای اتصال به Neo4j
 */
public class Neo4jConfig {
    private Driver driver;

    public Neo4jConfig(String uri, String username, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    public Driver getDriver() {
        return driver;
    }

    public void close() {
        if (driver != null) {
            driver.close();
        }
    }
}

// old version of this code




//package org.example.config;
//
//import org.neo4j.driver.AuthTokens;
//import org.neo4j.driver.Driver;
//import org.neo4j.driver.GraphDatabase;
//
///**
// * کلاس پیکربندی برای اتصال به Neo4j
// */
//public class Neo4jConfig {
//    private final Driver driver;
//
//    public Neo4jConfig(String uri, String username, String password) {
//        driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
//    }
//
//    public Driver getDriver() {
//        return driver;
//    }
//
//    public void close() {
//        driver.close();
//    }
//}