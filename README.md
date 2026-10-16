# 🎮 Realm Stitchers — Cross-Reality Multiplayer Combat

[![CI/CD Pipeline](https://github.com/Sushant-Nemade/browser/actions/workflows/ci.yml/badge.svg)](https://github.com/Sushant-Nemade/browser/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-cyan.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3-green.svg)](https://spring.io/projects/spring-boot)
[![Three.js r128](https://img.shields.io/badge/Three.js-r128-blue.svg)](https://threejs.org/)
[![WebRTC](https://img.shields.io/badge/WebRTC-P2P-purple.svg)](https://webrtc.org/)

> **Fight across two real-world locations stitched into one combat arena.** A Bengaluru developer desk and an Austin dining table are connected via a glowing Argon Portal Bridge — with real semantic cover, destructible cardboard props, hybrid soldier/RC car combat, and live two-client WebRTC multiplayer.

---

## ✨ What Is This?

Realm Stitchers is an open-source **cross-reality multiplayer combat game** that simulates two physical spaces (a desk and a dining table) connected by a portal bridge into a single 3D battleground. Built with **Three.js** for 3D rendering, **WebRTC** for peer-to-peer multiplayer, and a **Spring Boot** backend for production deployment.

### 🌍 The Two Realms

| 🇮🇳 India Desk (Bengaluru) | 🇺🇸 US Table (Austin) |
|---|---|
| Ceramic Coffee Mug (Hard Cover) | Cereal Box (Destructible Cardboard) |
| Mechanical Keyboard (Destructible) | Ceramic Fruit Bowl (Hard Cover) |
| 4K Coding Monitor (Hard Cover) | Milk Carton (Destructible Cardboard) |
| Tech Book Stack (Destructible) | Chrome Toaster (Hard Cover) |
| Gaming Mouse (Destructible) | Napkin Stack (Destructible) |

**Connected by:** The Argon Portal Bridge — a glowing SE(3)-aligned corridor linking the two desk edges.

---

## 🎯 Features

### 🏠 Stitched 3D Realms & Semantic Cover
- Two full desk environments with 10+ semantic props
- Material-based ballistic classification (Ceramic = Hard Cover, Cardboard = Destructible)
- Press **[T]** to toggle **Argon Vision** — semantic bounding box overlays on every prop
- Press **[R]** to trigger **Dual Scan & Stitch** — full 4-stage arena reconstruction animation

### 🪖 Hybrid Soldier & RC Car Combat
- **WASD / D-Pad**: Move (Soldier) or Drive (RC Car)
- **Mouse Click / [F]**: Aim and fire plasma rounds
- **Right-Click / [G]**: Launch breach grenades (shred cardboard props!)
- **[C]**: Crouch (Soldier) or Drift Brake (RC Car)
- **[Shift]**: Nitro Boost / Sprint
- **[V]**: Morph between Tactical Soldier and RC Combat Car anytime

### 🌐 Live Two-Client WebRTC Multiplayer
- **Auto-Pair**: Open in two browser tabs — automatically pairs via `BroadcastChannel` + `RTCPeerConnection`
- **Cross-Device**: Exchange Base64 SDP Offer/Answer tokens for serverless P2P across devices
- **AI Sparring Bot**: Toggle on/off — fills in as P2 opponent when no peer is connected
- **25Hz State Sync**: Player position, mode, and cover status synced at 25Hz over `RTCDataChannel`

### 🏗️ Production Architecture
- **Java 21 / Spring Boot 3.3** WebSocket signaling server
- **HAProxy** geo-edge load balancer with GPU workload routing
- **Docker** multi-stage build with health checks
- **GitHub Actions** CI/CD pipeline

---

## 🚀 Quick Start

### Option 1: Zero-Install Browser Play (Recommended)
```bash
# Just open the HTML file in any modern browser!
open src/main/resources/static/index.html

# For multiplayer: Open it in TWO tabs — they auto-pair instantly!
```

### Option 2: Spring Boot Server
```bash
# Requires Java 21+ and Maven 3.9+
./mvnw spring-boot:run
# Open http://localhost:8080 in two browser tabs
```

### Option 3: Docker Compose
```bash
cd deploy
docker compose up --build
# Open http://localhost in two browser tabs
```

---

## 🎮 Controls Reference

| Action | Keyboard | Mouse/Touch | Notes |
|--------|----------|-------------|-------|
| Move / Drive | `WASD` or Arrow Keys | On-screen D-Pad | Works in both Soldier and RC Car modes |
| Aim | — | Mouse Move | Raycast to ground plane |
| Fire Plasma | `F` or `Space` | Left Click | Auto-fire while held |
| Breach Grenade | `G` | Right Click | 3.5s cooldown, shreds cardboard |
| Morph Mode | `V` | Mode Button | Toggle Soldier ↔ RC Car |
| Crouch / Drift | `C` | Cover Button | Reduces hitbox, activates cover |
| Nitro / Sprint | `Shift` | Boost Button | Drains energy bar |
| Argon Vision | `T` | Vision Button | Toggle semantic overlays |
| Dual Scan & Stitch | `R` | Stitch Button | Restore all destroyed cover |
| Cycle Camera | `K` | Camera Button | Chase → Overhead → Bridge |
| Close Modal | `Esc` | ✕ Button | Close any open panel |

---

## 🏗️ Architecture

```
┌──────────────┐    BroadcastChannel     ┌──────────────┐
│  Browser Tab  │◄──────────────────────►│  Browser Tab  │
│  (India P1)   │    RTCDataChannel      │  (US P2)      │
│  Three.js     │◄──────────────────────►│  Three.js     │
└──────┬───────┘                         └──────┬───────┘
       │ WebSocket (STOMP)                      │
       └──────────────┬────────────────────────┘
                      │
              ┌───────▼────────┐
              │   HAProxy      │
              │   Edge LB      │
              └───────┬────────┘
                      │
          ┌───────────┼───────────┐
          │                       │
  ┌───────▼────────┐     ┌───────▼────────┐
  │ Spring Boot     │     │ Argon Real2Sim │
  │ Signaling Pod   │     │ GPU Worker Pod │
  └────────────────┘     └────────────────┘
```

---

## 📁 Project Structure

```
realm-stitchers/
├── .github/workflows/ci.yml          # GitHub Actions CI/CD
├── deploy/
│   ├── Dockerfile                     # Multi-stage Java 21 container
│   ├── docker-compose.yml             # Full-stack local deployment
│   └── haproxy.cfg                    # Production load balancer config
├── docs/
│   └── ARCHITECTURE.md                # Detailed architecture document
├── src/
│   ├── main/
│   │   ├── java/com/realmstitchers/
│   │   │   ├── RealmStitchersApplication.java
│   │   │   ├── argon/
│   │   │   │   ├── ArgonReal2SimService.java
│   │   │   │   └── ArgonScanController.java
│   │   │   ├── config/
│   │   │   │   └── WebSocketConfig.java
│   │   │   └── net/
│   │   │       └── WebRtcStitchController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html         # Full game client (self-contained)
│   └── test/
│       └── java/com/realmstitchers/
│           └── ArgonReal2SimServiceTest.java
├── .gitignore
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE                            # MIT License
├── README.md                          # This file
└── pom.xml                            # Maven build configuration
```

---

## 🤝 Contributing

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

**Areas to contribute:**
- 🎮 New weapons, props, and map layouts
- 🌐 TURN server integration for NAT-piercing multiplayer
- 🎨 Enhanced particle effects and post-processing shaders
- 📱 Mobile touch controls optimization
- 🧪 Integration and end-to-end tests
- 📚 Documentation and tutorials

---

## 🧪 Testing

```bash
# Java unit tests
./mvnw test

# Manual testing
# 1. Open index.html in two browser tabs
# 2. Tab 1 = India Desk (P1), Tab 2 auto-switches to US Table (P2)
# 3. Move with WASD, fire with click/F, morph with V
# 4. Shoot the cereal box to destroy it! Shoot the mug for ricochet sparks!
```

---

## 📜 License

[MIT License](LICENSE) — free to use, modify, and distribute.

---

## 🙏 Acknowledgments

- [Three.js](https://threejs.org/) — 3D rendering engine
- [Tailwind CSS](https://tailwindcss.com/) — Utility-first CSS framework
- [WebRTC](https://webrtc.org/) — Peer-to-peer real-time communication
- [Spring Boot](https://spring.io/projects/spring-boot) — Java backend framework
- [HAProxy](https://www.haproxy.org/) — Reliable high-performance load balancer

---

<p align="center">
  <strong>Built with ❤️ by <a href="https://github.com/Sushant-Nemade">Sushant Nemade</a></strong>
</p>
