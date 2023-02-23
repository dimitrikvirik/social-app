package git.dimitrikvirik.notificationapi.facade;

import git.dimitrikvirik.notification.model.NotificationSettingDTO;
import git.dimitrikvirik.notificationapi.mapper.NotificationMapper;
import git.dimitrikvirik.notificationapi.model.domain.NotificationSetting;
import git.dimitrikvirik.notificationapi.service.NotificationSettingService;
import git.dimitrikvirik.notificationapi.util.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSettingFacade {

	private final NotificationSettingService notificationSettingService;

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
