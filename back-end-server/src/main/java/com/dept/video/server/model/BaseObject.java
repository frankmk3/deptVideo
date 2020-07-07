package com.dept.video.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseObject {

    @JsonIgnore
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";

    @JsonIgnore
    public static final String TIME_FORMAT = "HH:mm";

    @Id
    protected String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    protected Date date;

    public BaseObject() {
        date = new Date();
    }
}