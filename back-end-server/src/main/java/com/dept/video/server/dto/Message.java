package com.dept.video.server.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Message {

    private String templateId;
    private String baseTemplate;
    private String subject;

    private List<String> to;

    private Map<String, Object> content;
    private Map<String, Object> baseContent;

    public Message(String templateId, String subject, List<String> to, Map<String, Object> content, Map<String, Object> baseContent) {
        this.templateId = templateId;
        this.subject = subject;
        this.to = to;
        this.content = content;
        this.baseContent = baseContent;
    }
}
