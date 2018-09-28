package io.jaegertracing.tests.model;

/**
 * @author Pavol Loffay
 */
public class Span {

    private String operationName;
    private String spanID;
    private String traceID;

    public String getOperationName() {
        return operationName;
    }

    public String getSpanID() {
        return spanID;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public void setSpanID(String spanID) {
        this.spanID = spanID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }
}
