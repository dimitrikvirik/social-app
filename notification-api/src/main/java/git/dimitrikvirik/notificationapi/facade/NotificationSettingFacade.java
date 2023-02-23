package git.dimitrikvirik.notificationapi.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.notification.model.NotificationSettingDTO;
import git.dimitrikvirik.notificationapi.mapper.NotificationMapper;
import git.dimitrikvirik.notificationapi.model.domain.Notification;
import git.dimitrikvirik.notificationapi.model.domain.NotificationSetting;
import git.dimitrikvirik.notificationapi.model.enums.NotificationType;
import git.dimitrikvirik.notificationapi.model.kafka.NotificationKafka;
import git.dimitrikvirik.notificationapi.model.kafka.UserKafka;
import git.dimitrikvirik.notificationapi.service.NotificationSettingService;
import git.dimitrikvirik.notificationapi.util.UserHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingFacade {

	private final NotificationSettingService notificationSettingService;

	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "user", groupId = "notification-api")
	public void kafkaListener(ConsumerRecord<String, String> record) throws JsonProcessingException {
		UserKafka userKafka = objectMapper.readValue(record.value(), UserKafka.class);
		log.info("UserKafka: {}", userKafka);
		NotificationSetting notificationSetting = NotificationSetting.builder()
				.id(UUID.randomUUID().toString())
				.userId(userKafka.getId())
				.commentEnabled(true)
				.reactionEnabled(true)
				.build();
		notificationSettingService.saveIfNotExist(notificationSetting);
	}


	public ResponseEntity<NotificationSettingDTO> getNotificationSettings() {
		NotificationSetting notificationSetting = notificationSettingService.findByUserId(UserHelper.currentUserId());
		return ResponseEntity.ok(NotificationMapper.map(notificationSetting));
	}

	public ResponseEntity<NotificationSettingDTO> updateNotificationSettings(NotificationSettingDTO notificationSettingDTO) {
		NotificationSetting notificationSetting = notificationSettingService.findByUserId(UserHelper.currentUserId());
		notificationSetting.setCommentEnabled(notificationSettingDTO.getCommentEnabled());
		notificationSetting.setReactionEnabled(notificationSettingDTO.getReactionEnabled());
		return ResponseEntity.ok(NotificationMapper.map(notificationSettingService.save(notificationSetting)));

	}
}
