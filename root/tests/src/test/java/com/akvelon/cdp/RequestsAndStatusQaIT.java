package com.akvelon.cdp;

import com.akvelon.cdp.data.TestDataProvider;
import com.akvelon.cdp.executors.RedirectAndStatusUpdateExecutor;
import com.akvelon.cdp.utils.ExceptionsUtil;
import com.akvelon.cdp.utils.HttpResponseUtil;
import com.akvelon.cdp.utils.RequestUtil;
import lombok.val;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.akvelon.cdp.utils.RequestUtil.getLastRequestUrlSkipProtocol;
import static java.lang.String.format;

public class RequestsAndStatusQaIT extends QaBase {
    private final RedirectAndStatusUpdateExecutor redirectAndStatusUpdateExecutor;
    private final RequestUtil requestUtil;
    private final HttpResponseUtil httpResponseUtil;
    private final ExceptionsUtil exceptionUtils;

    public RequestsAndStatusQaIT() {
        redirectAndStatusUpdateExecutor =
                new RedirectAndStatusUpdateExecutor(this.requestServiceClient, this.statusServiceClient);
        requestUtil = new RequestUtil(this.requestServiceClient);
        httpResponseUtil = new HttpResponseUtil(this.requestServiceClient);
        exceptionUtils = new ExceptionsUtil(this.requestServiceClient);
    }

    @AfterMethod(alwaysRun = true)
    public void clearAfterMethod() {
        clearTestEnvironment();
    }

    @Test
    public void test0GetEmptyStatusRecordWhenNoRequestsPerformed() {
        val status = statusServiceClient.getStatus();
        softAssert.assertNotNull(status, "Returned Status entity is null");
        softAssert.assertEquals(status.getNumberOfRequest(), 0,
                "Number of request for empty status should be 0");
        softAssert.assertTrue(status.getFullStatistic().isEmpty(),
                "Empty status should not contain information about requests");
        softAssert.assertAll();
    }

    @Test(dataProviderClass = TestDataProvider.class, dataProvider = "urlsToRedirectAndNumbersOfRequest")
    public void test1RedirectToSpecifiedUrlAndCheckStatus(final String url,
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
    public void test2Perform2RedirectsAndCheckStatistics(final String firstUrl,
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
    public void test3RedirectToWrongUrlAndExpectError() {
        val wrongUrl = "nonExistingUrl";
        val response = requestServiceClient.redirectToSpecifiedUrlUpdateStatisticAndReturnStatusCode(wrongUrl);
        val serverErrorJson = httpResponseUtil.getExceptionInJsonResponse(response);
        val exception = exceptionUtils.getExceptionFromJson(serverErrorJson);
        softAssert.assertNotNull(exception);
        softAssert.assertAll();
    }
}
