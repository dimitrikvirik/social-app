package git.dimitrikvirik.notificationapi.controller;

import git.dimitrikvirik.notification.api.NotificationApi;
import git.dimitrikvirik.notification.model.NotificationDTO;
import git.dimitrikvirik.notificationapi.facade.NotificationFacade;
import git.dimitrikvirik.notificationapi.mapper.NotificationMapper;
import git.dimitrikvirik.notificationapi.model.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

	private final NotificationFacade notificationFacade;

	@Override
	//TODO page
	public ResponseEntity<List<NotificationDTO>> getNotifications(Integer page, Integer size) {
		return notificationFacade.getNotifications(page, size);
	}

	@Override
	public ResponseEntity<NotificationDTO> markNotificationAsSeen(String id) {
		return notificationFacade.markNotificationAsSeen(id);
	}

	@MessageMapping("/notification")
	@SendToUser
	public NotificationDTO getNotificationsLive(@Payload Notification notification) {
		return NotificationMapper.map(notification);
	}

}
