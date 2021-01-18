package com.twillmott.pets4homesalert.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource("classpath:secrets.properties")
public class ApplicationConfig {
}

