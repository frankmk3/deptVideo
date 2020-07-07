package com.dept.video.server.common;

import com.dept.video.server.model.Contact;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IDGeneratorUtilityTest {

    @Test
    public void whenGenerateIdOfAClassTheIdStartWithClassName() {
        String result = IDGeneratorUtility.generateId(Contact.class);

        Assert.assertTrue(result.startsWith(Contact.class.getSimpleName() + "_"));
    }

    @Test
    public void whenIdAttributeIsMissingGenerateIdOfAClass() {
        Contact contact = new Contact();

        IDGeneratorUtility.generateIdIfMissing(contact);

        Assert.assertTrue(contact.getId().startsWith(Contact.class.getSimpleName() + "_"));
    }

    @Test
    public void whenIdAttributeIsPresentNotGenerateId() {
        Contact contact = new Contact();
        String id = "custom-id";
        contact.setId(id);

        IDGeneratorUtility.generateIdIfMissing(contact);

        Assert.assertEquals(id, contact.getId());
    }
}