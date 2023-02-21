package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedTopic;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface TopicRepository extends ReactiveElasticsearchRepository<FeedTopic, String> {
}
