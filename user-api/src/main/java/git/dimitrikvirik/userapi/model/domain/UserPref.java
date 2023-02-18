package git.dimitrikvirik.userapi.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_pref")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPref {

	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "comment_notification_enabled")
	private Boolean commentNotificationEnabled = true;

	@Column(name = "like_notification_enabled")
	private Boolean likeNotificationEnabled = true;

}
