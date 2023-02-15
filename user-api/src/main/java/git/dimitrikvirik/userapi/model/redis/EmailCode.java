package git.dimitrikvirik.userapi.model.redis;

import git.dimitrikvirik.user.model.EmailValidationRequest;
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
	private String id;
	private String email;
	private String code;

	private EmailValidationRequest.TypeEnum type;

}
