package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
@Testcontainers
class FeedPostControllerTest {


	@Container
	private static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.5.3");

	@DynamicPropertySource
	static void configureElasticsearchContainer(DynamicPropertyRegistry registry) {
		registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
	}


	@Autowired
	private WebTestClient webTestClient;

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
