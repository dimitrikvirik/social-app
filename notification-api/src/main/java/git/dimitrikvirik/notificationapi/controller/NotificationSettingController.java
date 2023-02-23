package git.dimitrikvirik.notificationapi.controller;

import git.dimitrikvirik.notification.api.NotificationSettingsApi;
import git.dimitrikvirik.notification.model.NotificationSettingDTO;
import git.dimitrikvirik.notificationapi.facade.NotificationFacade;
import git.dimitrikvirik.notificationapi.facade.NotificationSettingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationSettingController implements NotificationSettingsApi {

	private final NotificationSettingFacade notificationSettingFacade;


	@Override
	public ResponseEntity<NotificationSettingDTO> getNotificationSettings() {
		return notificationSettingFacade.getNotificationSettings();
	}

	@Override
	public ResponseEntity<NotificationSettingDTO> updateNotificationSettings(NotificationSettingDTO notificationSettingDTO) {
		return notificationSettingFacade.updateNotificationSettings(notificationSettingDTO);
	}
}
