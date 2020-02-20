package com.akvelon.cdp;

import com.akvelon.cdp.endpoints.RequestController;
import com.akvelon.cdp.entities.Request;
import com.akvelon.cdp.exceptions.RequestNotFoundException;
import com.akvelon.cdp.services.requests.RequestWithMetrics;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import static com.akvelon.cdp.TestData.DEFAULT_ID;
import static com.akvelon.cdp.TestData.EMPTY_STRING;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
                .thenReturn(mock(RedirectView.class));
        verifyNoMoreInteractions(requestController);
    }

    @SneakyThrows
    @Test
    public void testRequestCreated() {
        when(requestService.createRequest(EMPTY_STRING)).thenReturn(mock(Request.class));
    }

    @SneakyThrows
    @Test
    public void testGetRequest() {
        when(requestService.getRequest(DEFAULT_ID))
                .thenReturn(mock(Request.class))
                .thenThrow(RequestNotFoundException.class);
    }

    @Test
    public void testGetRequests() {
        when(requestService.getRequests()).thenReturn(List.of());
    }

    @Test
    public void testGetLastRequest() {
        when(requestService.getLastCreatedRequest()).thenReturn(mock(Request.class));
    }

    @SneakyThrows
    @Test
    public void testDeleteRequest() {
        when(requestService.deleteRequest(DEFAULT_ID))
                .thenReturn(true)
                .thenThrow(RequestNotFoundException.class);
    }
}
