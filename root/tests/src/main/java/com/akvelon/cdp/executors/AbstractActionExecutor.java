package com.akvelon.cdp.executors;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractActionExecutor {

    public void executeTaskAndWaitForConditionSuccess(final Runnable runnable, final Supplier<Boolean> condition) {
        log.debug("Executing task");
        runnable.run();
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (condition.get()) {
                    log.debug("Finishing task execution");
                    timer.cancel();
                }
            }
        }, 2000, 15000);
    }

    public Object executeTaskAndWaitForResponse(final Supplier<Object> task,
                                                final int timeOut,
                                                final TimeUnit timeUnit) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<Object> future = executorService.submit(getCallable(task));
        try {
            return future.get(timeOut, timeUnit);
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Exception was thrown during task execution: {}", e.getMessage());
            throw new RuntimeException();
        }
    }

    private Callable<Object> getCallable(final Supplier<Object> supplier) {
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return supplier.get();
            }
        };
    }
}
