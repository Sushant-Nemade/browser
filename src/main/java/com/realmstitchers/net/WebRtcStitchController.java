package com.realmstitchers.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Real-time STOMP WebSocket controller governing WebRTC signaling exchanges
 * and authoritative ballistic cover damage across stitched physical realms.
 * <p>
 * This controller serves two core synchronization duties:
 * <ol>
 *   <li><b>WebRTC SDP / ICE Signaling:</b> Relays session description protocol offers,
 *       answers, and ICE candidate negotiation with nanosecond server timestamps for
 *       peer synchronization.</li>
 *   <li><b>Authoritative Ballistic Cover Physics:</b> Enforces server-side damage resolution
 *       over spatial props (e.g. ceramic/metal ricochets versus destructible cardboard/plastic cover)
 *       to prevent client-side desync and cheating.</li>
 * </ol>
 * </p>
 *
 * @author Realm Stitchers Engineering Team
 * @version 1.0.0
 */
@Controller
public class WebRtcStitchController {

    private static final Logger log = LoggerFactory.getLogger(WebRtcStitchController.class);

    /**
     * Authoritative in-memory state tracking cover health per room:
     * RoomId -> (PropId -> CoverState).
     */
    private final ConcurrentMap<String, ConcurrentMap<String, CoverState>> roomCoverHpState = new ConcurrentHashMap<>();

    /**
     * Inbound WebRTC signaling message received from a participating edge client.
     *
     * @param senderId          unique ID of the sending client
     * @param recipientId       target client ID or "*" for room broadcast
     * @param type              SDP message type: "offer", "answer", "candidate", or "ping"
     * @param sdp               session description string (if SDP message)
     * @param candidate         candidate dictionary / ICE candidate payload (if candidate)
     * @param clientTimestampNs client local timestamp in nanoseconds
     */
    public record WebRtcSignalMessage(
            String senderId,
            String recipientId,
            String type,
            String sdp,
            Object candidate,
            long clientTimestampNs
    ) {}

    /**
     * Outbound WebRTC signaling message relayed to peers, stamped with the server timestamp.
     *
     * @param senderId          originating peer ID
     * @param recipientId       target peer ID or "*" for broadcast
     * @param type              signal type ("offer", "answer", "candidate")
     * @param sdp               session description string
     * @param candidate         ICE candidate payload
     * @param clientTimestampNs original client timestamp
     * @param serverTimestampNs high-precision server time in nanoseconds
     */
    public record WebRtcSignalRelay(
            String senderId,
            String recipientId,
            String type,
            String sdp,
            Object candidate,
            long clientTimestampNs,
            long serverTimestampNs
    ) {}

    /**
     * Inbound ballistic damage impact event reported by a shooting client.
     *
     * @param shooterId     identifier of the player discharging the weapon
     * @param propId        spatial ID of the prop taking the ballistic impact
     * @param damageAmount  nominal kinetic damage requested
     * @param materialClass material classification reported by client raycast
     * @param hitPosition   world coordinates [X, Y, Z] of impact
     */
    public record CoverDamageRequest(
            String shooterId,
            String propId,
            int damageAmount,
            String materialClass,
            double[] hitPosition
    ) {}

    /**
     * Authoritative server-verified cover damage update broadcast to all room participants.
     *
     * @param roomId            session room identifier
     * @param shooterId         player initiating ballistic damage
     * @param propId            prop mesh identifier
     * @param materialClass     material classification
     * @param hardCover         true if bullet ricocheted off impenetrable cover
     * @param remainingHp       authoritative remaining structural hit points
     * @param maxHp             initial maximum hit points
     * @param destroyed         whether the prop has collapsed
     * @param ricochetOccurred  true if kinetic energy deflected without causing damage
     * @param soundCue          client audio cue to trigger (ricochet vs penetration vs collapse)
     * @param serverTimestampNs authoritative timestamp in nanoseconds
     */
    public record CoverSyncEvent(
            String roomId,
            String shooterId,
            String propId,
            String materialClass,
            boolean hardCover,
            int remainingHp,
            int maxHp,
            boolean destroyed,
            boolean ricochetOccurred,
            String soundCue,
            long serverTimestampNs
    ) {}

    /**
     * Internal mutable state container for tactical cover objects.
     */
    public static class CoverState {
        private final String propId;
        private final String materialClass;
        private final boolean hardCover;
        private final int maxHp;
        private int currentHp;
        private boolean destroyed;

        public CoverState(String propId, String materialClass, boolean hardCover, int maxHp) {
            this.propId = propId;
            this.materialClass = materialClass;
            this.hardCover = hardCover;
            this.maxHp = maxHp;
            this.currentHp = maxHp;
            this.destroyed = false;
        }

        public synchronized DamageResult applyDamage(int damage) {
            if (hardCover) {
                // Hard cover (ceramic, glass, metal) causes ricochets and suffers zero structural damage
                return new DamageResult(0, 0, false, true, "metal_ceramic_ricochet_ping");
            }

            if (destroyed) {
                // Already destroyed prop
                return new DamageResult(0, maxHp, true, false, "debris_pass_through");
            }

            this.currentHp = Math.max(0, this.currentHp - damage);
            if (this.currentHp == 0) {
                this.destroyed = true;
                return new DamageResult(0, maxHp, true, false, "structure_collapse_shatter");
            }

            return new DamageResult(this.currentHp, maxHp, false, false, "cardboard_penetration_thud");
        }

        public String getPropId() { return propId; }
        public String getMaterialClass() { return materialClass; }
        public boolean isHardCover() { return hardCover; }
        public int getMaxHp() { return maxHp; }
        public synchronized int getCurrentHp() { return currentHp; }
        public synchronized boolean isDestroyed() { return destroyed; }
    }

    public record DamageResult(
            int remainingHp,
            int maxHp,
            boolean destroyed,
            boolean ricochet,
            String soundCue
    ) {}

    /**
     * Relays low-latency WebRTC SDP signaling offers, answers, and ICE candidates
     * between participants in the specified room, attaching a high-precision nanosecond
     * server timestamp for distributed network clock calibration.
     *
     * @param roomId  the destination room identifier
     * @param message incoming WebRTC signaling payload
     * @return stamped signaling relay message broadcast to topic subscribers
     */
    @MessageMapping("/room/{roomId}/signal")
    @SendTo("/topic/room/{roomId}/signal")
    public WebRtcSignalRelay relaySignal(
            @DestinationVariable String roomId,
            WebRtcSignalMessage message
    ) {
        long serverTimestampNs = System.nanoTime();

        log.debug("Relaying WebRTC [{}] signal in room [{}] from sender [{}] to recipient [{}] at serverTimeNs={}",
                message.type(), roomId, message.senderId(), message.recipientId(), serverTimestampNs);

        return new WebRtcSignalRelay(
                message.senderId(),
                message.recipientId(),
                message.type(),
                message.sdp(),
                message.candidate(),
                message.clientTimestampNs(),
                serverTimestampNs
        );
    }

    /**
     * Authoritative handler for weapon impacts and ballistic cover degradation.
     * <p>
     * Differentiates between hard cover (e.g., Ceramic Mugs, Aluminum/Glass Displays)
     * which trigger ricochets and deflect bullets, and soft/destructible cover
     * (e.g., Cardboard Cereal Boxes, ABS Plastic Keyboards, Milk Cartons) which track
     * HP reduction and collapse when hit points reach zero.
     * </p>
     *
     * @param roomId  the active battleground room identifier
     * @param request ballistic impact parameters from the shooting client
     * @return authoritative cover state broadcast to all room subscribers
     */
    @MessageMapping("/room/{roomId}/cover-damage")
    @SendTo("/topic/room/{roomId}/cover-sync")
    public CoverSyncEvent handleCoverDamage(
            @DestinationVariable String roomId,
            CoverDamageRequest request
    ) {
        long serverTimestampNs = System.nanoTime();

        ConcurrentMap<String, CoverState> roomProps = roomCoverHpState.computeIfAbsent(
                roomId,
                k -> new ConcurrentHashMap<>()
        );

        CoverState propState = roomProps.computeIfAbsent(
                request.propId(),
                k -> initializeDefaultCoverState(request.propId(), request.materialClass())
        );

        DamageResult result = propState.applyDamage(request.damageAmount());

        log.info("Ballistic damage resolved for room [{}] prop [{}]: material={}, dmg={}, remainingHp={}, ricochet={}, destroyed={}",
                roomId, request.propId(), propState.getMaterialClass(), request.damageAmount(),
                result.remainingHp(), result.ricochet(), result.destroyed());

        return new CoverSyncEvent(
                roomId,
                request.shooterId(),
                propState.getPropId(),
                propState.getMaterialClass(),
                propState.isHardCover(),
                result.remainingHp(),
                result.maxHp(),
                result.destroyed(),
                result.ricochet(),
                result.soundCue(),
                serverTimestampNs
        );
    }

    /**
     * Initializes authoritative default structural properties and material classification
     * based on known tactical prop IDs and materials.
     *
     * @param propId        spatial prop identifier
     * @param materialClass material classification string
     * @return initialized CoverState instance
     */
    private CoverState initializeDefaultCoverState(String propId, String materialClass) {
        String normalizedMat = (materialClass != null) ? materialClass.toUpperCase() : "UNKNOWN";

        return switch (normalizedMat) {
            case "CERAMIC", "ALUMINUM_GLASS", "CERAMIC_WOOD", "METAL", "CONCRETE" ->
                    new CoverState(propId, normalizedMat, true, 0);
            case "CARDBOARD" ->
                    new CoverState(propId, normalizedMat, false, 150);
            case "WAXED_CARDBOARD" ->
                    new CoverState(propId, normalizedMat, false, 120);
            case "ABS_PLASTIC", "PLASTIC" ->
                    new CoverState(propId, normalizedMat, false, 240);
            default ->
                    new CoverState(propId, normalizedMat, false, 100);
        };
    }

    /**
     * Inspection helper for integration testing and administrative monitoring.
     *
     * @param roomId active room ID
     * @return view of the room's cover HP registry
     */
    public Map<String, CoverState> getRoomCoverState(String roomId) {
        return roomCoverHpState.getOrDefault(roomId, new ConcurrentHashMap<>());
    }
}
