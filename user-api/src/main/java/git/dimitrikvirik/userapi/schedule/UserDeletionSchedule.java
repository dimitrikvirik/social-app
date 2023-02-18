package git.dimitrikvirik.userapi.schedule;

import git.dimitrikvirik.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeletionSchedule {

	private final UserService userService;

	@Scheduled(cron = "0 0 0 * * *")
	public void deleteWaitingUsers() {
		userService.deleteWaitingUsers();
	}

}
