package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.repository.TopicRepository;
import git.dimitrikvirik.feedapi.utils.ElasticsearchBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TopicService extends AbstractService<FeedTopic, TopicRepository> {

	private final ReactiveElasticsearchOperations operations;

	public TopicService(TopicRepository repository, ReactiveElasticsearchOperations operations) {
		super(repository);
		this.operations = operations;
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

}
