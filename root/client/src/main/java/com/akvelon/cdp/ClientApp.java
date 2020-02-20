package com.akvelon.cdp;

import com.akvelon.cdp.clients.HelloServiceClient;
import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import com.akvelon.cdp.entitieslib.Request;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class ClientApp {
    public static void main(String[] args) throws IOException, URISyntaxException {
//        HelloServiceClient helloServiceClient = new HelloServiceClient();
//        String greeting = helloServiceClient.greeting("Liuda");

//        StatusServiceClient statusServiceClient = new StatusServiceClient();
//        val status = statusServiceClient.getStatus();
//
//        log.debug("Returned entity:\n{}", status);

        RequestServiceClient requestServiceClient = new RequestServiceClient();
//        final String urlToRedirectTo = "adme.ru";
//        boolean redirectSuccessful = requestServiceClient.redirectToSpecifiedUrlAndUpdateStatistic(urlToRedirectTo);
//        log.debug("Redirect to {} was successful: {}", urlToRedirectTo, redirectSuccessful);

        List<Request> allRequests = requestServiceClient.getAllRequests();

    }
}
