package com.scrapernest.webscraperthesismodel.repository;

import com.scrapernest.webscraperthesismodel.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsername(String username);

    Optional<User> findByEmail(String email);
}

