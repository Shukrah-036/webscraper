package com.scrapernest.webscraperthesismodel.scraper;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Component;

@Component
public class MongoDBConnection {
    public static MongoDatabase getDatabase() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/scrapernest");
        return mongoClient.getDatabase("scrapernest");
    }
}

