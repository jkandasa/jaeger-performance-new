package io.jaegertracing.tests;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

import io.jaegertracing.tests.report.model.JaegerTestReport;
import io.jaegertracing.tests.model.TestConfig;

@Slf4j
public class PerformanceReportFactory {
    private static TestConfig _CONFIG;
    private static JaegerTestReport _REPORT;

    public static TestConfig config() {
        if (_CONFIG == null) {
            _CONFIG = TestConfig.loadFromEnvironment();
        }
        return _CONFIG;
    }

    public static JaegerTestReport report() {
        if (_REPORT == null) {
            // parse report from log file
            String[] filter = new String[] { "log" };
            List<File> files = (List<File>) FileUtils.listFiles(new File(config().getLogsDirectory()), filter, true);
            for (File _file : files) {
                if (_file.getName().contains("jaeger-performance-test-job")) {
                    try {
                        logger.info("Report fount, location: {}", _file.getCanonicalPath());
                        String content = FileUtils.readFileToString(_file, "UTF-8");
                        int beginIndex = content.indexOf("@@START@@");
                        int endIndex = content.indexOf("@@END@@");
                        if (beginIndex != -1 && endIndex != -1) {
                            beginIndex += 9;
                            logger.debug("File content:{}", content.substring(beginIndex, endIndex));
                            _REPORT = (JaegerTestReport) JsonUtils.loadFromString(
                                    content.substring(beginIndex, endIndex),
                                    JaegerTestReport.class);
                            // dump this json
                            JsonUtils.dumps(_REPORT, _file.getParent(), _file.getName().replace("log", "json"));
                            break;
                        }
                    } catch (IOException ex) {
                        logger.error("Exception,", ex);
                    }
                }
            }
        }
        return _REPORT;
    }

    public static void main(String[] args) {
        report();
    }
}
