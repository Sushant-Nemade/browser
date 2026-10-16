package com.realmstitchers.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket and STOMP message broker configuration for the Realm Stitchers platform.
 * <p>
 * This configuration establishes the real-time communication pipeline used for
 * WebRTC SDP handshake signaling, ICE candidate exchanges, and authoritative
 * synchronized ballistic cover damage across geographically distributed arena nodes.
 * </p>
 *
 * <ul>
 *   <li>Endpoint: <code>/ws/stitch-signaling</code> with CORS allowed for all origins.</li>
 *   <li>Destination Prefix: <code>/app</code> for client-to-server mapped message routes.</li>
 *   <li>Message Broker: <code>/topic</code> for multi-client pub/sub broadcasting.</li>
 * </ul>
 *
 * @author Realm Stitchers Engineering Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the in-memory simple message broker and destination prefixes.
     *
     * @param config the broker registry used to register destinations
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Simple in-memory broker routing outbound messages back to subscribed clients
        config.enableSimpleBroker("/topic");

        // Application destination prefix for incoming messages bound for @MessageMapping handlers
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP protocol endpoints mapping the WebSocket handshake URL.
     *
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Primary raw WebSocket STOMP endpoint with open CORS for cross-realm WebRTC clients
        registry.addEndpoint("/ws/stitch-signaling")
                .setAllowedOrigins("*");

        // Fallback endpoint with SockJS emulation for environments restricting raw WebSockets
        registry.addEndpoint("/ws/stitch-signaling")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
