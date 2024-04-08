package com.scrapernest.webscraperthesismodel.model;

import com.scrapernest.webscraperthesismodel.repository.UserRepository;
import com.scrapernest.webscraperthesismodel.scraper.SystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.util.Optional;
import java.util.Scanner;

@Component
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final Scanner scanner;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        scanner = new Scanner(System.in);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signUp() {
        Logger.info("Enter new username: ");
        String username = scanner.nextLine().trim();

        if (userRepository.existsByUsername(username)) {
            throw new SystemErrorException("SORRY, THIS USER ALREADY EXISTS");
        }

        Logger.info("Enter new email address: ");
        String email = scanner.nextLine().trim();

        if (userRepository.existsByEmail(email)) {
            throw new SystemErrorException("SORRY, THIS USER ALREADY EXISTS");
        }

        Logger.info("Enter password:");
        String password = scanner.nextLine().trim();

        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(newUser);
        Logger.info( username + ": signed up successfully");
    }

    public void logIn() {
        Logger.info("Enter your email address:");
        String email = scanner.nextLine().trim();

        Logger.info("Enter password:");
        String password = scanner.nextLine().trim();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();

            if(passwordEncoder.matches(password, user.getPassword())) {
                Logger.info("Login successful.");

            } else {
                throw new SystemErrorException("INCORRECT PASSWORD");
            }
        } else {
            throw new SystemErrorException("USER CANNOT BE FOUND");
        }
    }

}
