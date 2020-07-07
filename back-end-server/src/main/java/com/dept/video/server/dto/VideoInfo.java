package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Specific Video information
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfo implements Serializable {

    private static final long serialVersionUID = 1;
    private String id;
    private String key;
    private String site;
    private String size;
    private String type;
    private String title;
    private String description;
    private String channelTitle;
    private String duration;
    private String definition;
    private List<String> tags;
    private Map thumbnails;

}