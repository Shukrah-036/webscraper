package com.scrapernest.webscraperthesismodel.repository;

import com.scrapernest.webscraperthesismodel.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}

