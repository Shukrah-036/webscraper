package com.scrapernest.webscraperthesismodel.repository;

import com.scrapernest.webscraperthesismodel.model.ScraperResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScraperResultRepository extends MongoRepository<ScraperResult, String> {
}
