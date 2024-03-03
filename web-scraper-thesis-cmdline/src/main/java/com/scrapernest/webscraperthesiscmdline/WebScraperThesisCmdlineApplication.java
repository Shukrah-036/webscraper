package com.scrapernest.webscraperthesiscmdline;

import com.scrapernest.webscraperthesismodel.model.Item;
import com.scrapernest.webscraperthesismodel.model.ScraperController;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@ComponentScan(basePackages = {"com.scrapernest.webscraperthesismodel"})
public class WebScraperThesisCmdlineApplication implements CommandLineRunner {

    @Autowired
    private ScraperController scraperController;

    public static void main(String[] args) {
        SpringApplication.run(WebScraperThesisCmdlineApplication.class, args);
    }


    @Override
    public void run(String... args) {
        Options options = new Options();

        options.addOption("n", "name", true, "Name of Scraper");
        options.addOption("u", "url", true, "Target URL to scrape from");
        options.addOption("s", "selector", true, "CSS Selector");
        options.addOption("l", "label", true, "Label of selector");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            String targetUrl = cmd.getOptionValue("u");
            String scraperName = cmd.getOptionValue("n");

            String[] selectors = cmd.getOptionValues("s");
            String[] labels = cmd.getOptionValues("l");

            if (selectors == null || labels == null || selectors.length == 0 || labels.length == 0
                    || selectors.length != labels.length){
                System.err.println("Error: You must provide at least one pair of selector and label, " +
                        "and the number of selectors must match the number of labels.");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WebScraperThesisCmdlineApplication", options);
                return;
            }

            List<Item> scraperItems = new ArrayList<>();
            for (int i = 0; i < selectors.length; i++){
                scraperItems.add(Item.builder().selector(selectors[i]).label(labels[i]).build());
            }

            scraperController.setTargetUrl(targetUrl);
            scraperController.scrapeAndSaveResults(scraperName, scraperItems);
            System.out.println("Scraping completed.");
        }
        catch (ParseException e){
            System.err.println("Error parsing command line options: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WebScraperThesisCmdlineApplication", options);
        }

    }

}
