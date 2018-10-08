package io.jaegertracing.tests.performance;

import org.junit.Assert;
import org.junit.Test;

import io.jaegertracing.tests.TestSuite;
import io.jaegertracing.tests.report.model.TimerModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceTest extends TestSuite {

    private static final double MAX_ACCEPTED_DURATION = 1000 * 20; // 20 seconds

    @Test
    public void test01ReportStatus() {
        Assert.assertNotNull(report);
    }

    @Test
    public void test02SpansCount() {
        Number spansSent = (Number) report.getSpansCountStatistics().get("sent");
        Number spansFound = (Number) report.getSpansCountStatistics().get("found");
        logger.debug("Spans[sent:{}, found:{}]", spansSent.longValue(), spansFound.longValue());
        Assert.assertEquals(spansSent, spansFound);
    }

    @Test
    public void test03QueryTime() {
        for (TimerModel timer : report.getMetric().getTimers()) {
            if (timer.getName().startsWith("FINAL")) {
                double mean = (double) timer.getDuration().get("mean");
                double max = (double) timer.getDuration().get("max");
                double min = (double) timer.getDuration().get("min");
                logger.debug("Time taken, name:{}, count:{}, mean:{}, max:{}, min:{}",
                        timer.getName(), timer.getCount(), mean, max, min);
                Assert.assertTrue(mean <= MAX_ACCEPTED_DURATION);
            }
        }
    }
}
