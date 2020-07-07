package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdentityAvailability {

    private Boolean available;

    public UserIdentityAvailability() {
        available = false;
    }

    public UserIdentityAvailability(Boolean available) {
        this.available = available;
    }
}
