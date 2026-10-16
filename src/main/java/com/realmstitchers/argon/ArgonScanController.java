package com.realmstitchers.argon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller exposing endpoints for the Argon Real2Sim computer vision pipeline.
 * <p>
 * Handles multi-view volumetric scan uploads from client edge nodes, orchestrates
 * the spatial synthesis of the stitched battleground arena, and provides health monitoring
 * for container orchestrators and edge load balancers like HAProxy.
 * </p>
 *
 * @author Realm Stitchers Engineering Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/argon")
public class ArgonScanController {

    private static final Logger log = LoggerFactory.getLogger(ArgonScanController.class);

    private final ArgonReal2SimService argonReal2SimService;

    /**
     * Constructs the controller with the injected Argon Real2Sim synthesis service.
     *
     * @param argonReal2SimService the spatial synthesis service
     */
    public ArgonScanController(ArgonReal2SimService argonReal2SimService) {
        this.argonReal2SimService = argonReal2SimService;
    }

    /**
     * Ingests volumetric RGB-D scan data captured from two remote physical setups,
     * performs Real2Sim mesh extraction, calculates the portal bridge alignment,
     * and returns the authoritative stitched battleground payload.
     *
     * @param indiaScan binary scan frames from the Indian desk setup
     * @param usScan    binary scan frames from the US dining table setup
     * @param roomId    optional room identifier; if absent, a UUID is generated
     * @return the synthesized battleground payload or an appropriate error response
     */
    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadAndSynthesizeScan(
            @RequestParam("indiaScan") MultipartFile indiaScan,
            @RequestParam("usScan") MultipartFile usScan,
            @RequestParam(value = "roomId", required = false) String roomId
    ) {
        String effectiveRoomId = (roomId != null && !roomId.isBlank())
                ? roomId
                : UUID.randomUUID().toString();

        log.info("Received scan upload request for room [{}]: indiaScan size={}, usScan size={}",
                effectiveRoomId,
                indiaScan != null ? indiaScan.getSize() : 0,
                usScan != null ? usScan.getSize() : 0);

        if (indiaScan == null || indiaScan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "indiaScan file parameter is missing or empty"));
        }

        if (usScan == null || usScan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "usScan file parameter is missing or empty"));
        }

        try {
            byte[] indiaBytes = indiaScan.getBytes();
            byte[] usBytes = usScan.getBytes();

            ArgonReal2SimService.StitchedBattlegroundPayload payload =
                    argonReal2SimService.synthesizeStitchedArena(effectiveRoomId, indiaBytes, usBytes);

            return ResponseEntity.ok(payload);
        } catch (IOException e) {
            log.error("Failed to read uploaded scan files for room [{}]: {}", effectiveRoomId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process scan payloads: " + e.getMessage()));
        }
    }

    /**
     * Liveness and readiness health probe endpoint utilized by HAProxy, Kubernetes,
     * and Docker healthchecks.
     *
     * @return a JSON object reporting system health status, timestamp, and engine details
     */
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "argon-real2sim-engine",
                "version", "2.4.0-NeuralMesh",
                "timestamp", Instant.now().toString(),
                "nodeStatus", "HEALTHY",
                "subsystems", Map.of(
                        "pointCloudSegmenter", "OPERATIONAL",
                        "se3Solver", "OPERATIONAL",
                        "stompSignalingBroker", "OPERATIONAL"
                )
        ));
    }
}
