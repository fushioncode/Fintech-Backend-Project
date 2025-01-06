package com.fintech.bepc.services;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        int errorCode = checkDatabaseHealth();
        if (errorCode != 0) {
            return Health.down().withDetail("Database", "Unable to connect").build();
        }
        boolean externalServiceHealthy = checkExternalServices();

        if (!externalServiceHealthy) {
            return Health.down().withDetail("External Services", "Unhealthy").build();
        }
        return Health.up().withDetail("Service", "All components healthy").build();
    }

    private int checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                return 0;
            }
        } catch (SQLException e) {
            return 1;
        }
        return 1;
    }

    private boolean checkExternalServices() {
        try {
            boolean isApiReachable = checkApiHealth();

            return isApiReachable;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkApiHealth() {
        try {
            String url = "https://your-api-url/health";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
}

