package org.example.pixel;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

//Oznaczenie klasy jako @Component sprawia, że Spring
// automatycznie utworzy jej instancję podczas uruchamiania aplikacji.
@Component
public class TcpServer {
    private static final int PORT = 2136; // Port serwera TCP
    private static final String ADMIN_PASSWORD = "admin"; // hasło administratora
    private static final String DB_NAME = "jdbc:sqlite:database.db";
    private Connection database; // Dodanie połączenia do bazy danych
    private final UserController userController;

    //Podłączenie bazy danych
    @Autowired
    public TcpServer(UserController userController) {
        this.userController = userController;
    }

    //adnotacja @PostConstruct powoduje, że jest wykonywana natychmiast po utworzeniu instancji klasy.
    @PostConstruct
    public void startServer() {
        try {
            //Inicjalizacja połączenia z bazą danych
            database = DriverManager.getConnection(DB_NAME);
        // Serwer TCP uruchamia się w osobnym wątku, aby nie blokować głównego wątku aplikacji
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started on port " + PORT);

                // Serwer akceptuje tylko jedno połączenie
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected" + clientSocket.getInetAddress());

                // Utworzenie strumieni wejścia/wyjścia
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Wysłanie zapytania do klienta o podanie hasła
                out.println("Input password: ");

                //Odczytanie hasła od klienta
                String receivedPassword = in.readLine();

                if (receivedPassword.equals(ADMIN_PASSWORD)) {
                    out.println("Password correct. Logged in as Admin");
                    System.out.println("Admin logged in");
                    //Tutaj znajduje się logika co może Administrator

                    // Pętla do obsługi poleceń
                    String command;
                    while ((command = in.readLine()) != null) {
                        if (command.startsWith("ban ")) {
                            String token = command.substring(4).trim();// Pobranie tokena z komendy
                            UUID uuid = UUID.fromString(token); // przekonwertowanie stringa na UUID
                            User user = findUser(uuid);
                            if (user != null) {
                                userController.users.remove(user);
                            }
                            int removedRecords = banUser(token);
                            out.println("Banned token " + token + "Deleted: " + removedRecords + " records");
                        } else {
                            out.println("Unknown command " + command);
                        }
                    }
                } else {
                    out.println("Password incorrect");
                    System.out.println("Incorrect password");
                    clientSocket.close(); //Zamykanie połączenia w przypadku błędnego hasła
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start(); // Uruchominie serwera w osobnym wątku.
    } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metoda do usuwania użytkownika

    private int banUser(String token) {
        int removedRecord = 0;
        try {
            // Usuwanie rekordów z bazy danych
            String deleteSQL = "DELETE FROM entry WHERE token = ?";
            PreparedStatement preparedStatement = database.prepareStatement(deleteSQL);
            preparedStatement.setString(1, token);
            removedRecord = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return removedRecord;
    }

    //Znalezienie użytkownika na podstawie UUID

    private User findUser(UUID uuid) {
        return userController.users.stream()
                .filter(user -> user.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}
