package git.dimitrikvirik.userapi.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@GeneratedValue
	@Id
	private String id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_pref_id", referencedColumnName = "id")
	private UserPref userPref;

	@Column(name = "keycloak_id")
	private String keycloakId;

	@Column(name = "profile")
	private String profile;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "email")
	private String email;

	@Column(name = "is_disabled")
	private Boolean isDisabled = false;

	@Column(name = "is_blocked")
	private Boolean isBlocked = false;


}
