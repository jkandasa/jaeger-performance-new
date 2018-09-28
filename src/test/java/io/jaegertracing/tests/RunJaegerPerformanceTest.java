package io.jaegertracing.tests;

import org.junit.Assert;
import org.junit.Test;

import io.jaegertracing.tests.report.model.TimerModel;
import io.jaegertracing.tests.report.model.JaegerTestReport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunJaegerPerformanceTest {

    private static final double MAX_ACCEPTED_DURATION = 1000 * 20; // 20 seconds

    @Test
    public void test01ReportStatus() {
        JaegerTestReport report = PerformanceReportFactory.report();
        Assert.assertNotNull(report);
    }

    @Test
    public void test02SpansCount() {
        JaegerTestReport report = PerformanceReportFactory.report();
        int spansSent = (int) report.getSpansCountStatistics().get("sent");
        int spansFound = (int) report.getSpansCountStatistics().get("found");
        logger.debug("Spans[sent:{}, found:{}]", spansSent, spansFound);
        Assert.assertEquals(spansSent, spansFound);
    }

    @Test
    public void test03QueryTime() {
        JaegerTestReport report = PerformanceReportFactory.report();
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
