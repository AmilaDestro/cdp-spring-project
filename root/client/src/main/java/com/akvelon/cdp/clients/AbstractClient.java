package com.akvelon.cdp.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bndy.config.ConfigurationManager;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

@Slf4j
@Getter
public abstract class AbstractClient {
    private static final String CONFIGURATION_FILE = "client.properties";

    protected CloseableHttpClient httpClient;
    protected String serverAddress;
    protected ObjectMapper objectMapper;

    public AbstractClient() {
        httpClient = HttpClients.createDefault();

        final String hostName = ConfigurationManager.getConfiguration(CONFIGURATION_FILE, "default.server.host", String.class);
        final int port = ConfigurationManager.getConfiguration(CONFIGURATION_FILE, "default.server.port", Integer.class);
        serverAddress = format("http://%s:%s", hostName, port);

        objectMapper = getObjectMapper();
    }

    protected String mapResponseToJson(final HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }

    protected <T>T mapJsonToObject(final String json, final Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }
}
