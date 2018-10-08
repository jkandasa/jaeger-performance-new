package io.jaegertracing.tests.performance;

import org.junit.Assert;
import org.junit.Test;

import io.jaegertracing.tests.ParseReport;
import io.jaegertracing.tests.TestSuite;
import io.jaegertracing.tests.model.TestSuiteStatus;
import io.jaegertracing.tests.smoke.TestSuiteSmoke;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateSmokeTest extends TestSuite {

    @Test
    public void testSmokeTestReport() {
        if (config.getRunSmokeTest()) {
            TestSuiteStatus _status = ParseReport.report().getTestSuiteStatus().get(TestSuiteSmoke.SUITE_NAME);
            logger.info("Smoke test status:{}", _status);
            Assert.assertNotNull(_status);
            Assert.assertTrue(_status.getWasSuccessful());
            Assert.assertEquals("failure conunt", 0, (int) _status.getFailureCount());
        }
    }
}
