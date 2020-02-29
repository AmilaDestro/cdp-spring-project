package com.akvelon.cdp.executors;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

@Slf4j
public class AbstractActionExecutor {

    public void executeAndWait(final Runnable runnable, final Supplier<Boolean> supplier) {
        log.debug("Executing task");
        runnable.run();
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (supplier.get()) {
                    log.debug("Finishing task execution");
                    timer.cancel();
                }
            }
        }, 2000, 15000);
    }
}
