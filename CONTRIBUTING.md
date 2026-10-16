# Contributing to Realm Stitchers

Thank you for your interest in contributing to **Realm Stitchers**! We welcome contributions from the community — whether it's bug fixes, new features, documentation improvements, or gameplay enhancements.

## 🚀 Quick Start

1. **Fork** the repository
2. **Clone** your fork:
   ```bash
   git clone https://github.com/<your-username>/browser.git
   cd browser
   ```
3. **Create a branch** for your feature:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Make your changes** and test them
5. **Commit** with a descriptive message:
   ```bash
   git commit -m "feat: add rocket launcher weapon type"
   ```
6. **Push** and open a **Pull Request**

## 📋 Development Setup

### Frontend Only (Zero Dependencies)
Simply open `src/main/resources/static/index.html` in any modern browser. No build step required.

### Full Stack (Spring Boot + WebSocket Signaling)
```bash
# Requires Java 21+ and Maven 3.9+
./mvnw spring-boot:run
# Open http://localhost:8080 in two browser tabs
```

### Docker
```bash
cd deploy
docker compose up --build
# Open http://localhost in two browser tabs
```

## 🧪 Testing

```bash
# Run Java unit tests
./mvnw test

# Frontend: Open index.html in two browser tabs
# Press [V] to morph, [T] to toggle Argon Vision, [R] to scan/restore
```

## 📝 Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

| Prefix | Description |
|--------|-------------|
| `feat:` | New feature |
| `fix:` | Bug fix |
| `docs:` | Documentation only |
| `style:` | Code style (formatting, no logic change) |
| `refactor:` | Code refactoring |
| `test:` | Adding or fixing tests |
| `chore:` | Build scripts, CI, tooling |
| `perf:` | Performance improvement |

## 🎮 Areas to Contribute

### Gameplay
- New weapon types (sniper, shotgun spread)
- Additional prop types (books, potted plants, glass vases)
- Map layouts beyond desk/dining table
- Power-ups and pickups
- Match timer and round system

### Networking
- TURN relay server integration
- WebSocket signaling server improvements
- Better dead-reckoning and interpolation
- Spectator mode

### Graphics
- Enhanced particle effects
- Post-processing shaders (bloom, SSAO)
- Animated character models
- Destructible environment animations

### Infrastructure
- Kubernetes deployment manifests
- Terraform/Pulumi IaC
- Monitoring and observability (Prometheus, Grafana)

## 🐛 Reporting Issues

Please use [GitHub Issues](https://github.com/Sushant-Nemade/browser/issues) with:
- A clear title and description
- Steps to reproduce
- Expected vs. actual behavior
- Browser and OS information
- Screenshots or screen recordings if applicable

## 📜 Code of Conduct

Be kind, respectful, and constructive. We follow the [Contributor Covenant](https://www.contributor-covenant.org/version/2/1/code_of_conduct/).

## 📄 License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).
