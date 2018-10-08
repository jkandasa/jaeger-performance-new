package io.jaegertracing.tests.smoke.tests;

import static org.junit.Assert.assertEquals;

import static junit.framework.TestCase.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.jaegertracing.tests.model.TestConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleUITest {
    /**
     * A very simple test to see if the Jaeger UI responds
     *
     * @throws IOException if it cannot open the page
     */
    @Test
    public void verifyUIRespondsTest() throws IOException {
        // Turn off HTMLUnit logging as it complains about javascript issues that are not relevant to this test
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

        try (final WebClient webClient = new WebClient()) {
            WebClientOptions webClientOptions = webClient.getOptions();
            webClientOptions.setThrowExceptionOnScriptError(false);
            TestConfig config = TestConfig.get();

            final String uiUrl = "http://" + config.getJaegerQueryHost() + ":"
                    + config.getJaegerQueryPort() + "/search";
            logger.info("Connecting to Jaeger UI at :   " + uiUrl);
            final HtmlPage page = webClient.getPage(uiUrl);
            final String pageAsXml = page.asXml();
            final String pageAsText = page.asText();

            assertEquals("Jaeger UI", page.getTitleText());
            assertEquals("Jaeger UI", pageAsText);
            assertTrue(pageAsXml.contains("jaeger-ui-root"));
        }
    }
}