package io.jaegertracing.tests.report.model;

import java.util.Map;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import io.jaegertracing.tests.model.TestConfig;
import lombok.Builder;
import lombok.Data;
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
}
