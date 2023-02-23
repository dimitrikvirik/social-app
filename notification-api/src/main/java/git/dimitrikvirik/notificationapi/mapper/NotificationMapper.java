package git.dimitrikvirik.notificationapi.mapper;

import git.dimitrikvirik.notification.model.NotificationDTO;
import git.dimitrikvirik.notification.model.NotificationSettingDTO;
import git.dimitrikvirik.notificationapi.model.domain.Notification;
import git.dimitrikvirik.notificationapi.model.domain.NotificationSetting;

public class NotificationMapper {

	public static NotificationDTO map(Notification notification) {
		return NotificationDTO.builder()
				.createdAt(notification.getCreatedAt().toString())
				.seen(notification.getSeen())
				.senderUserId(notification.getSenderUserId())
				.sourceResourceId(notification.getSourceResourceId())
				.type(NotificationDTO.TypeEnum.valueOf(notification.getType().name()))
				.build();
	}

	public static NotificationSettingDTO map(NotificationSetting notificationSetting) {
		return NotificationSettingDTO.builder()
				.commentEnabled(notificationSetting.getCommentEnabled())
				.reactionEnabled(notificationSetting.getReactionEnabled())
				.build();
	}
}
