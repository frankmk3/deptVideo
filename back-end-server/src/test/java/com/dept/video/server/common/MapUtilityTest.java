package com.dept.video.server.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtilityTest {

    @Test
    public void WhenFirstLevelKeyExistInMapReturnTheValue() {
        String key = "keyValue";
        String value = "value";
        Map object = new HashMap();
        object.put(key, value);

        Object result = MapUtility.extractValueFromPath(object, key);

        Assert.assertEquals(value, result);
    }

    @Test
    public void WhenNestedLevelKeyExistInMapReturnTheValue() {
        List<String> keys = Arrays.asList("key1", "key2", "key3", "key4");
        String value = "nested-value";
        Map previousObject = generateMultipleLevelMap(keys, value);

        Object result = MapUtility.extractValueFromPath(previousObject, String.join(".", keys));

        Assert.assertEquals(value, result);
    }

    @Test
    public void WhenNestedLevelKeyNotExistInMapReturnNull() {
        List<String> keys = Arrays.asList("key1", "key2", "key3", "key4");
        String value = "nested-value";
        Map previousObject = generateMultipleLevelMap(keys, value);

        Object result = MapUtility.extractValueFromPath(previousObject, String.join(".", keys)+".other.path");

        Assert.assertNull(result);
    }


    @Test
    public void WhenPathNotExistInMapReturnNull() {
        Map object = new HashMap();
        String path = "invalidPath";

        Object result = MapUtility.extractValueFromPath(object, path);

        Assert.assertNull(result);
    }

    @Test
    public void WhenPathIsNullReturnNull() {
        Map object = new HashMap();

        Object result = MapUtility.extractValueFromPath(object, null);

        Assert.assertNull(result);
    }

    @Test
    public void WhenPathIsEmptyReturnNull() {
        Map object = new HashMap();

        Object result = MapUtility.extractValueFromPath(object, "");

        Assert.assertNull(result);
    }

    @Test
    public void WhenSourceObjectIsNullReturnNull() {
        String path = "invalidPath";

        Object result = MapUtility.extractValueFromPath(null, path);

        Assert.assertNull(result);
    }


    private Map generateMultipleLevelMap(List<String> keys, String value) {
        Map previousObject = new HashMap();
        previousObject.put(keys.get(keys.size() - 1), value);
        for (int i = keys.size() - 2; i >= 0; i--) {
            HashMap<Object, Object> currentObject = new HashMap<>();
            currentObject.put(keys.get(i), previousObject);
            previousObject = currentObject;
        }
        return previousObject;
    }
}