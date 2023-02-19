package git.dimitrikvirik.feedapi.service;

import git.dimitrikvirik.feedapi.model.domain.FeedPost;
import git.dimitrikvirik.feedapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;


	public Mono<FeedPost> save(FeedPost post) {
		return postRepository.save(post);
	}

	public Mono<FeedPost> getById(String id) {
		return postRepository.findById(id);
	}

	public Disposable deleteById(String id) {
		return postRepository.deleteById(id).subscribe();
	}

	public Flux<FeedPost> getAll() {
		return postRepository.findAll();
	}

}
