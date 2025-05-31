package com.jsonplaceholder.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Company {
    private String name;
    private String catchPhrase;
    private String bs;
} 