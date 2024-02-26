package com.scrapernest.webscraperthesiscmdline;

import com.scrapernest.webscraperthesismodel.model.Item;
import com.scrapernest.webscraperthesismodel.model.ScraperController;
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
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter targetUrl to scrape from: ");
        String targetUrl = scanner.nextLine();

        System.out.print("Enter scraperName: ");
        String scraperName = scanner.nextLine();

        List<Item> scraperItems = new ArrayList<>();

        while (true) {
            System.out.print("Enter selector (or 'exit' to finish): ");
            String selector = scanner.nextLine();

            if ("exit".equalsIgnoreCase(selector.trim())) {
                break;
            }

            System.out.print("Enter label: ");
            String label = scanner.nextLine();

            scraperItems.add(Item.builder().selector(selector).label(label).build());
        }

        scraperController.setTargetUrl(targetUrl);
        scraperController.scrapeAndSaveResults(scraperName, scraperItems);

        System.out.println("Scraping completed.");
    }

}
