package io.jaegertracing.tests;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.jaegertracing.tests.model.TestConfig;
import io.jaegertracing.tests.run.PerformanceTest;
import io.jaegertracing.tests.run.SmokeTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEnabled implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        TestConfig config = TestConfig.get();
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (description.getClassName().contains(PerformanceTest.class.getName())) {
                    if (config.isPerformanceTestEnabled()) {
                        base.evaluate();
                    } else {
                        logger.info("Test class disabled:[{}]", description.getClassName());
                    }
                } else if (description.getClassName().contains(SmokeTest.class.getName())) {
                    if (config.isSmokeTestEnabled()) {
                        base.evaluate();
                    } else {
                        logger.info("Test class disabled:[{}]", description.getClassName());
                    }
                } else {
                    base.evaluate();
                }

            }
        };
    }

}
