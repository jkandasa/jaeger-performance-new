package io.jaegertracing.tests.report.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class TimerModel {
    private String name;
    private Long count;
    private Map<String, Object> rate;
    private Map<String, Object> duration;
}
