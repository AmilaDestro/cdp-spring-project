package com.akvelon.cdp.utils;

import com.akvelon.cdp.clients.AbstractClient;
import com.akvelon.cdp.exceptionslibrary.NotFoundException;
import com.akvelon.cdp.exceptionslibrary.RequestNotFoundException;
import com.akvelon.cdp.exceptionslibrary.WebPageNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class ExceptionsUtil {
    private AbstractClient abstractClient;

    public static final List<Class> APPLICATION_EXCEPTIONS = List.of(NotFoundException.class,
                                                                     RequestNotFoundException.class,
                                                                     WebPageNotFoundException.class);

    private static final String EXCEPTION_TRACE_KEY = "trace";
    private static final String CAUSED_BY = "Caused by:";
    private static final String MESSAGE = "message";

    public static List<String> getApplicationExceptionClassNames() {
        return APPLICATION_EXCEPTIONS.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
    }

    public static String getExceptionNameWithPackage(final String name) {
        return APPLICATION_EXCEPTIONS.stream()
                .filter(exceptionClass -> exceptionClass.getSimpleName().equals(name))
                .map(Class::getCanonicalName)
                .findFirst().orElseThrow(() -> new AssertionError(String.format("Canonical name for %s was not found", name)));
    }

    public Throwable getExceptionFromJson(final String json) {
        String stackTraceString = null;
        try {
            stackTraceString = (String) abstractClient.mapJsonToObject(json, HashMap.class).get(EXCEPTION_TRACE_KEY);
        } catch (JsonProcessingException e) {
            log.error("Cannot process JSON {}.\nReason: {}", json, e.getMessage());
        }

        String cause = stackTraceString.split(CAUSED_BY)[1];
        String rootCauseException = cause.split("at")[0].trim();

        var exceptionAndParams = rootCauseException.split("\\(");
        var exceptionName = exceptionAndParams[0].trim();
        var paramKeysAndValues = exceptionAndParams[1].split(",");
        Map<String, String> paramsMap = new HashMap<>();
        Arrays.asList(paramKeysAndValues).forEach(
                param -> {
                    var keyAndValue = param.split("=");
                    paramsMap.put(keyAndValue[0].trim(), keyAndValue[1].trim());
                }
        );

        if (getApplicationExceptionClassNames().contains(exceptionName)) {
            return getException(exceptionName, paramsMap.get(MESSAGE));
        }
        throw new RuntimeException();
    }

    private <T extends Throwable>T getException(final String name, final String params) {
        try {
            Class<?> exceptionClass = Class.forName(getExceptionNameWithPackage(name));
            return (T) exceptionClass.getConstructor(String.class).newInstance(params);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error occurred when trying to instantiate class {}: {}", name, e.getMessage());
        }
        throw new AssertionError(String.format("Exception with name %s was not found in HttpResponse", name));
    }
}
