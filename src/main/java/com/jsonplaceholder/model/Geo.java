package com.jsonplaceholder.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Geo {
    private String lat;
    private String lng;
} 