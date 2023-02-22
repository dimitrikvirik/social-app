package git.dimitrikvirik.feedapi.repository;

import git.dimitrikvirik.feedapi.model.domain.FeedNotification;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface NotificationRepository extends ReactiveElasticsearchRepository<FeedNotification, String> {
}
