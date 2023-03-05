package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.config.ElasticsearchTestContainer;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
class FeedPostControllerTest {


	@Autowired
	private WebTestClient webTestClient;

	@BeforeAll
	static void setUp() {
		try (ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.5.3")
				.withPassword("elastic")
		) {
			// Start the container. This step might take some time...
			container.start();

			// Do whatever you want with the rest client ...
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(
					AuthScope.ANY,
					new UsernamePasswordCredentials("elastic", "elastic")
			);

			var client =
					RestClient
							.builder(HttpHost.create(container.getHttpHostAddress()))
							.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
							.build();

			Response response = client.performRequest(new Request("GET", "/_cluster/health"));
			System.out.println(response);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void createPost() {
		PostRequest postRequest = PostRequest.builder()
				.title("My title")
				.content("New content")
				.topics(Collections.emptyList())
				.build();

		webTestClient.post().uri("/post")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(postRequest)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.title").isEqualTo("My title")
				.jsonPath("$.content").isEqualTo("New content");
	}

	@Test
	void deletePost() {
	}

	@Test
	void getAllPosts() {
	}

	@Test
	void getPostById() {
	}

	@Test
	void updatePost() {
	}
}
