package com.akvelon.cdp;

import com.akvelon.cdp.clients.HelloServiceClient;
import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import lombok.Getter;

/**
 * Allows to initiate all necessary application http clients
 */
@Getter
public class ClientsRepository {
    private static ClientsRepository clientsRepository;

    private HelloServiceClient helloServiceClient;
    private StatusServiceClient statusServiceClient;
    private RequestServiceClient requestServiceClient;

    private ClientsRepository() {
        helloServiceClient = new HelloServiceClient();
        statusServiceClient = new StatusServiceClient();
        requestServiceClient = new RequestServiceClient();
    }

    public static ClientsRepository getInstance() {
        if (clientsRepository == null) {
            clientsRepository = new ClientsRepository();
        }
        return clientsRepository;
    }
}
