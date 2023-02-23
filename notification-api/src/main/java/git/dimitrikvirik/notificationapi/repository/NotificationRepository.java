package git.dimitrikvirik.notificationapi.repository;

import git.dimitrikvirik.notificationapi.model.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

	List<Notification> findAllByReceiverUserId(String receiverUserId);
}
