package com.scrapernest.webscraperthesismodel.repository;

import com.scrapernest.webscraperthesismodel.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
}

