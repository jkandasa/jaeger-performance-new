package io.jaegertracing.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Timer;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Sender;
import io.jaegertracing.tests.model.TestConfig;
import io.jaegertracing.tests.report.ReportFactory;
import io.jaegertracing.thrift.internal.senders.HttpSender;
import io.jaegertracing.thrift.internal.senders.UdpSender;
import io.opentracing.tag.Tags;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    static final String SERVICE_NAME = "performance-test";
    static final String TRACER_PREFIX = "tracer_";

    static Map<String, String> getNonseseTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("fooo.bar1", "fobarhax*+??");
        tags.put("fooo.ba2sar", "true");
        tags.put("fooo.ba4342r", "1");
        tags.put("fooo.ba24r*?%", "hehe");
        tags.put("fooo.bar*?%http.d6cconald", "hehuhoh$?ij");
        tags.put("fooo.bar*?%http.do**2nald", "goobarRAXbaz");
        tags.put("fooo.bar*?%http.don(a44ld", "goobarRAXbaz");
        return tags;
    }

    static Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);
        tags.put(Tags.HTTP_METHOD.getKey(), "get");
        tags.put(Tags.HTTP_METHOD.getKey(), "get");
        return tags;
    }

    public static void main(String[] args) throws Exception {
        Main instance = new Main();
        instance.triggerCreateSpans();
    }

    TestConfig config;

    private final int expectedSpansCount;

    private Main() {
        config = TestConfig.loadFromEnvironment();
        // custom
        //config.setTestDuration(60 * 3);
        //config.setTracersCount(6);
        //config.setSpansCount(100);
        //config.setQueryInterval(30);
        //config.setSpansCountFrom("jaeger-query");

        // wait for jaeger agent to get ready
        if (config.getRunningOnOpenshift()) {
            if (config.getSender().equalsIgnoreCase("udp")) {
                try {
                    logger.info("This test is running on OpenShift environment and jaeger-agent deployed as a sidecar."
                            + " There is pre-defined delay(30 seconds) to get ready 'jager-agent'.");
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException ex) {
                    logger.error("Exception,", ex);
                }
            }
        }

        expectedSpansCount = config.getTracersCount() * config.getSpansCount() * config.getTestDuration();
    }

    private JaegerTracer createJaegerTracer(String serviceName) {
        Sender sender;
        if (config.getSender().equalsIgnoreCase("udp")) {
            sender = new UdpSender(
                    config.getJaegerAgentHost(),
                    config.getJaegerAgentPort(),
                    config.getJaegerMaxPocketSize());
            logger.info("Using UDP sender, sending to: {}:{}",
                    config.getJaegerAgentHost(), config.getJaegerAgentPort());
        } else {
            // use the collector
            String httpEndpoint = "http://" + config.getJaegerCollectorHost() + ":" + config.getJaegerCollectorPort()
                    + "/api/traces";
            logger.info("Using HTTP sender, sending to endpoint: {}", httpEndpoint);
            sender = new HttpSender.Builder(httpEndpoint).build();
        }

        logger.info("Flush interval {}, queue size {}", config.getJaegerFlushInterval(),
                config.getJaegerMaxQueueSize());
        RemoteReporter reporter = new RemoteReporter.Builder()
                .withSender(sender)
                .withMaxQueueSize(config.getJaegerMaxQueueSize())
                .withFlushInterval(config.getJaegerFlushInterval())
                .build();

        return new JaegerTracer.Builder(serviceName)
                .withReporter(reporter)
                .withSampler(new ConstSampler(true))
                .build();
    }

    private ISpanCounter getSpanCounter(Set<String> serviceNames) {
        ISpanCounter spanCounter;
        if (config.getSpansCountFrom().equalsIgnoreCase("storage")) {
            if ("elasticsearch".equals(config.getStorageType())) {
                spanCounter = new ElasticsearchSpanCounter(config);
            } else {
                spanCounter = new CassandraSpanCounter(config);
            }
        } else {
            spanCounter = new JaegerQuerySpanCounter(config, serviceNames, false);
        }
        return spanCounter;
    }

    public void triggerCreateSpans() throws Exception {
        logger.debug("{}", config);
        Timer reportingTimer = ReportFactory.timer("report-spans");
        long startTime = System.currentTimeMillis();
        // + 1, for query interval execution
        ExecutorService executor = Executors.newFixedThreadPool(config.getTracersCount() + 1);
        List<Future<?>> futures = new ArrayList<>(config.getTracersCount());
        Set<String> serviceNames = new LinkedHashSet<>();
        for (int tracerNumber = 1; tracerNumber <= config.getTracersCount(); tracerNumber++) {
            String name = TRACER_PREFIX + tracerNumber;
            JaegerTracer tracer = createJaegerTracer(SERVICE_NAME + "-" + name);
            serviceNames.add(tracer.getServiceName());
            Runnable worker = new CreateSpansRunnable(tracer, name, config, true);
            futures.add(executor.submit(worker));
        }
        // add a worker for query execution time
        if (config.getQueryInterval() != -1) {
            Runnable queryWorker = new JaegerQueryRunnable(config, new ArrayList<>(serviceNames).get(0),
                    TRACER_PREFIX + 1,
                    Arrays.asList(getNonseseTags(), getTags()));
            futures.add(executor.submit(queryWorker));
        }

        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdownNow();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        reportingTimer.update(duration, TimeUnit.MILLISECONDS);
        logger.info("Finished sending spans to jaeger service. Spans sent:{}, timetaken:{}",
                config.getTracersCount() * config.getSpansCount(), TimeUnit.MILLISECONDS.toSeconds(duration));
        ISpanCounter spanCounter = getSpanCounter(serviceNames);
        startTime = System.currentTimeMillis();
        int spansCount = spanCounter.countUntilNoChange(expectedSpansCount);
        duration = System.currentTimeMillis() - startTime;
        ReportFactory.updateSpansCount(expectedSpansCount, spansCount);
        logger.info("Exceuted spans count. timetaken:{}, spans[expected:{}, actual:{}]",
                TimeUnit.MILLISECONDS.toSeconds(duration), expectedSpansCount, spansCount);

        JaegerQueryRunnable jaegerQuery = new JaegerQueryRunnable(config,
                new ArrayList<>(serviceNames).get(0), TRACER_PREFIX + 1, Arrays.asList(getNonseseTags(), getTags()));
        jaegerQuery.execute("FINAL_");

        if (config.getSpansCountFrom().equalsIgnoreCase("storage")
                && config.getStorageType().equalsIgnoreCase("elasticsearch")) {
            ElasticsearchStatsGetter esStatsGetter = new ElasticsearchStatsGetter(
                    config.getStorageHost(), config.getStoragePort());
            esStatsGetter.printStats();
            esStatsGetter.close();
        }
        logger.info("[DROP_COUNT={}]", ReportFactory.getDroupCount());
        logger.info("[DROP_PERCENTAGE={}]", ReportFactory.getDroupPercentage());
        logger.info("[SPANS_SENT={}]", ReportFactory.getSpansSent());
        logger.info("[SPANE_FOUND={}]", ReportFactory.getSpansFound());
        if (ReportFactory.getDroupCount() > 0) {
            logger.info("[TEST_STATUS=PASSED]", ReportFactory.getSpansFound());
        } else {
            logger.error("[TEST_STATUS=FAILED]", ReportFactory.getSpansFound());
        }
        logger.info("Final Report as json:\n@@START@@\n{}\n@@END@@", ReportFactory.getReport(config));
        ReportFactory.saveReport(config, "/tmp/performance_report.json");

        spanCounter.close();
        jaegerQuery.close();
    }
}
