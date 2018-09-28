package io.jaegertracing.tests.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import static io.jaegertracing.tests.TestUtils.getBooleanEnv;
import static io.jaegertracing.tests.TestUtils.getFloatEnv;
import static io.jaegertracing.tests.TestUtils.getIntegerEnv;
import static io.jaegertracing.tests.TestUtils.getStringEnv;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class TestConfig {

    public static TestConfig loadFromEnvironment() {
        return TestConfig
                .builder()
                .testDuration(getIntegerEnv("TEST_DURATION", "300"))
                .tracersCount(getIntegerEnv("NUMBER_OF_TRACERS", "5"))
                .runningOnOpenshift(getBooleanEnv("RUNNING_ON_OPENSHIFT", "false"))
                .logsDirectory(getStringEnv("LOGS_DIRECTORY", "logs/"))
                .spansCount(getIntegerEnv("NUMBER_OF_SPANS", "10"))
                .queryLimit(getIntegerEnv("QUERY_LIMIT", "20000"))
                .querySamples(getIntegerEnv("QUERY_SAMPLES", "20"))
                .queryInterval(getIntegerEnv("QUERY_INTERVAL", "60"))
                .sender(getStringEnv("SENDER", "http"))
                .storageType(getStringEnv("STORAGE_TYPE", "elasticsearch"))
                .spansCountFrom(getStringEnv("SPANS_COUNT_FROM", "storage"))
                .storageHost(getStringEnv("STORAGE_HOST", "localhost"))
                .storagePort(getIntegerEnv("STORAGE_PORT", "9200"))
                .storageKeyspace(getStringEnv("STORAGE_KEYSPACE", "keyspace"))
                .jaegerQueryHost(getStringEnv("JAEGER_QUERY_HOST", "localhost"))
                .jaegerQueryPort(getIntegerEnv("JAEGER_QUERY_PORT", "16686"))
                .jaegerCollectorHost(getStringEnv("JAEGER_COLLECTOR_HOST", "localhost"))
                .jaegerCollectorPort(getIntegerEnv("JAEGER_COLLECTOR_PORT", "14268"))
                .jaegerAgentHost(getStringEnv("JAEGER_AGENT_HOST", "localhost"))
                .jaegerAgentPort(getIntegerEnv("JAEGER_AGENT_PORT", "6831"))
                .jaegerFlushInterval(getIntegerEnv("JAEGER_FLUSH_INTERVAL", "100"))
                .jaegerMaxPocketSize(getIntegerEnv("JAEGER_MAX_POCKET_SIZE", "0"))
                .jaegerSamplingRate(getFloatEnv("JAEGER_SAMPLING_RATE", "1.0"))
                .jaegerMaxQueueSize(getIntegerEnv("JAEGER_MAX_QUEUE_SIZE", "10000"))
                .collectorPods(getIntegerEnv("COLLECTOR_PODS", "1"))
                .collectorQueueSize(getIntegerEnv("COLLECTOR_QUEUE_SIZE", "2000"))
                .collectorWorkersCount(getIntegerEnv("COLLECTOR_NUM_WORKERS", "50"))
                .queryStaticFiles(getStringEnv("QUERY_STATIC_FILES", ""))
                .esMemory(getStringEnv("ES_MEMORY", "1Gi"))
                .esBulkSize(getIntegerEnv("ES_BULK_SIZE", "5000000"))
                .esBulkWorkers(getIntegerEnv("ES_BULK_WORKERS", "1"))
                .esBulkFlushInterval(getStringEnv("ES_BULK_FLUSH_INTERVAL", "200ms"))
                .imageAgent(getStringEnv("JAEGER_AGENT_IMAGE", "jaegertracing/jaeger-agent:latest"))
                .imageCollector(getStringEnv("JAEGER_COLLECTOR_IMAGE", "jaegertracing/jaeger-collector:latest"))
                .imageQuery(getStringEnv("JAEGER_QUERY_IMAGE", "jaegertracing/jaeger-query:latest"))
                .imageElasticsearch(getStringEnv("ES_IMAGE", "registry.centos.org/rhsyseng/elasticsearch:5.5.2"))
                .esImageInSecure(getBooleanEnv("ES_IMAGE_INSECURE", "false"))
                .imagePerformanceTest(
                        getStringEnv("PERFORMANCE_TEST_IMAGE", "jkandasa/jaeger-performance-test:latest"))
                .build();
    }

    // general data
    private Integer testDuration; // in seconds
    private Integer tracersCount;
    private Boolean runningOnOpenshift;
    private String logsDirectory;

    private Integer spansCount;
    // HTTP GET details
    private Integer queryLimit;
    private Integer querySamples;

    private Integer queryInterval; // in seconds, -1 means, run only at the end

    // sender details
    private String sender; // http, udp
    // database details
    private String storageType; // elasticsearch, cassandra
    private String spansCountFrom; // storage, jaeger-query
    private String storageHost;
    private Integer storagePort;

    private String storageKeyspace;
    // Jaeger Query details
    private String jaegerQueryHost;
    private Integer jaegerQueryPort;

    // Jaeger collector details
    private String jaegerCollectorHost;
    private Integer jaegerCollectorPort;

    // Jaeger agent details
    private String jaegerAgentHost;
    private Integer jaegerAgentPort;

    private Integer jaegerFlushInterval;
    private Integer jaegerMaxPocketSize;
    private Float jaegerSamplingRate;

    private Integer jaegerMaxQueueSize;
    // collector pod details
    private Integer collectorPods;
    private Integer collectorQueueSize;

    private Integer collectorWorkersCount;

    // query pod config
    private String queryStaticFiles;
    // Elasticsearch configurations
    private String esMemory;
    private Integer esBulkSize;
    private Integer esBulkWorkers;
    private String esBulkFlushInterval;

    private Boolean esImageInSecure;
    // images
    private String imageAgent;
    private String imageCollector;
    private String imageQuery;

    private String imageElasticsearch;

    private String imagePerformanceTest;

    private String jaegerClientVersion;

}
