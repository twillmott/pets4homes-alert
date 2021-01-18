package com.twillmott.pets4homesalert.model;

import lombok.Data;

@Data
public class ScraperConfig {
    private final String animalId;
    private final String breedId;
    private final String sort;
}
