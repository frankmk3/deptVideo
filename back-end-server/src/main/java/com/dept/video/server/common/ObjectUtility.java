package com.dept.video.server.common;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * General reusable feature.
 */
@Component
public final class ObjectUtility {

    private ObjectUtility() {
        //prevent initialization
    }


    /**
     * Pass the non null values from updateObject to sourceObject
     *
     * @param sourceObject
     * @param updateObject
     * @return
     */
    public static Object updateNoNullParameter(Object sourceObject, Object updateObject) {
        return updateNoNullParameter(sourceObject, updateObject, false);
    }

    /**
     * Pass the non null values from updateObject to sourceObject
     *
     * @param sourceObject
     * @param updateObject
     * @return
     */
    public static Object updateNoNullParameter(Object sourceObject, Object updateObject, boolean recursiveValidation) {
        Object result;
        if (sourceObject == null) {
            result = updateObject;
        } else if (updateObject == null) {
            result = sourceObject;
        } else {
            Method[] updateObjectMethods = updateObject.getClass().getMethods();
            for (Method currentMethod : updateObjectMethods) {
                if (currentMethod.getName().startsWith("get") || currentMethod.getName().startsWith("is")) {
                    String fullMethodName = currentMethod.getName();
                    String methodName = fullMethodName.substring(currentMethod.getName().startsWith("get") ? 3 : 2);
                    String setMethodName = "set" + methodName;
                    updateNoNullParameterOnChilds(sourceObject, updateObject, recursiveValidation, currentMethod, setMethodName);
                }
            }
            result = sourceObject;
        }
        return result;
    }

    private static void updateNoNullParameterOnChilds(
            Object sourceObject,
            Object updateObject,
            boolean recursiveValidation,
            Method currentMethod,
            String setMethodName
    ) {
        try {
            Method setMethod = sourceObject.getClass().getMethod(setMethodName, currentMethod.getReturnType());
            Object methodResult = currentMethod.invoke(updateObject);
            if (methodResult != null) {
                Object resultSubObject;
                if (recursiveValidation && !(methodResult instanceof Enum) && methodResult.getClass().getName().startsWith("com.zebra")) {
                    Object originalResult = currentMethod.invoke(sourceObject);
                    resultSubObject = updateNoNullParameter(originalResult, methodResult);
                } else {
                    resultSubObject = methodResult;
                }
                setMethod.invoke(sourceObject, resultSubObject);

            }
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException ignore) {
            //ignore errors
        }
    }

}
