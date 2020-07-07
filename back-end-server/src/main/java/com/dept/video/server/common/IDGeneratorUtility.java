package com.dept.video.server.common;

import com.dept.video.server.model.BaseObject;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Utility to generate ids
 */
public final class IDGeneratorUtility {

    private IDGeneratorUtility() {
        //prevent initialization
    }

    public static String generateId(Class clazz) {
        return String.format("%s_%s", clazz.getSimpleName(), UUID.randomUUID().toString());
    }

    public static void generateIdIfMissing(BaseObject baseObject) {
        if (StringUtils.isEmpty(baseObject.getId())) {
            baseObject.setId(generateId(baseObject.getClass()));
        }
    }
}