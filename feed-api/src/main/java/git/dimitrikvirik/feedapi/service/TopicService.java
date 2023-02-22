package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.repository.TopicRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

	private final TopicRepository topicRepository;

	private final ReactiveElasticsearchOperations operations;

	public Mono<FeedTopic> save(FeedTopic feedTopic) {
		return topicRepository.save(feedTopic);
	}

	public Mono<Void> deleteById(String id) {
		return topicRepository.deleteById(id);
	}


	public Mono<FeedTopic> findById(String id) {
		return topicRepository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")));
	}

	public Flux<FeedTopic> findAll(Integer page, Integer size, String searchText) {
		return ElasticsearchBuilder.create(
				FeedTopic.class,
				PageRequest.of(page, size),
				List.of("name"),
				searchText,
				operations
		).doSearch();
	}

	public Flux<FeedTopic> findAllByIds(List<String> id) {
		return topicRepository.findAllById(id).switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")));
	}


	public Mono<Void> delete(FeedTopic feedTopic) {
		return topicRepository.delete(feedTopic);
	}
}
