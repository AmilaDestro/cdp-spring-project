package com.akvelon.cdp;

import com.akvelon.cdp.clients.HelloServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class ClientApp {
    public static void main(String[] args) throws IOException, URISyntaxException {
//        HelloServiceClient helloServiceClient = new HelloServiceClient();
//        String greeting = helloServiceClient.greeting("Liuda");

        StatusServiceClient statusServiceClient = new StatusServiceClient();
        val status = statusServiceClient.getStatus();

        log.debug("Returned entity:\n{}", status);
    }
}
