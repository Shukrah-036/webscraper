package com.scrapernest.webscraperthesismodel.scraper;

public class SystemErrorException extends RuntimeException {
    public SystemErrorException(String message) {
        super(message);
    }
}
