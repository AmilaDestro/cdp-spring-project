package com.akvelon.cdp;

import com.akvelon.cdp.data.TestDataProvider;
import com.akvelon.cdp.executors.RedirectAndStatusUpdateExecutor;
import com.akvelon.cdp.utils.RequestUtil;
import lombok.val;
import org.testng.annotations.Test;

import static com.akvelon.cdp.utils.RequestUtil.getLastRequestUrlSkipProtocol;
import static java.lang.String.format;

public class RequestsAndStatusQaIT extends QaBase {
    private final RedirectAndStatusUpdateExecutor redirectAndStatusUpdateExecutor;
    private final RequestUtil requestUtil;

    public RequestsAndStatusQaIT() {
        redirectAndStatusUpdateExecutor =
                new RedirectAndStatusUpdateExecutor(this.requestServiceClient, this.statusServiceClient);
        requestUtil = new RequestUtil(this.requestServiceClient);
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
    public void redirectToSpecifiedUrlAndCheckStatus(final String url,
                                                     final int expectedRequestNumber) {
        val redirectSuccessful = redirectAndStatusUpdateExecutor.redirectToSpecifiedUrlAndWaitForStatusUpdate(url);
        softAssert.assertTrue(redirectSuccessful, format("Redirect to URL %s was unsuccessful", url));

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
}
