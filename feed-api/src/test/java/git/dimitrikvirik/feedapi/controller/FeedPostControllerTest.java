package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.model.domain.FeedUser;
import git.dimitrikvirik.feedapi.repository.TopicRepository;
import git.dimitrikvirik.feedapi.repository.UserRepository;
import git.dimitrikvirik.generated.feedapi.model.PostRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {"spring.config.location=classpath:application.yml"})
@Testcontainers
class FeedPostControllerTest {


    @Container
    private static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.5.3")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("bootstrap.memory_lock", "true")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @DynamicPropertySource
    static void configureElasticsearchContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @MockBean
    public ReactiveJwtDecoder decoder;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private UserRepository userRepository;


    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser(value = "test-user")
    void createPost() {

        Mockito.when(topicRepository.findAllById(List.of("topic-1-id"))).thenReturn(Flux.fromIterable(List.of(FeedTopic.builder()
                .id("topic-1-id")
                .name("The Test Topic").build())));

        Mockito.when(userRepository.findById("test-user")).thenReturn(Mono.just(FeedUser.builder()
                .id("test-user")
                .firstname("Test firstname")
                .lastname("Test lastname")
                .photo("Test Photo")
                .build()));

        PostRequest postRequest = PostRequest.builder()
                .title("My title")
                .content("New content")
                .topics(List.of("topic-1-id"))
                .build();

        webTestClient.post().uri("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(postRequest)
                .exchange()
                .expectStatus().isCreated()
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
