package com.scrapernest.webscraperthesismodel.model;

import com.scrapernest.webscraperthesismodel.repository.ScraperResultRepository;
import lombok.*;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "scrapers")
public class Scraper {

    private static final int scrapingIntervalMinutes = 60; //**UPDATE TO QUARTZ LATER**

    @Id
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String targetUrl;

    @Getter
    @Setter
    private String scrapingParameters;

    @Getter
    @Setter
    private List<Item> scraperItems;

    @Getter
    @Setter
    private List<ScraperResult> scraperResults;

    @Getter
    @Setter
    private boolean loaded = false;

    @Autowired
    private ScraperResultRepository scraperResultRepository;

    public List<ScraperResult> getScraperResults() {
        if (!loaded) {
            this.scraperResults = loadScraperResultsFromDatabase();
            loaded = true;
        }
        return scraperResults;
    }


    public void execute() {

        try {
            Logger.info("Executing scraper for: {}", name);
            for (Item item : scraperItems) {
                Logger.info("Scraping data for item: {}", item.getLabel());
                org.jsoup.nodes.Document doc = Jsoup.connect(targetUrl).get();

                Logger.debug(doc.title());
                List<String> extractedData = new ArrayList<>();

                Elements elements = doc.select(item.getSelector());
                Logger.debug(elements);
                for (Element element : elements) {
                    Logger.debug(element);
                    String data = element.text();
                    extractedData.add(data);
                }
                List<Item> associatedItems = new ArrayList<>();
                associatedItems.add(item);

                ScraperResult scraperResult = ScraperResult.builder()
                        .id(new ObjectId().toString())
                        .scraperName(name)
                        .timeStamp(LocalDateTime.now())
                        .extractedData(extractedData)
                        .associatedItems(associatedItems)
                        .build();

                List<ScraperResult> updatedResults = new ArrayList<>(scraperResults);
                updatedResults.add(scraperResult);
                scraperResults = updatedResults;

                Logger.info("Execution completed successfully.");
            }
            Logger.debug("Executed :)");
        } catch (IOException e) {

            Logger.error("Error occurred during web scraping: {}", e.getMessage(), e);
        }
    }

    private List<ScraperResult> loadScraperResultsFromDatabase() {

        return scraperResultRepository.findByScraperName(name);
    }

}