package com.scalable.seatingservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:constants.properties")
@PropertySource(value = "file:/.config/constants.properties", ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConstants {

    private ErrorCodes errorCodes;

    @Getter
    @Setter
    public static class ErrorCodes {
        private String notFoundCode;
        private String notFoundMessage;
        private String okCode;
        private String okMessage;
        private String seatUnavailableCode;
        private String seatUnavailableMessage;
        private String reservedStatus;
        private String allocatedStatus;
        private String availableStatus;

    }
}
