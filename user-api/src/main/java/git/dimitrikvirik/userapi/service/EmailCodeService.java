package git.dimitrikvirik.userapi.service;


import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import git.dimitrikvirik.userapi.model.redis.EmailCode;
import git.dimitrikvirik.userapi.repository.EmailCodeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailCodeService {

	private final EmailCodeRepository emailCodeRepository;

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String mailFrom;

	public void sendMail(String email, String text) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			helper.setFrom(mailFrom);
			helper.setTo(email);
			helper.setSubject("Social Net Email validation");
			helper.setText(text);
			javaMailSender.send(mimeMessage);

		} catch (MessagingException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Email not send");
		}
	}

	public void create(String email, EmailValidationRequest.TypeEnum type) {
		delete(email);
		EmailCode emailCode = new EmailCode();
		emailCode.setEmail(email);
		emailCode.setType(EmailType.valueOf(type.name()));
		String code = String.valueOf((int) (Math.random() * 10000));
		emailCode.setCode(code);
		sendMail(email, "Your code is " + code);
		emailCodeRepository.save(emailCode);
	}

	public void delete(String email) {
		emailCodeRepository.deleteById(email);
	}

	public Optional<EmailCode> getByEmail(String email) {
		return emailCodeRepository.findById(email);
	}

	public void check(String email, String code, EmailType type) {
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
