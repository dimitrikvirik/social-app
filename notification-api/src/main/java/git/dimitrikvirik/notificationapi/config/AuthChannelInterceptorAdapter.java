package git.dimitrikvirik.notificationapi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {


	private final KeycloakWebSocketAuthManager authenticationManager;

	@Override
	public Message<?> preSend(@NotNull final Message<?> message, final MessageChannel channel) {
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT == accessor.getCommand()) {
			String bearerToken = accessor.getFirstNativeHeader("Authorization");

			if (bearerToken != null) {
				bearerToken = bearerToken.replace("Bearer ", "");
				log.debug("Received bearer token {}", bearerToken);
				JWSAuthenticationToken token = (JWSAuthenticationToken) authenticationManager
						.authenticate(new JWSAuthenticationToken(bearerToken));
				accessor.setUser(token);

			}
		}
		return message;
	}

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		ChannelInterceptor.super.postSend(message, channel, sent);
	}
}
