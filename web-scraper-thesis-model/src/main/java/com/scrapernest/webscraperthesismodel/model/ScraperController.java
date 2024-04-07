package com.scrapernest.webscraperthesismodel.model;


import com.scrapernest.webscraperthesismodel.repository.ItemRepository;
import com.scrapernest.webscraperthesismodel.repository.ScraperRepository;
import com.scrapernest.webscraperthesismodel.repository.ScraperResultRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

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

    @Setter
    private String targetUrl;


    public void scrapeAndSaveResults(String scraperName, List<Item> scraperItems) {
        Logger.info("Starting scraping process...");

        Scraper scraper = new Scraper();
        scraper.setTargetUrl(targetUrl);
        scraper.setName(scraperName);
        scraper.setScraperItems(scraperItems);
        scraper.setScraperResults(List.of());
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

    }

    private boolean hasResultChanged(ScraperResult existingResult, ScraperResult newResult) {
        return !existingResult.getScraperName().equals(newResult.getScraperName()) ||
                !new HashSet<>(existingResult.getExtractedData()).equals(new HashSet<>(newResult.getExtractedData()));
    }
}
