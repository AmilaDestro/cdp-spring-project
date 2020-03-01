package com.akvelon.cdp;

import com.akvelon.cdp.data.TestDataProvider;
import com.akvelon.cdp.executors.RedirectAndStatusUpdateExecutor;
import com.akvelon.cdp.utils.ServerExceptionsUtil;
import com.akvelon.cdp.utils.HttpResponseUtil;
import com.akvelon.cdp.utils.RequestUtil;
import lombok.val;
import org.eclipse.jetty.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.akvelon.cdp.utils.RequestUtil.getLastRequestUrlSkipProtocol;
import static java.lang.String.format;

public class RequestsAndStatusQaIT extends QaBase {
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
                format("Expected number of request for %s - %s, actual - %s", url, expectedRequestNumber,
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
        val allExpectedRequests = List.of(requestUtil.getRequestByUrl(firstUrl), requestUtil.getRequestByUrl(secondUrl));
        softAssert.assertTrue(allActualRequests.containsAll(allExpectedRequests),
                format("Expected requests - %s\nActual requests - %s", allExpectedRequests, allActualRequests));
        softAssert.assertTrue(fullStatistic.containsAll(allActualRequests),
                format("Expected full statistic - %s\nActual full statistic - %s", allActualRequests, fullStatistic));
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
}
