package io.jaegertracing.tests;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.jaegertracing.tests.model.TestConfig;
import io.jaegertracing.tests.report.model.JaegerTestReport;

public class TestSuite {
    private static final AtomicBoolean PERFORMANCE_TEST_EXECUTED = new AtomicBoolean(false);
    protected static TestConfig config;
    protected static JaegerTestReport report;

    @BeforeClass
    public static void setUp() throws Exception {
        if (PERFORMANCE_TEST_EXECUTED.get()) {
            return;
        }
        PERFORMANCE_TEST_EXECUTED.set(true);

        config = TestConfig.get();
        // setting up
        // if this test triggered on local, execute performance test first
        if (!config.getRunningOnOpenshift()) {
            Main instance = new Main();
            instance.execute();
        }
        // load report
        report = ParseReport.report();
    }

    @AfterClass
    public static void tearDown() {
        // tearing down
    }
}
