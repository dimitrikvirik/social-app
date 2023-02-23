package git.dimitrikvirik.notificationapi.facade;

import git.dimitrikvirik.notification.model.NotificationDTO;
import git.dimitrikvirik.notificationapi.mapper.NotificationMapper;
import git.dimitrikvirik.notificationapi.model.domain.Notification;
import git.dimitrikvirik.notificationapi.service.NotificationService;
import git.dimitrikvirik.notificationapi.util.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

	private final NotificationService notificationService;

	@KafkaListener(topics = "notification", groupId = "notification-api")
	public void kafkaListener() {

	}


	public ResponseEntity<List<NotificationDTO>> getNotifications(Integer page, Integer size) {
		return ResponseEntity.ok(notificationService.getAll(page, size, UserHelper.currentUserId()).stream().map(NotificationMapper::map).toList());
	}

	public ResponseEntity<NotificationDTO> markNotificationAsSeen(String id) {
		Notification notification = notificationService.findById(id);
		String receiverUserId = notification.getReceiverUserId();
		if (!receiverUserId.equals(UserHelper.currentUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this notification");
		}
		notification.setSeen(true);

		return ResponseEntity.ok(NotificationMapper.map(notificationService.save(notification)));
	}
}
