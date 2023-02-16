package git.dimitrikvirik.userapi.repository;

import git.dimitrikvirik.userapi.model.redis.EmailCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface EmailCodeRepository extends CrudRepository<EmailCode, String> {

	void deleteByEmail(String email);

	Optional<EmailCode> findByEmail(String email);
}
