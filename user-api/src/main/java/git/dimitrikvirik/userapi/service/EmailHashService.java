package git.dimitrikvirik.userapi.service;

import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import git.dimitrikvirik.userapi.model.redis.EmailHash;
import git.dimitrikvirik.userapi.repository.EmailHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EmailHashService {

	private final EmailHashRepository emailHashRepository;

	public void delete(String id) {
		emailHashRepository.deleteById(id);
	}

	public EmailHash getById(String id) {
		EmailHash emailHash = emailHashRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not validated")
		);
		delete(id);
		return emailHash;
	}

	public EmailHash save(String email, EmailType type) {
		EmailHash emailHash = new EmailHash();
		emailHash.setEmail(email);
		emailHash.setType(type);
		return emailHashRepository.save(emailHash);
	}

}
