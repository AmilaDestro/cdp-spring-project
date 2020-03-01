package com.akvelon.cdp;

import com.akvelon.cdp.data.TestDataProvider;
import com.akvelon.cdp.executors.RedirectAndStatusUpdateExecutor;
import com.akvelon.cdp.utils.ServerExceptionsUtil;
import com.akvelon.cdp.utils.HttpResponseUtil;
import com.akvelon.cdp.utils.RequestUtil;
import com.google.common.collect.Iterables;
import lombok.val;
import org.eclipse.jetty.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import java.util.List;

import static com.akvelon.cdp.utils.RequestUtil.getLastRequestUrlSkipProtocol;
import static java.lang.String.format;

public class RequestsAndStatusQaIT extends QaBase {

    private final static long NON_EXISTING_REQUEST_ID = 0;

    private final RedirectAndStatusUpdateExecutor redirectAndStatusUpdateExecutor;
    private final RequestUtil requestUtil;
    private final HttpResponseUtil httpResponseUtil;
    private final ServerExceptionsUtil exceptionUtils;

    public RequestsAndStatusQaIT() {
        redirectAndStatusUpdateExecutor =
                new RedirectAndStatusUpdateExecutor(this.requestServiceClient, this.statusServiceClient);
        requestUtil = new RequestUtil(this.requestServiceClient);
        httpResponseUtil = new HttpResponseUtil(this.requestServiceClient);
        exceptionUtils = new ServerExceptionsUtil();
    }

    @AfterMethod(alwaysRun = true)
    public void clearAfterMethod() {
        clearTestEnvironment();
    }

    @Test
    public void getEmptyStatusRecordWhenNoRequestsPerformed() {
        val status = statusServiceClient.getStatus();
        softAssert.assertNotNull(status, "Returned Status entity is null");
        softAssert.assertEquals(status.getNumberOfRequest(), 0,
                                "Number of request for empty status should be 0");
        softAssert.assertTrue(status.getFullStatistic().isEmpty(),
                              "Empty status should not contain information about requests");
        softAssert.assertAll();
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "urlsToRedirectAndNumbersOfRequest")
    public void redirectToSpecifiedUrlAndCheckStatusIsUpdated(final String url,
                                                              final int expectedRequestNumber) {
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(url);

        val currentStatus = statusServiceClient.getStatus();
        val expectedUrl = getLastRequestUrlSkipProtocol(url);
        val actualUrl = getLastRequestUrlSkipProtocol(currentStatus.getLastRequestUrl());
        softAssert.assertEquals(actualUrl, expectedUrl,
                                format("Last visited website was not %s", url));

        val actualRequestNumber = currentStatus.getNumberOfRequest();
        softAssert.assertEquals(actualRequestNumber, expectedRequestNumber,
                                format("Expected number of request for %s - %s, actual - %s", url,
                                       expectedRequestNumber,
                                       actualRequestNumber));

        val statistic = currentStatus.getFullStatistic();
        val expectedRequest = requestUtil.getRequestByUrl(url);
        softAssert.assertTrue(statistic.contains(expectedRequest),
                              format("Request %s is not included to full statistic.\nActual statistic:\n%s",
                                     expectedRequest, statistic));

        softAssert.assertAll();
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "twoDifferentUrls")
    public void checkStatusWithFullStatisticsIsUpdatedAfterEachRedirect(final String firstUrl,
                                                                        final String secondUrl) {
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(firstUrl);
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(secondUrl);

        val status = statusServiceClient.getStatus();
        val actualLastUrl = getLastRequestUrlSkipProtocol(status.getLastRequestUrl());
        val expectedLastUrl = getLastRequestUrlSkipProtocol(secondUrl);
        softAssert.assertEquals(actualLastUrl, expectedLastUrl,
                                format("Expected last URL - %s, actual - %s", expectedLastUrl, actualLastUrl));

        val fullStatistic = status.getFullStatistic();
        softAssert.assertEquals(fullStatistic.size(), 2, "Full statistic is expected to have 2 records");

        val allActualRequests = requestServiceClient.getAllRequests();
        val allExpectedRequests =
                List.of(requestUtil.getRequestByUrl(firstUrl), requestUtil.getRequestByUrl(secondUrl));
        softAssert.assertTrue(allActualRequests.containsAll(allExpectedRequests),
                              format("Expected requests - %s\nActual requests - %s", allExpectedRequests,
                                     allActualRequests));
        softAssert.assertTrue(fullStatistic.containsAll(allActualRequests),
                              format("Expected full statistic - %s\nActual full statistic - %s", allActualRequests,
                                     fullStatistic));
        softAssert.assertAll();
    }

    @Test
    public void redirectToWrongUrlAndExpectNotFoundError() {
        val lastStatus = statusServiceClient.getStatus();
        val lastRequestNumber = lastStatus.getNumberOfRequest();
        val lastUrl = lastStatus.getLastRequestUrl();

        val wrongUrl = "nonExistingUrl";
        val response = requestServiceClient.redirectToSpecifiedUrlUpdateStatistic(wrongUrl);
        val actualResponseStatus = response.getStatus();
        softAssert.assertEquals(actualResponseStatus, HttpStatus.NOT_FOUND_404,
                                format("Expected response status - %s but was - %s",
                                       HttpStatus.NOT_FOUND_404,
                                       actualResponseStatus));

        val statusAfterRedirectToWrongUrl = statusServiceClient.getStatus();
        softAssert.assertEquals(statusAfterRedirectToWrongUrl.getLastRequestUrl(),
                                lastUrl,
                                "Status last URL was updated but shouldn't");
        softAssert.assertEquals(statusAfterRedirectToWrongUrl.getNumberOfRequest(),
                                lastRequestNumber,
                                "Status number of request was updated but shouldn't");
        softAssert.assertAll();
    }

    @Test(dataProvider = "applicationHomePageUrl", dataProviderClass = TestDataProvider.class)
    public void performRedirectWithoutSpecifyingUrlParamAndCheckAppHomePageIsReturned(final String expectedUrl) {
        val statusBeforeCall = statusServiceClient.getStatus();
        val previousNumberOfRequest = statusBeforeCall.getNumberOfRequest();

        val response = redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitResponseIsSuccessful("");
        val statusCode = response.getStatus();
        softAssert.assertEquals(statusCode, HttpStatus.OK_200,
                                format("Expected status code after redirect without URL specification is %s but " +
                                               "actually was %s",
                                       HttpStatus.OK_200,
                                       statusCode));

        val updatedStatus = statusServiceClient.getStatus();
        val updatedNumberOfRequest = updatedStatus.getNumberOfRequest();
        val updatedLastUrl = updatedStatus.getLastRequestUrl();
        softAssert.assertNotEquals(updatedNumberOfRequest, previousNumberOfRequest,
                                   "Number of request wasn't changed but expected");
        softAssert.assertEquals(updatedNumberOfRequest - previousNumberOfRequest, 1,
                                "Number of request should have been changed by 1");
        softAssert.assertEquals(updatedLastUrl, expectedUrl,
                                format("Expected last URL - %s, actual - %s", expectedUrl, updatedLastUrl));
        softAssert.assertAll();
    }

    @Test(dataProvider = "twoDifferentUrls", dataProviderClass = TestDataProvider.class)
    public void deleteRequestRecordAndCheckStatusFullStatisticIsUpdated(final String firstUrl,
                                                                        final String secondUrl) {
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(firstUrl);
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(secondUrl);

        val status = statusServiceClient.getStatus();
        val initialNumberOfRequest = status.getNumberOfRequest();
        softAssert.assertEquals(initialNumberOfRequest, 2,
                                format("Expected number of request - 2 but was - %s", initialNumberOfRequest));

        val actualInitialStatistic = status.getFullStatistic();
        val actualInitialStatisticSize = actualInitialStatistic.size();
        softAssert.assertEquals(actualInitialStatisticSize, 2,
                                format("Expected initial statistic size is 2 but was %s", actualInitialStatisticSize));

        val firstRequest = requestUtil.getRequestByUrl(firstUrl);
        val secondRequest = requestUtil.getRequestByUrl(secondUrl);
        val expectedInitialStatistic = List.of(firstRequest, secondRequest);
        softAssert.assertTrue(actualInitialStatistic.containsAll(expectedInitialStatistic),
                              format("Full statistic contains records about requests - %s, but expected - %s",
                                     actualInitialStatistic, expectedInitialStatistic));

        requestActionExecutor.deleteRequestAndWaitItIsAbsentInDb(firstRequest.getId());

        val updatedStatus = statusServiceClient.getStatus();
        val updatedStatistic = updatedStatus.getFullStatistic();
        val updatedStatisticSize = updatedStatistic.size();
        softAssert.assertEquals(updatedStatisticSize, 1,
                                format("Expected updated statistic size is 1 but was %s", updatedStatisticSize));
        val expectedStatisticAfterRequestDeletion = List.of(secondRequest);
        softAssert.assertEquals(updatedStatistic, expectedStatisticAfterRequestDeletion,
                                format("Expected statistic after request deletion - %s, actual - %s",
                                       expectedStatisticAfterRequestDeletion,
                                       updatedStatistic));
        softAssert.assertAll();
    }

    @Test(dataProvider = "singleUrl", dataProviderClass = TestDataProvider.class)
    public void testGetRequestById(final String url) {
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(url);

        val status = statusServiceClient.getStatus();
        val fullStatistic = status.getFullStatistic();
        val fullStatisticSize = fullStatistic.size();
        softAssert.assertEquals(fullStatisticSize, 1,
                                format("Expected statistic size is 1 but was %s", fullStatisticSize));

        val createdRequest = fullStatistic.get(0);
        val requestId = createdRequest.getId();
        val requestFoundById = requestUtil.getRequestById(requestId);
        softAssert.assertEquals(requestFoundById, createdRequest,
                                format("Full statistic contains unexpected request:\nExpected - %s, actual - %s",
                                       createdRequest, requestFoundById));
    }

    @Test(dataProvider = "twoDifferentUrls", dataProviderClass = TestDataProvider.class)
    public void testGetLastRequest(final String firstUrl,
                                   final String secondUrl) {
        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(firstUrl);
        val status1 = statusServiceClient.getStatus();
        val statistic1 = status1.getFullStatistic();
        val lastUrlStatus1 = status1.getLastRequestUrl();
        softAssert.assertEquals(lastUrlStatus1, firstUrl,
                                format("Expected last URL - %s, actual - %s", firstUrl, lastUrlStatus1));

        val lastRequestApi1 = requestServiceClient.getLastRequest();
        val lastStatisticRequest1 = Iterables.getLast(statistic1);
        softAssert.assertEquals(lastRequestApi1, lastStatisticRequest1,
                                format("Unexpected last request in statistic was found.\nExpected -%s, actual - %s",
                                       lastRequestApi1, lastStatisticRequest1));

        redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(secondUrl);
        val status2 = statusServiceClient.getStatus();
        val statistic2 = status2.getFullStatistic();
        val lastUrlStatus2 = status2.getLastRequestUrl();
        softAssert.assertEquals(lastUrlStatus2, secondUrl,
                                format("Expected last URL - %s, actual - %s", secondUrl, lastUrlStatus2));

        val lastRequestApi2 = requestServiceClient.getLastRequest();
        val lastStatisticRequest2 = Iterables.getLast(statistic2);
        softAssert.assertEquals(lastRequestApi2, lastStatisticRequest2,
                                format("Unexpected last request in full statistic was found.\nExpected -%s, actual - " +
                                               "%s",
                                       lastRequestApi2, lastStatisticRequest2));
    }

    @Test
    public void testGetRequestByWrongId() {
        val response = requestServiceClient.getRequestById(NON_EXISTING_REQUEST_ID);
        val statusCode = response.getStatus();
        softAssert.assertEquals(statusCode, HttpStatus.NOT_FOUND_404,
                                format("Expected status code when attempt to get non-existing request was made - %s, " +
                                               "actual - %s",
                                       HttpStatus.NOT_FOUND_404, statusCode));
    }

    @Test
    public void deleteNonExistingRequest() {
        val originalStatus = statusServiceClient.getStatus();
        val isDeleted = requestServiceClient.deleteRequestById(NON_EXISTING_REQUEST_ID);
        softAssert.assertFalse(isDeleted, "Deletion of non-existing request ended up with true but must be false");

        softAssert.assertEquals(statusServiceClient.getStatus(), originalStatus,
                                "Status was changed but shouldn't");
    }
}
