package org.example.pixel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;
@Component
public class ImageInitializer implements CommandLineRunner {

    private final ImageController imageController;

    public ImageInitializer(ImageController imageController) {
        this.imageController = imageController;
    }

    @Override
    public void run(String... args) throws Exception {
        imageController.generateImage();
    }
}
