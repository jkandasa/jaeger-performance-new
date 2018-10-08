package io.jaegertracing.tests.model;

import org.junit.runner.Result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestSuiteStatus {
    private String name;
    private Boolean wasSuccessful;
    private Integer failureCount;
    private Integer runCount;
    private Integer ignoreCount;
    private Long runTime;

    public static TestSuiteStatus get(String name, Result testResult) {
        testResult.wasSuccessful();
        return TestSuiteStatus.builder()
                .name(name)
                .wasSuccessful(testResult.wasSuccessful())
                .runCount(testResult.getRunCount())
                .failureCount(testResult.getFailureCount())
                .ignoreCount(testResult.getIgnoreCount())
                .runTime(testResult.getRunTime())
                .build();
    }
}
