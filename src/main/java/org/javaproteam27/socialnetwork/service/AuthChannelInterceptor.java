package org.javaproteam27.socialnetwork.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private static final String TOKEN_HEADER = "token";
    private final JwtTokenProvider jwtTokenProvider;


    // Processes a message before sending it
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        // Instantiate an object for retrieving the STOMP headers
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // Check that the object is not null
        assert accessor != null;
        // If the frame is a CONNECT frame
        if (accessor.getCommand() == StompCommand.CONNECT) {
            // retrieve the username from the headers
            final String token = accessor.getFirstNativeHeader(TOKEN_HEADER);
            // authenticate the user and if that's successful add their user information to the headers.
            accessor.setUser(jwtTokenProvider.getAuthentication(token));
        }
        return message;
    }

}
