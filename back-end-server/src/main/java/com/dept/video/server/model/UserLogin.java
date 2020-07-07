package com.dept.video.server.model;

import com.dept.video.server.dto.FingerPrint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "userlogin")
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin extends BaseObject {

    private String userId;
    private FingerPrint fingerPrint;

    private Boolean notified;
}