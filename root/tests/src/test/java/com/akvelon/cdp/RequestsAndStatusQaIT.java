package com.akvelon.cdp;

import com.akvelon.cdp.data.TestDataProvider;
import com.akvelon.cdp.entitieslibrary.Request;
import lombok.val;
import org.testng.annotations.Test;

import static java.lang.String.format;

public class RequestsAndStatusQaIT extends QaBase {

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
                                                     final int expectedRequestNumber) { //todo: needs timeouts
        val redirectSuccessful = requestServiceClient.redirectToSpecifiedUrlAndUpdateStatistic(url);
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
        val expectedRequest = getRequestByUrl(url);
        softAssert.assertTrue(statistic.contains(expectedRequest),
                format("Request %s is not included to full statistic.\nActual statistic:\n%s",
                expectedRequest, statistic));

        softAssert.assertAll();
    }

    private Request getRequestByUrl(final String url) {
        return requestServiceClient.getAllRequests()
                .stream()
                .filter(request -> request.getRequestUrl().contains(url))
                .findFirst().orElse(null);
    }

    private String getLastRequestUrlSkipProtocol(final String visitedUrl) {
        final String http = "http://";
        final String https = "https://";
        if (visitedUrl.contains(http)) {
            return visitedUrl.substring(http.length());
        }
        if (visitedUrl.contains(https)) {
            return visitedUrl.substring(https.length());
        }
        return visitedUrl;
    }
}
