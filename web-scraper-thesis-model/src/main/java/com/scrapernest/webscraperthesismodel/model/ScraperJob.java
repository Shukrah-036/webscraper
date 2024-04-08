package com.scrapernest.webscraperthesismodel.model;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ScraperJob implements Job {

    @Autowired
    private Scraper scraper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        scraper.execute();
    }
}
