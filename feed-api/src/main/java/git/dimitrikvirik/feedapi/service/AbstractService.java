package git.dimitrikvirik.feedapi.service;

import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractService<T, R extends ReactiveElasticsearchRepository<T, String>> {


	protected final R repository;

	protected final String indexName;


	public AbstractService(R repository, String indexName) {
		this.repository = repository;
		this.indexName = indexName;
	}

	public Mono<T> save(T entity) {
		return repository.save(entity);
	}

	public Mono<T> getById(String id) {
		return repository.findById(id).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, indexName + " not found")));
	}

	public Flux<T> findAllByIds(Iterable<String> ids) {
		return repository.findAllById(ids);
	}
	public Flux<T> findAllByIds(Flux<String> ids) {
		return repository.findAllById(ids);
	}

	public Mono<Void> delete(T entity) {
		return repository.delete(entity);
	}


}
