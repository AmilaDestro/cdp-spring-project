package com.akvelon.cdp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bndy.config.ConfigurationManager;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@Getter
public abstract class AbstractClient {
    private static final String CONFIGURATION_FILE = "client.properties";

    protected CloseableHttpClient httpClient;
    protected String serverAddress;

    public AbstractClient() {
        httpClient = HttpClients.createDefault();

        final String hostName = ConfigurationManager.getConfiguration(CONFIGURATION_FILE, "default.server.host", String.class);
        final int port = ConfigurationManager.getConfiguration(CONFIGURATION_FILE, "default.server.port", Integer.class);

        serverAddress = format("http://%s:%s", hostName, port);
    }

    protected String mapResponseToJson(final HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }

    protected <T>T mapJsonToObject(final String json, final Class<T> type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
}
