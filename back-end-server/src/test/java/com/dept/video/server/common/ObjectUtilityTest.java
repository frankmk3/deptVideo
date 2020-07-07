package com.dept.video.server.common;

import com.dept.video.server.model.Contact;
import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilityTest {

    private static final String CUSTOM_NAME = "custom-name";
    private static final String CUSTOM_ID = "custom-id";

    @Test
    public void whenTheObjectHasValuesReturnAllValuesOnTheObject() {
        Contact sourceObject = new Contact();
        sourceObject.setName(CUSTOM_NAME);
        Contact updateObject = new Contact();
        updateObject.setId(CUSTOM_ID);

        Contact mergedContact = (Contact) ObjectUtility.updateNoNullParameter(sourceObject, updateObject);

        Assert.assertEquals(CUSTOM_ID, mergedContact.getId());
        Assert.assertEquals(CUSTOM_NAME, mergedContact.getName());
    }

    @Test
    public void whenTheSourceObjectIsNullReturnUpdateObject() {
        Contact updateObject = new Contact();
        updateObject.setId(CUSTOM_ID);

        Contact mergedContact = (Contact) ObjectUtility.updateNoNullParameter(null, updateObject);

        Assert.assertEquals(CUSTOM_ID, mergedContact.getId());
        Assert.assertNull(mergedContact.getName());
    }

    @Test
    public void whenTheUpdateObjectIsNullReturnSourceObject() {
        Contact sourceObject = new Contact();
        sourceObject.setName(CUSTOM_NAME);

        Contact mergedContact = (Contact) ObjectUtility.updateNoNullParameter(sourceObject, null);

        Assert.assertNull(mergedContact.getId());
        Assert.assertEquals(CUSTOM_NAME, mergedContact.getName());
    }

    @Test
    public void whenBothObjectAreNullReturnNull() {
        Object result = ObjectUtility.updateNoNullParameter(null, null);

        Assert.assertNull(result);
    }
}