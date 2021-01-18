package com.twillmott.pets4homesalert.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "pet_advert")
@Data
@NoArgsConstructor
@ToString
public class PetAdvertEntity {

    @Id
    @GeneratedValue
    @Column( columnDefinition = "uuid", updatable = false )
    public UUID id;
    String url;
    String title;
    BigDecimal price;
    @Lob
    String description;
    String location;
    ZonedDateTime created;
    String thumbnail;
    boolean boosted;

    public PetAdvertEntity(String url, String title, BigDecimal price, String description, String location, ZonedDateTime created, String thumbnail, boolean boosted) {
        this.url = url;
        this.title = title;
        this.price = price;
        this.description = description;
        this.location = location;
        this.created = created;
        this.thumbnail = thumbnail;
        this.boosted = boosted;
    }
}
