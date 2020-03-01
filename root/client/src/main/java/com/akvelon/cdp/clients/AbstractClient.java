package com.akvelon.cdp.clients;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bndy.config.ConfigurationManager;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FutureResponseListener;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Abstract client which contains all basic methods for server host initialization
 * and mapping from json to application objects
 */
@Slf4j
@Getter
public abstract class AbstractClient {

    private static final String CONFIGURATION_FILE = "client.properties";
    protected static final String ERROR_MESSAGE = "Exception was thrown during request execution: {}";

    protected HttpClient httpClient;
    protected String serverAddress;
    protected ObjectMapper objectMapper;

    public AbstractClient() {
        final SslContextFactory sslContextFactory = new SslContextFactory.Client();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.setConnectTimeout(10000);

        final String hostName =
                ConfigurationManager.getConfiguration(CONFIGURATION_FILE, "default.server.host", String.class);
        final int port =
                ConfigurationManager.getConfiguration(CONFIGURATION_FILE, "default.server.port", Integer.class);
        serverAddress = format("http://%s:%s", hostName, port);

        objectMapper = getObjectMapper();
    }

    /**
     * Executes passed HTTP request
     *
     * @param request - HTTP request to execute
     * @return {@link ContentResponse}
     */
    protected ContentResponse executeHttpRequest(final Request request) {
        final FutureResponseListener responseListener = new FutureResponseListener(request);
        request.send(responseListener);
        try {
            return responseListener.get(20, SECONDS);
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Exception was thrown during execution of request {} {}:\n{}",
                      request.getMethod(), request.getURI(), e.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * Maps JSON to object of specified type
     *
     * @param json record to map
     * @param type - class of object that must be deserialized
     * @return Object of specified type
     */
    public <T> T mapJsonToObject(final String json, final Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (final JsonProcessingException e) {
            log.error("Exception was thrown while parsing JSON {} to {}:\n{}", json, type, e.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * Returns {@link ObjectMapper} that is capable to deserialize the application objects
     *
     * @return object mapper that ready to use
     */
    private ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        final LocalDateTimeDeserializer localDateTimeDeserializer =
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

    /**
     * Starts {@link HttpClient}
     */
    public void startHttpClient() {
        try {
            if (httpClient.isStopped()) {
                httpClient.start();
            }
        } catch (final Exception e) {
            log.error("Http client start failed");
        }
    }

    /**
     * Stops {@link HttpClient}
     */
    public void stopHttpClient() {
        try {
            if (httpClient.isRunning()) {
                httpClient.stop();
            }
        } catch (final Exception e) {
            log.error("Http client stop failed");
        }
    }
}
