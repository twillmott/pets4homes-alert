package com.twillmott.pets4homesalert.service;

import com.twillmott.pets4homesalert.dto.Notification;
import com.twillmott.pets4homesalert.entity.PetAdvertEntity;
import com.twillmott.pets4homesalert.repository.PetAdvertRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PetService {

    private final NotificationService notificationService;
    private final PetAdvertRepository petAdvertRepository;
    private final PetScraper petScraper;

    @Autowired
    public PetService(NotificationService notificationService,
                      PetAdvertRepository petAdvertRepository,
                      PetScraper petScraper) {
        this.notificationService = notificationService;
        this.petAdvertRepository = petAdvertRepository;
        this.petScraper = petScraper;
    }

    @Scheduled(fixedDelayString = "${application.crawlPeriod}")
    public void findMeADog() throws IOException, ParseException {

        // Find the most recent entry we saved to the cache
        List<PetAdvertEntity> latestPets = petAdvertRepository.findLatestByOrderByCreatedDesc();

        ZonedDateTime lastUpdate = ZonedDateTime.now().minusHours(1); // Limit crawl to the last hour if we've never crawled before
        if (latestPets != null && !latestPets.isEmpty()) {
            lastUpdate = latestPets.get(0).getCreated();
        }

        log.info("Crawling pets4homes for entries newer than " + lastUpdate.format(DateTimeFormatter.RFC_1123_DATE_TIME));

        // Crawl pet4homes to find all newer dogs than the last saved ad
        List<PetAdvertEntity> dogs = petScraper.crawl(lastUpdate);

        if (dogs.isEmpty()) {
            log.info("No new dogs found since last update at " + lastUpdate);
        }
        // Send the new dog ad notifications
        dogs.forEach(dog -> {

            if (!petAdvertRepository.existsByUrl(dog.getUrl())) {
                Notification notification = new Notification(
                        dog.getTitle(),
                        dog.getDescription(),
                        dog.getUrl(),
                        dog.getThumbnail(),
                        dog.getCreated().toOffsetDateTime()
                );
                notificationService.sendNotification(notification);
                petAdvertRepository.save(dog);
                log.info("New doggo found: " + dog.getTitle() + " " + dog.getCreated().toString() + " " + dog.getUrl());
            }
        });
    }
}
