package com.akvelon.cdp;

import com.akvelon.cdp.clients.HelloServiceClient;
import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import com.akvelon.cdp.entitieslibrary.Request;
import com.akvelon.cdp.entitieslibrary.RequestStatus;
import com.akvelon.cdp.executors.RequestActionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * Basic class that prepares environment for tests
 */
@Slf4j
public abstract class QaBase {

    protected RequestServiceClient requestServiceClient;
    protected StatusServiceClient statusServiceClient;
    protected HelloServiceClient helloServiceClient;
    protected RequestActionExecutor requestActionExecutor;
    protected SoftAssert softAssert;

    protected QaBase() {
        final ClientsRepository clientsRepository = ClientsRepository.getInstance();
        requestServiceClient = clientsRepository.getRequestServiceClient();
        statusServiceClient = clientsRepository.getStatusServiceClient();
        helloServiceClient = clientsRepository.getHelloServiceClient();
        requestActionExecutor = new RequestActionExecutor(requestServiceClient, statusServiceClient);
        softAssert = new SoftAssert();
    }

    @BeforeClass
    public void prepareTestEnvironment() {
        startClients();
        clearTestEnvironment();
    }

    @AfterClass
    public void clearTestEnvironmentAfterTests() {
        clearTestEnvironment();
        stopClients();
    }

    /**
     * Starts all necessary http clients
     */
    public void startClients() {
        requestServiceClient.startHttpClient();
        statusServiceClient.startHttpClient();
        helloServiceClient.startHttpClient();
    }

    /**
     * Stops all necessary http clients
     */
    public void stopClients() {
        requestServiceClient.stopHttpClient();
        statusServiceClient.stopHttpClient();
        helloServiceClient.stopHttpClient();
    }

    /**
     * Deletes all entities from the database
     */
    public void clearTestEnvironment() {
        log.debug("Cleaning test environment");
        clearAllRequests();
        clearStatus();
    }

    /**
     * Deletes all internal requests from the database
     */
    private void clearAllRequests() {
        log.debug("Deleting all requests");
        List<Request> allRequests = requestServiceClient.getAllRequests();
        if (!allRequests.isEmpty()) {
            allRequests.forEach(request -> requestActionExecutor.deleteRequestAndWaitItIsAbsentInDb(request.getId()));
        }
    }

    /**
     * Deletes {@link RequestStatus} entity from the database
     */
    private void clearStatus() {
        log.debug("Deleting status record");
        statusServiceClient.deleteStatus();
    }
}
