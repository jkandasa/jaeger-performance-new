package io.jaegertracing.tests;

import static io.jaegertracing.tests.ElasticsearchSpanCounter.getSpanIndex;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Pavol Loffay
 */
@Slf4j
public class ElasticsearchStatsGetter implements Closeable {

    private static final String INDEX_STATS = "/_stats?pretty";
    private static final String NODES = "/_cat/nodes?v&h=hm,hc,rm,rc,iiti,iito,sqti,sqto";
    private static final String THREAD_POOL = "/_cat/thread_pool/generic?v&h=id,name,active,rejected,completed";

    private final RestClient restClient;
    private final String spanIndex;

    public ElasticsearchStatsGetter(String host, int port) {
        this.restClient = ElasticsearchSpanCounter.getESRestClient(host, port);
        this.spanIndex = getSpanIndex();

    }

    @Override
    public void close() throws IOException {
        restClient.close();
    }

    public void printStats() {
        try {
            Response indexStats = restClient.performRequest("GET", "/" + spanIndex + INDEX_STATS);
            // https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cat-nodes.html
            Response nodeCat = restClient.performRequest("GET", NODES);
            // https://www.elastic.co/guide/en/elasticsearch/reference/6.3/cat-thread-pool.html
            Response threadPool = restClient.performRequest("GET", THREAD_POOL);
            logger.debug("{} --> {}",
                    nodeCat.getRequestLine().getUri(), EntityUtils.toString(nodeCat.getEntity()));
            logger.debug("{} --> {}",
                    threadPool.getRequestLine().getUri(), EntityUtils.toString(threadPool.getEntity()));
            logger.debug("{} --> {}",
                    indexStats.getRequestLine().getUri(), EntityUtils.toString(indexStats.getEntity()));
        } catch (IOException ex) {
            logger.error("Exception,", ex);
        }
    }
}
