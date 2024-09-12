package org.example.pixel;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class ImageController {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;
    private final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    //Inicjalizacja bazy danych
    private final DatabaseManager databaseManager;
    private final UserController userController;
    public ImageController(DatabaseManager databaseManager, UserController userController) {
        this.databaseManager = databaseManager;
        this.userController = userController;
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        graphics.dispose();
    }

    @GetMapping("/image")
    public void getImage(HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        ImageIO.write(image,"jpeg",response.getOutputStream());
    }

    public void generateImage() {
        List<DatabaseManager.Entry> pixelRequests = databaseManager.getAllEntries();

        for (DatabaseManager.Entry pixelRequest : pixelRequests) {
            int x = pixelRequest.getX();
            int y = pixelRequest.getY();
            int rgb = Integer.parseInt(pixelRequest.getColor().trim(), 16);
            image.setRGB(x, y, rgb);
        }
    }


    @PostMapping("/pixel")
    public ResponseEntity<Void> putPixel(@RequestBody PixelRequest pixelRequest){
        String colorHex = pixelRequest.getColor().trim();
        int rgb = Integer.parseInt(colorHex, 16);
        image.setRGB(pixelRequest.getX(), pixelRequest.getY(),rgb);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        //Wywolanie zapytania do bazy danych za pomocą wcześniej utworzonej metody insertEntry
        databaseManager.insertEntry(pixelRequest.getUuid().toString(), pixelRequest.getX(), pixelRequest.getY(), pixelRequest.getColor(), timestamp);
        return ResponseEntity.ok().build();
    }

}
