package io.jaegertracing.tests.smoke;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.jaegertracing.tests.model.TestConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class SimpleRestClient implements Closeable {

    // Limit for the number of retries when getting traces
    private static final Integer RETRY_LIMIT = 10;

    TestConfig config = TestConfig.get();
    private ObjectMapper jsonObjectMapper = new ObjectMapper();
    private OkHttpClient okClient;

    public SimpleRestClient() {
        okClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * GET all traces for a service: http://localhost:3001/api/traces?service=something
     * GET a Trace by id: http://localhost:3001/api/traces/23652df68bd54e15
     * GET services http://localhost:3001/api/services
     *
     * GET after a specific time: http://localhost:3001/api/traces?service=something&start=1492098196598
     * NOTE: time is in MICROseconds.
     *
     */
    private List<JsonNode> getTraces(String parameters) {
        List<JsonNode> traces = new ArrayList<>();
        String targetUrl = "http://" + config.getJaegerQueryHost() + ":"
                + config.getJaegerQueryPort() + "/api/traces?service="
                + TestBase.SMOKE_TEST_SERVICE_NAME;
        if (parameters != null && !parameters.trim().isEmpty()) {
            targetUrl = targetUrl + "&" + parameters;
        }

        logger.debug("GETTING TRACES: " + targetUrl);
        try {
            Request request = new Request.Builder().url(targetUrl).build();
            Response response = okClient.newCall(request).execute();
            String result = response.body().string();

            JsonNode jsonPayload = jsonObjectMapper.readTree(result);
            JsonNode data = jsonPayload.get("data");
            Iterator<JsonNode> traceIterator = data.iterator();
            while (traceIterator.hasNext()) {
                traces.add(traceIterator.next());
            }
        } catch (IOException ex) {
            // TODO what is the best thing to do here
            throw new RuntimeException(ex);
        }

        return traces;
    }

    /**
     *
     * @param parameters Parameter string to be appended to REST call
     * @param expectedTraceCount expected number of traces
     * @return a List of traces returned from the Jaeger Query API
     */
    public List<JsonNode> getTraces(final String parameters, final int expectedTraceCount) {
        waitForFlush();
        List<JsonNode> traces = new ArrayList<>();
        int iterations = 0;
        long sleepInterval = 1000;

        // Retry for up to RETRY_LIMIT seconds to get the expected number of traces
        // TODO make wait time an argument?
        while (iterations < RETRY_LIMIT && traces.size() < expectedTraceCount) {
            iterations++;
            traces = getTraces(parameters);
            if (traces.size() >= expectedTraceCount) {
                return traces;
            }
            try {
                Thread.sleep(sleepInterval);
                sleepInterval += 100;
            } catch (InterruptedException e) {
                logger.warn("Sleep was interrupted", e);
            }
        }

        return traces;
    }

    /**
     * Return all of the traces created since the start time given.  NOTE: The Jaeger Rest API
     * requires a time in microseconds.
     *
     * @param testStartTime time the test started
     * @return A List of Traces created after the time specified.
     */
    public List<JsonNode> getTracesSinceTestStart(Instant testStartTime, int expectedTraceCount) {
        long startTime = TimeUnit.MILLISECONDS.toMicros(testStartTime.toEpochMilli());
        List<JsonNode> traces = getTraces("start=" + startTime, expectedTraceCount);
        return traces;
    }

    /**
     * Return all of the traces created between the start and end times given.  NOTE: The Jaeger Rest API requires times
     * in microseconds.
     *
     * @param testStartTime start time
     * @param testEndTime end time
     * @return A List of traces created between the times specified.
     */
    public List<JsonNode> getTracesBetween(Instant testStartTime, Instant testEndTime, int expectedTraceCount) {
        long startTime = TimeUnit.MILLISECONDS.toMicros(testStartTime.toEpochMilli());
        long endTime = TimeUnit.MILLISECONDS.toMicros(testEndTime.toEpochMilli());
        String parameters = "start=" + startTime + "&end=" + endTime;
        List<JsonNode> traces = getTraces(parameters, expectedTraceCount);
        return traces;
    }

    /**
     * Make sure spans are flushed before trying to retrieve them
     */
    public void waitForFlush() {
        try {
            Thread.sleep(config.getJaegerFlushInterval());
        } catch (InterruptedException e) {
            logger.warn("Sleep interrupted", e);
        }
    }

    /**
     * Return a formatted JSON String
     * @param json Some json that you want to format
     * @return pretty formatted json
     */
    public String prettyPrintJson(JsonNode json) {
        String pretty = "";
        try {
            ObjectWriter writer = jsonObjectMapper.writerWithDefaultPrettyPrinter();
            pretty = writer.writeValueAsString(json);
        } catch (JsonProcessingException jpe) {
            logger.error("prettyPrintJson Failed", jpe);
        }

        return pretty;
    }

    /**
     * Debugging method
     *
     * @param traces A list of traces to print
     */
    protected void dumpAllTraces(List<JsonNode> traces) {
        logger.info("Got " + traces.size() + " traces");

        for (JsonNode trace : traces) {
            logger.info("------------------ Trace {} ------------------", trace.get("traceID"));
            Iterator<JsonNode> spanIterator = trace.get("spans").iterator();
            while (spanIterator.hasNext()) {
                JsonNode span = spanIterator.next();
                logger.debug(prettyPrintJson(span));  // TODO does this work?
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (okClient != null) {
            okClient.dispatcher().executorService().shutdown();
            okClient.connectionPool().evictAll();
        }
    }
}