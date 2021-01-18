package com.twillmott.pets4homesalert.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Notification {
    public final String title;
    public final String description;
    public final String url;
    public final String thumbnail;
    public final OffsetDateTime timestamp;
}
