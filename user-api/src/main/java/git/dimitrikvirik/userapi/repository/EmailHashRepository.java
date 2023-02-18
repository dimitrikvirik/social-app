package git.dimitrikvirik.userapi.repository;

import git.dimitrikvirik.userapi.model.redis.EmailHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHashRepository extends CrudRepository<EmailHash, String> {

}
