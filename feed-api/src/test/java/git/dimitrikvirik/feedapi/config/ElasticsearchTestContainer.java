package git.dimitrikvirik.feedapi.config;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticsearchTestContainer {

	private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:8.5.3";

	private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER =
			  new ElasticsearchContainer(ELASTICSEARCH_IMAGE)
				.withEnv("discovery.type", "single-node")
				.withEnv("xpack.security.enabled", "false")
				.withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
				.withExposedPorts(9220);

	static {
		ELASTICSEARCH_CONTAINER.start();
	}

	public static ElasticsearchContainer getContainer() {
		return ELASTICSEARCH_CONTAINER;
	}

}
