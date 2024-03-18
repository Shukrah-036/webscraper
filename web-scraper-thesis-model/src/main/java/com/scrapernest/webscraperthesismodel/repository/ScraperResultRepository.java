package com.scrapernest.webscraperthesismodel.repository;

import com.scrapernest.webscraperthesismodel.model.ScraperResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScraperResultRepository extends MongoRepository<ScraperResult, String> {

    List<ScraperResult> findByScraperName(String scraperName);
}
