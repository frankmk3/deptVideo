package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rating implements Serializable {

    private static final long serialVersionUID = 1;
    private String source;
    private Float value;
}
