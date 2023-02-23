package git.dimitrikvirik.notificationapi.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.notification.model.NotificationDTO;
import git.dimitrikvirik.notificationapi.mapper.NotificationMapper;
import git.dimitrikvirik.notificationapi.model.domain.Notification;
import git.dimitrikvirik.notificationapi.model.domain.NotificationSetting;
import git.dimitrikvirik.notificationapi.model.enums.NotificationType;
import git.dimitrikvirik.notificationapi.model.kafka.NotificationKafka;
import git.dimitrikvirik.notificationapi.service.NotificationService;
import git.dimitrikvirik.notificationapi.service.NotificationSettingService;
import git.dimitrikvirik.notificationapi.util.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationFacade {

	private final NotificationService notificationService;

	private final NotificationSettingService notificationSettingService;

	private final ObjectMapper objectMapper;

	private final SimpMessagingTemplate simpMessagingTemplate;

	@KafkaListener(topics = "notification", groupId = "notification-api")
	public void kafkaListener(ConsumerRecord<String, String> record) throws JsonProcessingException {
		NotificationKafka value = objectMapper.readValue(record.value(), NotificationKafka.class);
		NotificationSetting settings = notificationSettingService.findByUserId(value.getReceiverUserId());

		NotificationType type = NotificationType.valueOf(value.getType());
		//Dont save notification if user has disabled it
		if ((type == NotificationType.COMMENT && !settings.getCommentEnabled()) || (type == NotificationType.REACTION && !settings.getReactionEnabled())) {
			return;
		}

		Notification notification = Notification.builder()
				.id(UUID.randomUUID().toString())
				.senderUserId(value.getSenderUserId())
				.receiverUserId(value.getReceiverUserId())
				.sourceResourceId(value.getSourceResourceId())
				.type(type)
				.seen(false)
				.createdAt(LocalDateTime.now())
				.build();
		simpMessagingTemplate.convertAndSendToUser(value.getReceiverUserId(),"topic/notification", NotificationMapper.map(notification));
		notificationService.save(notification);
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
