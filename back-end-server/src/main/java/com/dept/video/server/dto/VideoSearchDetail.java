package com.dept.video.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Video details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoSearchDetail implements Serializable {

    private static final long serialVersionUID = 1;

    private String title;
    private String originalTitle;
    private String overview;
    private Integer year;
    private String type;
    private String originalLanguage;
    private String imdbId;
    private String releaseDate;
    private String tagline;

    private List<String> posters;
    private List<String> countries;
    private List<String> genres;
    private List<String> directors;
    private List<String> actors;
    private List<String> companies;
    private List<String> spokenLanguages;
    private List<Rating> ratings;
    private List<VideoInfo> videos;
    private Map<String, Object> extraInformation;

}