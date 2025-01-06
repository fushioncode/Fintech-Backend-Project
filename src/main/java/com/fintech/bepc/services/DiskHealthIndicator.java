package com.fintech.bepc.services;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DiskHealthIndicator implements HealthIndicator {

    private static final long MIN_REQUIRED_SPACE_MB = 500L; // Minimum required space in MB

    @Override
    public Health health() {
        File disk = new File("/");
        long freeSpace = disk.getFreeSpace() / (1024 * 1024); // Free space in MB

        if (freeSpace < MIN_REQUIRED_SPACE_MB) {
            return Health.down().withDetail("Disk Space", "Insufficient space: " + freeSpace + "MB available").build();
        }

        return Health.up().withDetail("Disk Space", "Sufficient space: " + freeSpace + "MB available").build();
    }
}

