package git.dimitrikvirik.feedapi.facade;

import git.dimitrikvirik.feedapi.mapper.PostMapper;
import git.dimitrikvirik.feedapi.mapper.TopicMapper;
import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.service.TopicService;
import git.dimitrikvirik.generated.feedapi.model.TopicRequest;
import git.dimitrikvirik.generated.feedapi.model.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicFacade {

	private final TopicService topicService;

	public Mono<ResponseEntity<TopicResponse>> createTopic(Mono<TopicRequest> topicRequest) {
		return topicRequest.flatMap(topic -> {
			FeedTopic feedTopic = FeedTopic.builder()
					.id(UUID.randomUUID().toString())
					.name(topic.getName())
					.build();
			return topicService.save(feedTopic);
		}).map(TopicMapper::toTopicResponseEntityCreated);

	}

	public Mono<ResponseEntity<Void>> deleteTopic(String id) {
		return topicService.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")))
				.flatMap(topicService::delete).map(TopicMapper::toTopicResponseEntityNoContent);
	}

	public Mono<ResponseEntity<Flux<TopicResponse>>> getAllTopics(Integer page, Integer size, String searchText) {
		return Mono.just(ResponseEntity.ok(topicService.findAll(page, size, searchText).map(TopicMapper::toTopicResponse)));
	}

	public Mono<ResponseEntity<TopicResponse>> getTopicById(String id) {
		return topicService.findById(id).map(TopicMapper::toTopicResponseEntityOK);
	}

	public Mono<ResponseEntity<TopicResponse>> updateTopic(String id, Mono<TopicRequest> topicRequest) {
		return topicRequest.zipWith(
				topicService.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")))
		).map(tuple -> {
			TopicRequest topicRequest1 = tuple.getT1();
			FeedTopic feedTopic = tuple.getT2();
			feedTopic.setName(topicRequest1.getName());
			return feedTopic;
		}).flatMap(topicService::save).map(TopicMapper::toTopicResponseEntityOK);
	}
}
