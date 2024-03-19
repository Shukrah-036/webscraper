package com.scrapernest.webscraperthesismodel.model;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.scrapernest.webscraperthesismodel.repository.ItemRepository;
import com.scrapernest.webscraperthesismodel.repository.ScraperRepository;
import com.scrapernest.webscraperthesismodel.repository.ScraperResultRepository;
import com.scrapernest.webscraperthesismodel.scraper.MongoDBConnection;
import lombok.Setter;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class ScraperController {

    @Autowired
    private ScraperRepository scraperRepository;

    @Autowired
    private ScraperResultRepository scraperResultRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MongoDBConnection mongoDBConnection;

    @Setter
    private String targetUrl;

    private boolean loaded = false;

    public void scrapeAndSaveResults(String scraperName, List<Item> scraperItems) {
        Logger.info("Starting scraping process...");

        List<ScraperResult> scraperResults = new ArrayList<>();

        Logger.debug("Checking if scraper results need to be loaded...");
        if (!loaded) {
            Logger.debug("Scraper results are being loaded for the first time...");
            scraperResults = scraperResultRepository.findByScraperName(scraperName);
            loaded = true;
        } else {
            Logger.debug("Scraper results have already been loaded previously...");
        }

        Scraper scraper = new Scraper();
        scraper.setTargetUrl(targetUrl);
        scraper.setName(scraperName);
        scraper.setScraperItems(scraperItems);
        scraper.setScraperResults(scraperResults != null ? scraperResults : List.of());
        scraper.execute();

        for (int i = 0; i < scraper.getScraperResults().size(); i++) {

            ScraperResult scraperResult = scraper.getScraperResults().get(i);

            List<Item> savedItems = itemRepository.saveAll(scraperResult.getAssociatedItems());
            scraperResult.setAssociatedItems(savedItems);

            scraperResultRepository.save(scraperResult);

            ScraperResult existingResult = scraperResultRepository.findById(scraperResult.getId()).orElse(null);
            if (existingResult != null && hasResultChanged(existingResult, scraperResult)) {
                Logger.info("Scrape result has changed!");
            }
            else if (existingResult == null){
                Logger.info("Scrape result is new.");
            }
            else {
                Logger.info("Scraper result has not changed.");
            }
        }
        scraperRepository.save(scraper);
        Logger.info("Scraping process completed.");

        loaded = false;
    }

    private boolean hasResultChanged(ScraperResult existingResult, ScraperResult newResult) {
        return !existingResult.getScraperName().equals(newResult.getScraperName()) ||
                !new HashSet<>(existingResult.getExtractedData()).equals(new HashSet<>(newResult.getExtractedData()));
    }
}
