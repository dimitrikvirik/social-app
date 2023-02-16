package git.dimitrikvirik.userapi.service;

import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.redis.EmailCode;
import git.dimitrikvirik.userapi.repository.EmailCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

	private final EmailCodeRepository emailCodeRepository;

	public void create(String email, EmailValidationRequest.TypeEnum type) {
		EmailCode emailCode = new EmailCode();
		emailCode.setEmail(email);
		emailCode.setType(type);
		emailCode.setCode(String.valueOf((int) (Math.random() * 10000)));
		emailCodeRepository.save(emailCode);
	}

	public void delete(String email) {
		emailCodeRepository.deleteByEmail(email);
	}

	public Optional<EmailCode> getByEmail(String email) {
		return emailCodeRepository.findByEmail(email);
	}

	public void check(String email, String code, EmailValidationRequest.TypeEnum type) {
		EmailCode emailCode = getByEmail(email).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not send")
		);
		if (!(emailCode.getCode().equals(code)) && emailCode.getType().equals(
				type
		)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email code is not valid");
		}
	}

}
