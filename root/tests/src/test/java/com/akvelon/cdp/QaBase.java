package com.akvelon.cdp;

import com.akvelon.cdp.clients.HelloServiceClient;
import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import com.akvelon.cdp.entitieslibrary.Request;
import com.akvelon.cdp.executors.RequestActionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.asserts.SoftAssert;

import java.util.List;

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
    public void clearTestEnvironment() {
        log.debug("Cleaning test environment");
        clearAllRequests();
        clearStatus();
    }

    private void clearAllRequests() {
        log.debug("Deleting all requests");
        List<Request> allRequests = requestServiceClient.getAllRequests();
        if (!allRequests.isEmpty()) {
            allRequests.forEach(request -> requestActionExecutor.deleteRequestAndWaitItIsAbsentInDb(request.getId()));
        }
    }

    private void clearStatus() {
        log.debug("Deleting status record");
        statusServiceClient.deleteStatus();
    }
}
