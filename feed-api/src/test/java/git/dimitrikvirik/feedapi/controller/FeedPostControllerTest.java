package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.config.ElasticsearchTestContainer;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
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
		ElasticsearchTestContainer.getContainer().start();
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
