package io.jaegertracing.tests;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import io.jaegertracing.tests.model.TestConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Pavol Loffay
 */
@Slf4j
public class CassandraSpanCounter extends UntilNoChangeCounter {

    private final Session session;

    public CassandraSpanCounter(TestConfig config) {
        super();
        this.session = getCassandraSession(
                config.getStorageHost(), config.getStoragePort(), config.getStorageKeyspace());
    }

    @Override
    public void close() {
        session.close();
    }

    @Override
    public int count() {
        ResultSet result = session.execute("select * from traces");
        int spansCount = result.all().size();
        logger.info("found {} traces in Cassandra", spansCount);
        return spansCount;
    }

    private Session getCassandraSession(String contactPoint, Integer port, String keyspace) {
        Cluster cluster = Cluster.builder()
                .addContactPoint(contactPoint)
                .withPort(port)
                .build();
        return cluster.connect(keyspace);
    }
}
