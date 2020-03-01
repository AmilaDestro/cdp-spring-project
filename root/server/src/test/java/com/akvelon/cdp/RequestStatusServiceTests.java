package com.akvelon.cdp;

import static com.akvelon.cdp.TestData.DEFAULT_ID;
import static com.akvelon.cdp.TestData.REQUEST_ENTITY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.akvelon.cdp.endpoints.RequestStatusController;
import com.akvelon.cdp.entities.RequestStatus;
import com.akvelon.cdp.services.status.StatusService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class RequestStatusServiceTests {

    @MockBean
    private RequestStatusController statusController;
    @MockBean
    private StatusService statusService;

    @Test
    public void testGetStatusController() {
        when(statusController.getStatus()).thenReturn(mock(RequestStatus.class));
        verifyNoMoreInteractions(statusController);
    }

    @Test
    public void testGetOrCreateStatus() {
        when(statusService.getOrCreateStatus()).thenReturn(mock(RequestStatus.class));
    }

    @Test
    public void testUpdateStatus() {
        when(statusService.updateStatus(DEFAULT_ID, REQUEST_ENTITY))
                .thenReturn(mock(RequestStatus.class));
    }

    @Test
    public void testDeleteStatus() {
        when(statusService.deleteStatus()).thenReturn(true);
    }
}
