package git.dimitrikvirik.notificationapi.model.domain;

import git.dimitrikvirik.notificationapi.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "sender_user_id")
	private String senderUserId;

	@Column(name = "receiver_user_id")
	private String receiverUserId;

	@Column(name = "source_resource_id")
	private String sourceResourceId;

	@Column(name = "seen")
	private Boolean seen;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@CreationTimestamp
	private LocalDateTime createdAt;


}
