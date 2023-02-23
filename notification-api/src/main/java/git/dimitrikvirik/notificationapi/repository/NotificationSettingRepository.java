package git.dimitrikvirik.notificationapi.repository;

import git.dimitrikvirik.notificationapi.model.domain.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, String> {

	Optional<NotificationSetting> findByUserId(String userId);
}
