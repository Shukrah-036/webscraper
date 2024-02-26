package com.scrapernest.webscraperthesismodel.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.scrapernest.webscraperthesismodel.repository"})
@ComponentScan(basePackages = {"com.scrapernest.webscraperthesismodel"})
public class WebScraperThesisModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebScraperThesisModelApplication.class, args);
    }

}
