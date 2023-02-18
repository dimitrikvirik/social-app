package git.dimitrikvirik.userapi.model.redis;

import git.dimitrikvirik.userapi.model.EmailValidationRequest;
import git.dimitrikvirik.userapi.model.enums.EmailType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "email_hash", timeToLive = 3600)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailHash {
	@Id
	private String id;
	private String email;
	private EmailType type;
}
