package com.scrapernest.webscraperthesismodel.scraper;

import com.scrapernest.webscraperthesismodel.model.Scraper;
import com.scrapernest.webscraperthesismodel.model.ScraperJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail scraperJobDetail() {
        return JobBuilder.newJob(ScraperJob.class)
                .withIdentity("scraperJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger scraperJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(24) // Set your desired interval here
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(scraperJobDetail())
                .withIdentity("scraperTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
