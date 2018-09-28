package io.jaegertracing.tests.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import io.jaegertracing.client.Version;

import io.jaegertracing.tests.JsonUtils;
import io.jaegertracing.tests.model.TestConfig;
import io.jaegertracing.tests.report.model.JaegerTestReport;

public class ReportFactory {
    // final report
    private static final JaegerTestReport TEST_REPORT = JaegerTestReport.builder().build();

    private static final MetricRegistry METRICS_REGISTRY = new MetricRegistry();

    private static long spanCountSent = -1;
    private static long spanCountFound = -1;

    public static String getReport(TestConfig config) {
        updateReport(config);
        return JsonUtils.asString(TEST_REPORT);
    }

    public static void saveReport(TestConfig config, String filename) {
        updateReport(config);
        // save it in file
        JsonUtils.dumps(TEST_REPORT, filename);
    }

    public static Timer timer(String name) {
        return METRICS_REGISTRY.timer(name);
    }

    private static void updateReport(TestConfig config) {
        // add test configurations
        TEST_REPORT.setConfig(config);

        // update Jaeger java client version
        TEST_REPORT.getConfig().setJaegerClientVersion(Version.get());

        // disable metric attributes
        Set<MetricAttribute> disabledMetric = new HashSet<>();
        disabledMetric.add(MetricAttribute.MEAN_RATE);
        disabledMetric.add(MetricAttribute.M1_RATE);
        disabledMetric.add(MetricAttribute.M5_RATE);
        disabledMetric.add(MetricAttribute.M15_RATE);

        // update metrics
        TEST_REPORT.setMetric(
                JsonReporter.forRegistry(METRICS_REGISTRY)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .disabledMetricAttributes(disabledMetric)
                        .build().getReport());

        // update count statistics
        TEST_REPORT.setSpansCountStatistics(new HashMap<String, Object>());
        TEST_REPORT.getSpansCountStatistics().put("sent", spanCountSent);
        TEST_REPORT.getSpansCountStatistics().put("found", spanCountFound);

        long dropped_count = getDroupCount();
        double dropped_percentage = getDroupPercentage();
        final int spansPersecond = config.getTracersCount() * config.getSpansCount();

        TEST_REPORT.getSpansCountStatistics().put("dropped_count", dropped_count);
        TEST_REPORT.getSpansCountStatistics().put("dropped_percentage",
                dropped_percentage < 0 ? 0 : dropped_percentage);
        TEST_REPORT.getSpansCountStatistics().put("per_second", spansPersecond);
        TEST_REPORT.getSpansCountStatistics().put("per_minute", spansPersecond * 60);
    }

    public static void updateSpansCount(long sent, long found) {
        spanCountSent = sent;
        spanCountFound = found;
    }

    public static long getDroupCount() {
        return spanCountSent - spanCountFound;
    }

    public static double getDroupPercentage() {
        return ((double) getDroupCount() / spanCountSent) * 100.0;
    }

    public static long getSpansSent() {
        return spanCountSent;
    }

    public static long getSpansFound() {
        return spanCountFound;
    }

}
