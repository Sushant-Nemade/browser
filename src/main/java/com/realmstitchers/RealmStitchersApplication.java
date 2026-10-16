package com.realmstitchers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * Entry point for the Realm Stitchers distributed backend service.
 * <p>
 * Realm Stitchers powers spatial real-to-sim arena synthesis, bridging disparate
 * physical environments (such as an office desk in India and a dining table in the US)
 * into a synchronized digital battleground via Argon Real2Sim computer vision pipelines,
 * WebRTC low-latency signaling, and authoritative spatial cover destruction physics.
 * </p>
 *
 * @author Realm Stitchers Engineering Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableWebSocketMessageBroker
public class RealmStitchersApplication {

    /**
     * Bootstraps the Realm Stitchers Spring Boot application runtime.
     *
     * @param args command-line arguments passed during startup
     */
    public static void main(String[] args) {
        SpringApplication.run(RealmStitchersApplication.class, args);
    }
}
