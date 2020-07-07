package com.dept.video.server.common;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateUtilityTest {

    private TemplateUtility templateUtility;
    private Configuration configuration;

    @Before
    public void init() {
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        templateUtility = new TemplateUtility(configuration);
    }

    @Test
    public void whenTemplateExistParseTemplateReturnsValue() throws IOException, TemplateException {
        Map<String, Object> parameters = new HashMap<>();

        String result = templateUtility.parseTemplateAsString("templates/base.email.ftl", parameters);

        Assert.assertTrue(!StringUtils.isEmpty(result));
    }

    @Test(expected = IOException.class)
    public void whenTemplateNotExistParseTemplateThrowIOException() throws IOException, TemplateException {
        Map<String, Object> parameters = new HashMap<>();

        templateUtility.parseTemplateAsString("templates/bad.name.ftl", parameters);
    }
}