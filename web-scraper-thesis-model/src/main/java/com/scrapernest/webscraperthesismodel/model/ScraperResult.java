package com.scrapernest.webscraperthesismodel.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scraperResults")
public class ScraperResult {

    @Id
    private String id;

    @Getter
    @Setter
    private String scraperName;

    @Getter
    @Setter
    private LocalDateTime timeStamp;

    @Getter
    @Setter
    private List<String> extractedData;

    @Getter
    @Setter
    @DBRef(lazy = true)
    private List<Item> associatedItems;

}
