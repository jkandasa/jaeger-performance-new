package io.jaegertracing.tests;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Timer;

import io.jaegertracing.tests.report.ReportFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Pavol Loffay
 */
@Slf4j
public abstract class UntilNoChangeCounter implements ISpanCounter {

    private Timer queryTimer;
    private Timer queryUntilNoChangeTimer;

    public UntilNoChangeCounter() {
        this.queryTimer = ReportFactory.timer("until-no-change-single-span-counter");
        this.queryUntilNoChangeTimer = ReportFactory.timer("until-no-change-span-counter");
    }

    @Override
    public int countUntilNoChange(int expected) {
        long startUntilNoChange = System.currentTimeMillis();

        try {
            await().atMost(30, TimeUnit.SECONDS)
                    .pollInterval(5, TimeUnit.SECONDS)
                    .pollDelay(0, TimeUnit.SECONDS)
                    .until(() -> {
                        long start = System.currentTimeMillis();
                        int spansCount = count();
                        long duration = System.currentTimeMillis() - start;
                        queryTimer.update(duration, TimeUnit.MILLISECONDS);
                        logger.debug("Count took: {}s, spans status[returned:{}, expected:{}]",
                                TimeUnit.MILLISECONDS.toSeconds(duration), spansCount, expected);
                        return expected <= spansCount;
                    });
        } catch (Exception ex) {
            logger.error("Exception,", ex);
        }
        logger.debug("Completed await loop. Now getting the final count.");
        int count = count();
        queryUntilNoChangeTimer.update(System.currentTimeMillis() - startUntilNoChange, TimeUnit.MILLISECONDS);
        return count;
    }
}
