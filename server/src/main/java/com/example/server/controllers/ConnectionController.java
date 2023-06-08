package com.example.server.controllers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8081") // Allow requests from the client running on localhost:8081
public class ConnectionController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ConnectionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        String query = "SELECT password FROM users WHERE username = ?";
        try {
            String dbPassword = jdbcTemplate.queryForObject(query, String.class, username);

            if (dbPassword != null && dbPassword.equals(password)) {
                return ResponseEntity.status(HttpStatus.OK).body("Login successful!");
            }
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or password are incorrect");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username and password.");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, Integer.class, username);

        if (count > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists. Please choose a different username");
        }

        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
        jdbcTemplate.update(insertQuery, username, password);


        return ResponseEntity.status(HttpStatus.OK).body("Registered successful!");
    }

    @PostConstruct
    public void initializeDatabase() {
        try {
            Path path = Paths.get("src/main/java/database/create-table.sql");
            String createTableSql = Files.readString(path);

            jdbcTemplate.execute(createTableSql);
        } catch (IOException e) {
            System.err.println("Exception thrown at creating the table: " + e);
        }
    }
}
