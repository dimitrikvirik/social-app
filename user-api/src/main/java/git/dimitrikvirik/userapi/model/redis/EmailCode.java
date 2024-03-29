package git.dimitrikvirik.userapi.model.redis;


import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "email_code", timeToLive = 120)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailCode {

	@Id
	private String email;
	private String code;

	private EmailType type;

}
