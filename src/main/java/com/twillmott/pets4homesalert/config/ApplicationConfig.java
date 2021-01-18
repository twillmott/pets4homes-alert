package com.twillmott.pets4homesalert.config;

import com.twillmott.pets4homesalert.model.ScraperConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.twillmott.pets4homesalert.config.DefaultConfig.*;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public ScraperConfig scraperConfig(@Value("${application.scraper.animal}") String animalId,
                                       @Value("${application.scraper.breed}") String breedId,
                                       @Value("${application.scraper.sort}") String sort) {
        return new ScraperConfig(defaultIfBlank(animalId, ANIMAL_ID),
                defaultIfBlank(breedId, BREED_ID),
                defaultIfBlank(sort, SCRAPER_SORT));
    }
}

