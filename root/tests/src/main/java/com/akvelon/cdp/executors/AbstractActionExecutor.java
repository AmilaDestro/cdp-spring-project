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

/**
 * This class contains methods capable to execute tasks with timeout and wait for some condition
 */
@Slf4j
public class AbstractActionExecutor {

    /**
     * Executes specified task from {@link Runnable} and waits until supplied condition is fullfilled
     * or ends wait if timeout is reached
     *
     * @param runnable  - task to execute
     * @param condition - {@link Supplier<Boolean>} with condition that indicates that wait can be interrupted
     */
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

    /**
     * Executes task andwaits for response during specified period of time
     *
     * @param task     - to execute
     * @param timeOut  - during which response is being waited
     * @param timeUnit - {@link TimeUnit} to wait
     * @return object from received response
     */
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
        return supplier::get;
    }
}
