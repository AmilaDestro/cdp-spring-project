package com.akvelon.cdp.utils;

import static java.lang.String.format;

import com.akvelon.cdp.clients.AbstractClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class ServerExceptionsUtil {

//    private AbstractClient abstractClient;
//
//    private static final List<Class> APPLICATION_EXCEPTIONS = List.of(NotFoundException.class,
//                                                                      RequestNotFoundException.class,
//                                                                      WebPageNotFoundException.class);
//    private static final List<String> APPLICATION_EXCEPTIONS_NAMES =
//            List.of(NotFoundException.class.getSimpleName(),
//                    RequestNotFoundException.class.getSimpleName(),
//                    WebPageNotFoundException.class.getSimpleName());
//
//
//    private static final String EXCEPTION_TRACE_KEY = "trace";
//    private static final String CAUSED_BY = "Caused by:";
//    private static final String MESSAGE = "message";
//
//    public static List<String> getApplicationExceptionClassNames() {
//        return APPLICATION_EXCEPTIONS.stream()
//                                     .map(Class::getSimpleName)
//                                     .collect(Collectors.toList());
//    }
//
//    public static String getExceptionNameWithPackage(final String name) {
//        return APPLICATION_EXCEPTIONS.stream()
//                                     .filter(exceptionClass -> exceptionClass.getSimpleName().equals(name))
//                                     .map(Class::getCanonicalName)
//                                     .findFirst().orElseThrow(
//                        () -> new AssertionError(format("Canonical name for %s was not found", name)));
//    }
//
//    public Throwable getThrownExceptionFromJsonResponse(final String json) {
//        final String stackTraceString = (String) abstractClient.mapJsonToObject(json, HashMap.class)
//                                                               .get(EXCEPTION_TRACE_KEY);
//
//        final String cause = stackTraceString.split(CAUSED_BY)[1];
//        final String rootCauseException = cause.split("at")[0].trim();
//
//        final String[] exceptionAndParams = rootCauseException.split("\\(");
//        final String exceptionName = exceptionAndParams[0].trim();
//        final String[] paramKeysAndValues = exceptionAndParams[1].split(",");
//        final Map<String, String> exceptionParamsMap = new HashMap<>();
//        Arrays.asList(paramKeysAndValues).forEach(
//                param -> {
//                    final String[] keyAndValue = param.split("=");
//                    exceptionParamsMap.put(keyAndValue[0].trim(), keyAndValue[1].trim());
//                });
//
//        if (getApplicationExceptionClassNames().contains(exceptionName)) {
////            return getException(exceptionName, exceptionParamsMap.get(MESSAGE));
//            return new NotFoundException(MESSAGE);
//        }
//        throw new RuntimeException();
//    }

//    private Throwable getException(final String exceptionName, final String constructorParam) {
//        switch (exceptionName) {
//
//        }
//    }

//    private <T extends Throwable> T getException(final String exceptionName, final String constructorParam) {
//        try {
//            val exceptionClass = EXCEPTIONS_CLASSES_AND_NAMES.get(exceptionName);
//            return (T) exceptionClass.getConstructor(String.class).newInstance(constructorParam);
//        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            log.error("Error occurred when trying to instantiate class {}: {}", exceptionName, e.getMessage());
//        }
//        throw new AssertionError(
//                format("Exception with exceptionName %s was not found in HttpResponse", exceptionName));
//    }
}
