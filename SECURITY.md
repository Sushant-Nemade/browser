# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.0.x   | ✅ Yes    |

## Reporting a Vulnerability

If you discover a security vulnerability in Realm Stitchers, please **do not** open a public GitHub issue.

Instead, please email the maintainer directly:
- **Email**: Open a private security advisory via [GitHub Security Advisories](https://github.com/Sushant-Nemade/browser/security/advisories)

### What to Include
- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

### Response Timeline
- **Acknowledgment**: Within 48 hours
- **Initial Assessment**: Within 1 week
- **Fix Release**: As soon as practical

## Security Best Practices for Deployment

### WebRTC
- The game uses STUN servers for NAT traversal. In production, configure your own STUN/TURN servers
- SDP tokens are exchanged as Base64 — never share them on public channels

### Spring Boot
- Enable CSRF protection for non-WebSocket endpoints
- Configure proper CORS origins (replace `*` with your domain)
- Use HTTPS in production

### HAProxy
- Enable TLS termination at the edge
- Configure rate limiting to prevent DDoS
- Restrict admin stats access
