package com.dept.video.server.common;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * Maps reusable feature.
 */
@Component
public final class MapUtility {

    private MapUtility() {
        //prevent initialization
    }

    public static Object extractValueFromPath(Map<String, Object> source, String path) {
        if (!StringUtils.isEmpty(path) && source != null) {
            return extractValueFromPath(source, path.split("\\."));
        }
        return null;
    }

    /**
     * Get the value from the source map according to the pathSegment provided.
     * Each element on the path represent a level on the map structure.
     */
    private static Object extractValueFromPath(Map<String, Object> source, String... pathSegment) {
        if (pathSegment.length == 1) {
            return source.get(pathSegment[0]);
        }
        if (pathSegment.length > 1 && source.containsKey(pathSegment[0]) && source.get(pathSegment[0]) instanceof Map) {
            return extractValueFromPath((Map<String, Object>) source.get(pathSegment[0]),
                    Arrays.copyOfRange(pathSegment, 1,
                            pathSegment.length));
        }
        return null;
    }

}
