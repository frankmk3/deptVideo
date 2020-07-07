package com.dept.video.server.common;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Utility to manage freemarker template.
 */
@Component
@Slf4j
public class TemplateUtility {

    private final Configuration configuration;

    @Autowired
    public TemplateUtility(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Process the request template file using freemarker.
     *
     * @param templateId
     * @param parameters
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String parseTemplateAsString(String templateId, Map<String, Object> parameters) throws IOException, TemplateException {
        configuration.setClassForTemplateLoading(this.getClass(), "/");
        configuration.setLocalizedLookup(false);
        final Template template = configuration.getTemplate(templateId);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, parameters);
    }
}