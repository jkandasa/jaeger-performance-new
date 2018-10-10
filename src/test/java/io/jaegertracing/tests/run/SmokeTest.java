package io.jaegertracing.tests.run;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import io.jaegertracing.tests.ParseReport;
import io.jaegertracing.tests.TestEnabled;
import io.jaegertracing.tests.TestSuite;
import io.jaegertracing.tests.model.TestSuiteStatus;
import io.jaegertracing.tests.smoke.TestSuiteSmoke;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmokeTest extends TestSuite {

    @ClassRule
    public static TestEnabled testEnabled = new TestEnabled();

    @Test
    public void testSmokeTestReport() {
        TestSuiteStatus _status = ParseReport.report().getTestSuiteStatus().get(TestSuiteSmoke.SUITE_NAME);
        logger.info("Smoke test status:{}", _status);
        Assert.assertNotNull(_status);
        Assert.assertTrue(_status.getWasSuccessful());
        Assert.assertEquals("failure conunt", 0, (int) _status.getFailureCount());
    }
}
