package io.jaegertracing.tests.smoke;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import io.jaegertracing.tests.smoke.tests.BasicSpanTest;
import io.jaegertracing.tests.smoke.tests.FirstJaegerTest;
import io.jaegertracing.tests.smoke.tests.SimpleUITest;
import io.jaegertracing.tests.smoke.tests.TagAndDurationTest;

@RunWith(Suite.class)
@SuiteClasses({
        BasicSpanTest.class,
        FirstJaegerTest.class,
        SimpleUITest.class,
        TagAndDurationTest.class })
public class TestSuiteSmoke {

    public static final String SUITE_NAME = "smoke_test";

    @BeforeClass
    public static void setUp() throws Exception {
        // setting up
    }

    @AfterClass
    public static void tearDown() {
        // tearing down
    }
}
