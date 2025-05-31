package com.jsonplaceholder.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private GeoDto geo;
} 