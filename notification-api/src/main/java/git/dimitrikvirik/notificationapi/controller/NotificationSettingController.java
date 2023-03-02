package git.dimitrikvirik.notificationapi.controller;

import git.dimitrikvirik.notification.api.NotificationSettingsApi;
import git.dimitrikvirik.notification.model.NotificationSettingDTO;
import git.dimitrikvirik.notificationapi.facade.NotificationFacade;
import git.dimitrikvirik.notificationapi.facade.NotificationSettingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Date;

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
