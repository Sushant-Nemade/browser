# Realm Stitchers — Detailed Architecture Document

## Overview

Realm Stitchers is a cross-reality multiplayer combat game that stitches two physical
real-world locations (a developer desk in Bengaluru, India and a dining table in Austin, TX)
into a single unified 3D combat arena. Players fight across both locations, connected by a
glowing Argon Portal Bridge.

## System Architecture

### Layer 1: Client (Browser)
- **Rendering**: Three.js r128 WebGL with procedural canvas textures
- **Physics**: Custom AABB collision engine with cover detection
- **Networking**: WebRTC RTCPeerConnection + RTCDataChannel for P2P state sync
- **Signaling**: BroadcastChannel (same-origin multi-tab) + manual Base64 SDP (cross-device)
- **Audio**: Web Audio API synthesized sound effects (zero external files)
- **UI**: Tailwind CSS 3.4 with glassmorphism HUD panels

### Layer 2: Signaling Server (Spring Boot)
- **Protocol**: STOMP over WebSocket at `/ws/stitch-signaling`
- **SDP Relay**: `/room/{roomId}/signal` — relays WebRTC SDP Offers, Answers, and ICE Candidates
- **Authoritative Cover**: `/room/{roomId}/cover-damage` — server-verified ballistic damage

### Layer 3: Real2Sim Pipeline (Argon GPU Workers)
- **Endpoint**: POST `/api/v1/argon/scan`
- **Input**: Dual smartphone video frames (India + US)
- **Processing**: Semantic segmentation → Material classification → SE(3) alignment
- **Output**: `StitchedBattlegroundPayload` with prop meshes and portal transform matrix

### Layer 4: Edge Load Balancer (HAProxy)
- **Anycast PoPs**: ap-south-1 (Mumbai) and us-central1 (Iowa)
- **GPU Routing**: `/api/v1/argon/scan` → `argon_real2sim_gpu_pool` (leastconn)
- **Signaling Routing**: `/ws/stitch-signaling` → `spring_webrtc_signaling` (consistent hash)

## Data Flow

### Multiplayer State Sync (25Hz)
```
Local Player → STATE_SYNC packet → RTCDataChannel (UDP, unordered) → Remote Client
```
Packet format:
```json
{
  "type": "STATE_SYNC",
  "x": -10.5,
  "z": 0.0,
  "yaw": 1.57,
  "mode": "SOLDIER",
  "crouching": false,
  "hp": 100
}
```

### Reliable Event Channel
```
Local Player → FIRE_EVENT → RTCDataChannel (ordered) → Remote Client
Local Player → PROP_DAMAGE → RTCDataChannel (ordered) → Remote Client
```

### WebRTC Signaling Handshake
```
Tab 1: createOffer() → BroadcastChannel → Tab 2
Tab 2: setRemoteDescription(offer) → createAnswer() → BroadcastChannel → Tab 1
Tab 1: setRemoteDescription(answer) → ICE complete → P2P DataChannel open
```

## Semantic Prop Classification

| Material Class | Ballistic Behavior | HP | Ricochet | Destructible |
|---|---|---|---|---|
| CERAMIC | Hard Cover | ∞ | Yes (cyan sparks) | No |
| ALUMINUM/GLASS | Hard Cover | ∞ | Yes (cyan sparks) | No |
| STAINLESS STEEL | Hard Cover | ∞ | Yes (cyan sparks) | No |
| CERAMIC/WOOD | Hard Cover | ∞ | Yes (cyan sparks) | No |
| ABS PLASTIC | Soft Cover | 220 | No | Yes |
| CARDBOARD | Destructible Cover | 95-150 | No | Yes |
| PAPER/CARDBOARD | Destructible Cover | 95-150 | No | Yes |

## Deployment Topology

### Development (Single Machine)
```
Browser Tab 1 ←→ BroadcastChannel ←→ Browser Tab 2
```

### Production (Multi-Region)
```
                    ┌─── HAProxy Anycast PoP (ap-south-1) ───┐
India Browser ─────►│ /ws/* → Spring Boot SIG-IN             │
                    │ /api/* → Argon GPU Worker IN            │
                    └─────────────────────────────────────────┘
                              ↕ Cross-PoP Sync
                    ┌─── HAProxy Anycast PoP (us-central1) ──┐
US Browser ────────►│ /ws/* → Spring Boot SIG-US              │
                    │ /api/* → Argon GPU Worker US             │
                    └─────────────────────────────────────────┘
```

## Technology Stack

| Component | Technology | Version |
|---|---|---|
| 3D Engine | Three.js | r128 |
| CSS Framework | Tailwind CSS | 3.4.17 |
| Font | Inter + JetBrains Mono | Latest |
| Backend | Spring Boot | 3.3.5 |
| Language | Java | 21 |
| Build | Maven | 3.9+ |
| Load Balancer | HAProxy | 2.9 |
| Container | Docker | Multi-stage |
| CI/CD | GitHub Actions | Latest |
| P2P Protocol | WebRTC | Native |
| Audio | Web Audio API | Native |
