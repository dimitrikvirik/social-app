package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import git.dimitrikvirik.feedapi.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

	private final TopicRepository topicRepository;

	public Disposable save(FeedTopic feedTopic) {
		return topicRepository.save(feedTopic).subscribe();
	}

	public Disposable deleteById(String id) {
		return topicRepository.deleteById(id).subscribe();
	}


	public Mono<FeedTopic> findById(String id) {
		return topicRepository.findById(id);
	}

	public Flux<FeedTopic> findAll() {
		return topicRepository.findAll();
	}

	public Flux<FeedTopic> findAllByIds(List<String> id){
		return topicRepository.findAllById(id);
	}


}
