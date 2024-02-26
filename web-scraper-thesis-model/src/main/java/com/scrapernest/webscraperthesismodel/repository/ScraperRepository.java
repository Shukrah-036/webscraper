package com.scrapernest.webscraperthesismodel.repository;

import com.scrapernest.webscraperthesismodel.model.Scraper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScraperRepository extends MongoRepository<Scraper, String> {
}
