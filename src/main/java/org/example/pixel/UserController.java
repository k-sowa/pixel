package org.example.pixel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    List<User> users = new ArrayList<>();


    @PostMapping("/register")
    public User register() {
        User user = new User();
        users.add(user);
        return user;
    }

    @GetMapping("/tokens")
    public List<User> getTokens() {
        return users;
    }

}
