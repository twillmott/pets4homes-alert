package com.twillmott.pets4homesalert.service;

import com.twillmott.pets4homesalert.dto.Notification;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class DiscordNotificationService implements NotificationService {

    private final JDA discord;
    private final TextChannel channel;

    public DiscordNotificationService(
            @Value("${application.discord.token}") String discordToken,
            @Value("${application.discord.guild}") String discordGuild,
            @Value("${application.discord.channel}") String discordChannel)
            throws LoginException, InterruptedException {

        discord = JDABuilder.createDefault(discordToken).build();
        discord.awaitReady();
        channel = discord.getGuildById(Long.valueOf(discordGuild)).getTextChannelById(Long.valueOf(discordChannel));
    }

    @Override
    public void sendNotification(Notification notification) {

        MessageEmbed message = new MessageEmbed(
                notification.getUrl(),
                notification.getTitle(),
                notification.getDescription(),
                EmbedType.LINK,
                notification.getTimestamp(),
                55,
                new MessageEmbed.Thumbnail(notification.getThumbnail(), null, 200, 200),
                null,
                null,
                null,
                null,
                null,
                null
        );

        channel.sendMessage(message).complete();
    }
}
