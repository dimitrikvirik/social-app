package git.dimitrikvirik.feedapi.controller;

import git.dimitrikvirik.feedapi.facade.TopicFacade;
import git.dimitrikvirik.generated.feedapi.api.TopicApi;
import git.dimitrikvirik.generated.feedapi.model.TopicRequest;
import git.dimitrikvirik.generated.feedapi.model.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TopicController implements TopicApi {

	private final TopicFacade topicFacade;

	@Override
	public Mono<ResponseEntity<TopicResponse>> createTopic(Mono<TopicRequest> topicRequest, ServerWebExchange exchange) {
		return topicFacade.createTopic(topicRequest);
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteTopic(String id, ServerWebExchange exchange) {
		return topicFacade.deleteTopic(id);
	}

	@Override
	public Mono<ResponseEntity<Flux<TopicResponse>>> getAllTopics(Integer page, Integer size, String searchText, ServerWebExchange exchange) {
		return topicFacade.getAllTopics(page, size, searchText);
	}

	@Override
	public Mono<ResponseEntity<TopicResponse>> getTopicById(String id, ServerWebExchange exchange) {
		return topicFacade.getTopicById(id);
	}

	@Override
	public Mono<ResponseEntity<TopicResponse>> updateTopic(String id, Mono<TopicRequest> topicRequest, ServerWebExchange exchange) {
		return topicFacade.updateTopic(id, topicRequest);
	}
}
