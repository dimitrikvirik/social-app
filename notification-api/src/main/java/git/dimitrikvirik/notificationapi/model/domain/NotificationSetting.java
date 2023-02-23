package git.dimitrikvirik.notificationapi.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_setting")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSetting {


	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;


	@Column(name = "user_id")
	private String userId;

	@Column(name = "reaction_enabled")
	private Boolean reactionEnabled;

	@Column(name = "comment_enabled")
	private Boolean commentEnabled;


}
