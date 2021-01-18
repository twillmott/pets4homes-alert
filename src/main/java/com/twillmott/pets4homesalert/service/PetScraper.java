package com.twillmott.pets4homesalert.service;

import com.twillmott.pets4homesalert.entity.PetAdvertEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PetScraper {

    private final static String PET_URL = "https://www.pets4homes.co.uk/search/?page=1&type_id=3&breed_id=116&results=10&sort=datenew";
    private final static String DOG_ID = "3";
    private final static String COCKER_SPANIEL_ID = "116";
    private final static String SORT = "datenew";

    public List<PetAdvertEntity> crawl(final ZonedDateTime cutoffTime) throws IOException, ParseException {

        int page = 1;
        boolean complete = false;
        List<PetAdvertEntity> pets = new ArrayList();

        while (!complete) {
            String uri = getUriForPage(page);
            log.info("Crawling for page {}: {}", page, uri);
            Document doc = Jsoup.connect(uri).get();
            for(Element element : doc.getElementsByClass("profilelisting")) {
                try {
                    String url = element.getElementsByClass("headline").get(0).getElementsByTag("a").get(0).attr("href");
                    String headline = element.getElementsByClass("headline").get(0).text();
                    String location = element.getElementsByClass("location").get(0).text();
                    String description = element.getElementsByClass("description").get(0).text();
                    String advertAge = element.getElementsByClass("profile-listing-updated").get(0).text();
                    String priceString = element.getElementsByClass("listingprice").get(0).text();
                    String thumbnail = element.getElementsByClass("img-responsive").get(0).attr("src");

                    boolean boosted = element.toString().toLowerCase().contains("increased exposure");
                    BigDecimal price = BigDecimal.valueOf(NumberFormat.getCurrencyInstance(Locale.UK).parse(priceString).doubleValue());
                    ZonedDateTime created = getTimeFromAgeString(advertAge);

                    if (!boosted && !created.isAfter(cutoffTime)) {
                        complete = true;
                        break;
                    }

                    pets.add(new PetAdvertEntity(url, headline, price, description, location, created, thumbnail, boosted));
                } catch (IndexOutOfBoundsException e) {}
            }
            page++;
        }

        // As the focused items are at the top of the page, older ones than the most recent non focused item
        // might be at the top of the list. Sort and filter to get rid of the older focused items.
        Collections.sort(pets, Comparator.comparing(PetAdvertEntity::getCreated).reversed());
        pets = pets.stream()
                .filter(pet -> pet.getCreated().isAfter(cutoffTime))
                .collect(Collectors.toList());

        return pets;
    }

    private final String getUriForPage(int page) {
        return new URIBuilder()
                .setScheme("https")
                .setHost("www.pets4homes.co.uk")
                .setPath("/search/")
                .addParameter("type_id", DOG_ID)
                .addParameter("breed_id", COCKER_SPANIEL_ID)
                .addParameter("sort", SORT)
                .addParameter("page", String.valueOf(page))
                .toString();
    }

    private ZonedDateTime getTimeFromAgeString(String age) {
        if (age.toLowerCase().contains("minute")) {
            return ZonedDateTime.now().minusMinutes(Long.valueOf(age.replaceAll("\\D+","")));
        }
        if (age.toLowerCase().contains("hour")) {
            return ZonedDateTime.now().minusHours(Long.valueOf(age.replaceAll("\\D+","")));
        }
        if (age.toLowerCase().contains("day")) {
            return ZonedDateTime.now().minusDays(Long.valueOf(age.replaceAll("\\D+","")));
        }
        if (age.toLowerCase().contains("month")) {
            return ZonedDateTime.now().minusMonths(Long.valueOf(age.replaceAll("\\D+","")));
        }
        return ZonedDateTime.now();
    }
}
