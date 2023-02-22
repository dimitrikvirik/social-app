package git.dimitrikvirik.feedapi.service;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractService<T, R extends ReactiveElasticsearchRepository<T, String>> {


	protected final R repository;


	public AbstractService(R repository) {
		this.repository = repository;
	}

	public Mono<T> save(T entity) {
		return repository.save(entity);
	}

	public Mono<T> getById(String id) {
		return repository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")));
	}

	public Flux<T> findAllByIds(Iterable<String> ids) {
		return repository.findAllById(ids);
	}


	public Mono<Void> delete(T entity) {
		return repository.delete(entity);
	}


}
