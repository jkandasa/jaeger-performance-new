package io.jaegertracing.tests.report.model;

import java.util.Map;

import io.jaegertracing.tests.model.TestConfig;
import io.jaegertracing.tests.model.TestSuiteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class JaegerTestReport {
    private TestConfig config;
    private MetricReport metric;
    private Map<String, Object> spansCountStatistics;
    private Map<String, TestSuiteStatus> testSuiteStatus;
}
