package com.akvelon.cdp.executors;

import com.akvelon.cdp.clients.RequestServiceClient;
import com.akvelon.cdp.clients.StatusServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@AllArgsConstructor
public class RedirectAndStatusUpdateExecutor {
    private RequestServiceClient requestServiceClient;
    private StatusServiceClient statusServiceClient;

    public boolean redirectToSpecifiedUrlAndWaitForStatusUpdate(final String url) {
        log.debug("Performing redirect to {} and waiting for status updates", url);
        final boolean redirectSuccessful = requestServiceClient.redirectToSpecifiedUrlAndUpdateStatistic(url);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (statusServiceClient.getStatus().getLastRequestUrl().contains(url)) {
                    timer.cancel();
                }
            }
        }, 0, 30000);
        return redirectSuccessful;
    }
}
