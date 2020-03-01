package com.akvelon.cdp;

import static com.akvelon.cdp.TestData.DEFAULT_ID;
import static com.akvelon.cdp.TestData.EMPTY_STRING;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import com.akvelon.cdp.endpoints.RequestController;
import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.exceptions.RequestNotFoundException;
import com.akvelon.cdp.services.requests.RequestWithMetrics;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;

@SpringBootTest
public class RequestServiceTests {

    @MockBean
    private RequestController requestController;
    @MockBean
    private RequestWithMetrics requestService;

    @SneakyThrows
    @Test
    public void testRedirectSuccessful() {
        when(requestController.redirectToSpecifiedUrlAndUpdateStatistic(EMPTY_STRING))
                .thenReturn(mock(Object.class));
        verifyNoMoreInteractions(requestController);
    }

    @SneakyThrows
    @Test
    public void testRequestCreated() {
        when(requestService.sendGetRequestAndReturnPage(EMPTY_STRING)).thenReturn(mock(Pair.class));
    }

    @SneakyThrows
    @Test
    public void testGetRequest() {
        when(requestService.getInternalRequest(DEFAULT_ID))
                .thenReturn(mock(Request.class))
                .thenThrow(RequestNotFoundException.class);
    }

    @Test
    public void testGetRequests() {
        when(requestService.getInternalRequests()).thenReturn(List.of());
    }

    @Test
    public void testGetLastRequest() {
        when(requestService.getLastCreatedInternalRequest()).thenReturn(mock(Request.class));
    }

    @SneakyThrows
    @Test
    public void testDeleteRequest() {
        when(requestService.deleteInternalRequest(DEFAULT_ID))
                .thenReturn(true)
                .thenThrow(RequestNotFoundException.class);
    }
}
