package io.jaegertracing.tests.report.model;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class BaseModel {
    private String name;
    private String key;
    private Object value;
}
