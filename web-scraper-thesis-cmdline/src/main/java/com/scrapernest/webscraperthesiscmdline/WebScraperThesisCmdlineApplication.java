package com.scrapernest.webscraperthesiscmdline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scrapernest.webscraperthesismodel.model.*;
import com.scrapernest.webscraperthesismodel.repository.UserRepository;
import com.scrapernest.webscraperthesismodel.scraper.SystemErrorException;
import org.apache.commons.cli.*;
import org.hibernate.Hibernate;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.scrapernest.webscraperthesismodel"})
public class WebScraperThesisCmdlineApplication implements CommandLineRunner {

    @Autowired
    private ScraperController scraperController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    private final Scanner scanner;
    private Options options;

    private User currentUser;

    private final ObjectMapper objectMapper;


    public static void main(String[] args) {
        SpringApplication.run(WebScraperThesisCmdlineApplication.class, args);
    }

    public WebScraperThesisCmdlineApplication() {
        scanner = new Scanner(System.in);

        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());

        options = new Options();

        options.addOption("n", "name", true, "Name of Scraper");
        options.addOption("u", "url", true, "Target URL to scrape from");
        options.addOption("s", "selector", true, "CSS Selector");
        options.addOption("l", "label", true, "Label of selector");
        options.addOption("h", "help", false, "Print this help message");
    }

    @Override
    public void run(String... args) throws IOException {

        Logger.info("Welcome to Web Scraper Thesis Command Line Application!");

        authenticateUser();

        currentUser = userController.getCurrentUser();
        showOptions();

    }

    public void authenticateUser() {
        Logger.info("Choose an option:");
        Logger.info("Press 1 for Sign Up, 2 for Login, 3 to cancel");

        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                try {
                    userController.signUp();
                } catch (SystemErrorException e) {
                    Logger.error(e.getMessage());
                    Logger.info("Enter any key to go back to start.");
                    scanner.nextLine();
                    authenticateUser();
                }
                break;
            case 2:
                try {
                    userController.logIn();
                } catch (SystemErrorException e) {
                    Logger.error(e.getMessage());
                    Logger.info("Enter any key to go back to start.");
                    scanner.nextLine();
                    authenticateUser();
                }
                break;
            case 3:
                Logger.info("Cancelled.");
                System.exit(0);
                break;
            default:
                Logger.error("Invalid option.");
        }

    }

    public void showOptions() {
        try {
            Terminal terminal = TerminalBuilder.builder().build();

            String[] options = {"Profile", "Download Scraped Data", "Scrapers", "New Scraper", "Log Out"};
            int currentIndex = 0;

            Logger.info("Press 'j' to move down, 'k' to move up and 'y' to select the current option");
            printOptions(terminal, options, currentIndex);

            while (true) {
                int input = terminal.reader().read();
                switch (input) {
                    case 'y':
                        handleSelection(options[currentIndex]);
                        break;
                    case 'j':
                        currentIndex = (currentIndex + 1) % options.length;
                        printOptions(terminal, options, currentIndex);
                        break;
                    case 'k':
                        currentIndex = (currentIndex - 1 + options.length) % options.length;
                        printOptions(terminal, options, currentIndex);
                        break;
                    default:

                }
            }
        } catch (IOException e) {
            Logger.error("Error initializing terminal: " + e.getMessage());
        }
    }

    private void printOptions(Terminal terminal, String[] options, int currentIndex) {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            if (i == currentIndex) {
                sb.append("> ");
            } else {
                sb.append("  ");
            }
            sb.append(options[i]);
            sb.append(System.lineSeparator());
        }
        System.out.print(sb.toString());
    }

    private void handleSelection(String option) {
        switch (option.toLowerCase()) {
            case "profile":
                Logger.info("Opening PROFILE page");
                displayProfile();
                break;
            case "download scraped data":
                downloadScrapedData();
                break;

            case "scrapers":
                Logger.debug("opening PREVIOUS SCRAPERS page");
                displayPreviousScrapers();
                break;
            case "new scraper":
                Logger.info("Please provide command line arguments:");
                Logger.info("Format: -n <scraperName> -u <targetUrl> -s <selector1> -l <label1> ...");
                String extra = scanner.nextLine();
                String userInput = scanner.nextLine();
                List<String> argList = new ArrayList<>();
                StringBuilder currentArg = new StringBuilder();
                boolean inQuotes = false;

                for (char c : userInput.toCharArray()) {
                    if (c == '"') {
                        inQuotes = !inQuotes;
                    } else if (c == ' ' && !inQuotes) {

                        if (currentArg.length() > 0) {
                            argList.add(currentArg.toString());
                            currentArg.setLength(0);
                        }
                    } else {
                        currentArg.append(c);
                    }
                }

                if (currentArg.length() > 0) {
                    argList.add(currentArg.toString());
                }

                String[] parsedArgs = argList.toArray(new String[0]);
                processScraperArguments(parsedArgs);
                break;
            case "log out":
                Logger.info("Logged out.");
                System.exit(0);
                break;
            default:
                Logger.error("Invalid option.");
        }
    }


    public void processScraperArguments(String[] args) {
        Logger.info("Enter scraper details:");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WebScraperThesisCmdlineApplication", options);
                return;
            }

            String targetUrl = cmd.getOptionValue("u");
            String scraperName = cmd.getOptionValue("n");

            String[] selectors = cmd.getOptionValues("s");
            String[] labels = cmd.getOptionValues("l");

            if (selectors == null || labels == null || selectors.length == 0 || labels.length == 0
                    || selectors.length != labels.length) {
                System.err.println("Error: You must provide at least one pair of selector and label, " +
                        "and the number of selectors must match the number of labels.");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("WebScraperThesisCmdlineApplication", options);
                return;
            }

            List<Item> scraperItems = new ArrayList<>();
            for (int i = 0; i < selectors.length; i++) {
                scraperItems.add(Item.builder().selector(selectors[i]).label(labels[i]).build());
            }

            scraperController.setTargetUrl(targetUrl);
            scraperController.scrapeAndSaveResults(currentUser, scraperName, scraperItems);
            System.out.println("Scraping completed.");
        } catch (ParseException e) {
            System.err.println("Error parsing command line options: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WebScraperThesisCmdlineApplication", options);
        }
    }

    public void displayProfile() {
        System.out.println("*********************************YOUR PROFILE*********************************");
        System.out.println("______________________________________________________________________________");
        System.out.println("\n");
        System.out.println("~~~~~ Username: " + currentUser.getUsername());
        System.out.println("~~~~~ Email: " + currentUser.getEmail());
    }

    public void downloadScrapedData() {
        List<Scraper> userScrapers = currentUser.getScrapers();
        if (userScrapers != null && !userScrapers.isEmpty()) {
            try {
                String jsonData = objectMapper.writeValueAsString(userScrapers);

                File file = new File("scraped_data.json");
                objectMapper.writeValue(file, userScrapers);

                ByteArrayResource resource = new ByteArrayResource(jsonData.getBytes());
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scraped_data.json");

                ResponseEntity<ByteArrayResource> responseEntity = ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(resource);

                System.out.println("Downloading scraped data...");
                System.out.println("File saved as scraped_data.json");

            } catch (IOException e) {
                Logger.error("Error while downloading scraped data: " + e.getMessage());
            }
        } else {
            Logger.info("No scraped data available to download.");
        }
    }

    @Transactional
    public void displayPreviousScrapers() {
        System.out.println("****************************YOUR PREVIOUS SCRAPERS****************************");
        System.out.println("______________________________________________________________________________");
        System.out.println("\n");

        List<Scraper> userScrapers = currentUser.getScrapers();
        if (userScrapers != null){
            System.out.println("~~~~~ These are all your previous scrapers ~~~~~:");
            for (Scraper scraper : userScrapers) {
                Hibernate.initialize(scraper.getScraperItems());
                Hibernate.initialize(scraper.getScraperResults());
                System.out.println("***: " + scraper + "\n");
            }
        } else {
            System.out.println("You had not previously made any scrapers!");
            System.out.println("To make your first scraper, press j to return to the options menu page and get scraping!");
        }
    }
}