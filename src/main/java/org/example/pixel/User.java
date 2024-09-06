package org.example.pixel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID uuid;
    private LocalDateTime tokenCreationTime;

    public User() {
        uuid = UUID.randomUUID();
        tokenCreationTime = LocalDateTime.now();
        System.out.println("User created with uuid: " + uuid + "and time of creation: " + tokenCreationTime);

    }

    public UUID getUuid() {
        return uuid;
    }

    public void refreshToken() {
        tokenCreationTime = LocalDateTime.now();
    }

    public LocalDateTime getTokenCreationTime() {
        return tokenCreationTime;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(tokenCreationTime, now);
        return duration.toMinutes() < 5;
    }

}
