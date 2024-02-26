package com.scrapernest.webscraperthesismodel.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scraperItems")
public class Item {

    @Id
    private String id;

    @Getter
    @Setter
    private String selector;

    @Getter
    @Setter
    private String label;

}
