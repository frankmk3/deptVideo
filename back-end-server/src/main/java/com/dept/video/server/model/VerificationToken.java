package com.dept.video.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Document(collection = "verificationToken")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationToken extends BaseObject {

    private static final int EXPIRATION = 60 * 24;

    private String token;
    private String userId;
    private String type;
    private Date expiryDate;

    public VerificationToken() {
        super();
        //default constructor
    }

    public VerificationToken(String token, String userId, String type) {
        super();
        this.token = token;
        this.userId = userId;
        this.type = type;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
