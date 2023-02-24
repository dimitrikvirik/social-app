package git.dimitrikvirik.notificationapi.service;

import git.dimitrikvirik.notificationapi.model.domain.NotificationSetting;
import git.dimitrikvirik.notificationapi.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

	private final NotificationSettingRepository repository;

	public NotificationSetting save(NotificationSetting notificationSetting) {
		return repository.save(notificationSetting);
	}

	public NotificationSetting findByUserId(String id) {
		return repository.findByUserId(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification setting not found")
		);
	}

	public void saveIfNotExist(NotificationSetting notificationSetting) {
		repository.findById(notificationSetting.getUserId()).orElseGet(() -> repository.save(notificationSetting));
	}

}
