package git.dimitrikvirik.notificationapi.service;

import git.dimitrikvirik.notificationapi.model.domain.Notification;
import git.dimitrikvirik.notificationapi.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository repository;


	public Notification save(Notification notification) {
		return repository.save(notification);
	}

	public Notification findById(String id) {
		return repository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found")
		);
	}

	public List<Notification> getAll(int page, int size, String s) {
		return repository.findAll(PageRequest.of(page, size)).getContent();
	}
}
