package io.jaegertracing.tests.report.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class MetricReport {
    private List<TimerModel> timers = new ArrayList<>();
    private List<MeterModel> meters = new ArrayList<>();
    private List<HistogramModel> histograms = new ArrayList<>();
    private List<BaseModel> counters = new ArrayList<>();
    private List<BaseModel> gauges = new ArrayList<>();
}
