package git.dimitrikvirik.userapi.repository;

import git.dimitrikvirik.userapi.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);

	Optional<User> findByKeycloakId(String keycloakId);

	void deleteByIsDisabledAndUpdatedAtAfter(Boolean isDisabled, LocalDateTime time);

}
