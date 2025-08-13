# 9. Network Security

## Overview

Network Security involves protecting networks, devices, and data from unauthorized access, attacks, and damage. It encompasses various technologies, processes, and policies designed to ensure confidentiality, integrity, and availability of network resources.

## Security Principles (CIA Triad)

### 1. Confidentiality
- **Definition**: Information accessible only to authorized parties
- **Mechanisms**: Encryption, access controls, authentication
- **Threats**: Eavesdropping, data theft, unauthorized disclosure

### 2. Integrity
- **Definition**: Information remains accurate and unmodified
- **Mechanisms**: Digital signatures, checksums, hash functions
- **Threats**: Data tampering, unauthorized modification

### 3. Availability
- **Definition**: Information and services accessible when needed
- **Mechanisms**: Redundancy, backup systems, DDoS protection
- **Threats**: DoS attacks, system failures, natural disasters

## Cryptography Basics

### Symmetric Encryption
- **Key**: Same key for encryption and decryption
- **Speed**: Fast encryption/decryption
- **Key Distribution**: Challenging for large networks
- **Algorithms**: AES, DES, 3DES, Blowfish

#### AES (Advanced Encryption Standard)
- **Key Sizes**: 128, 192, 256 bits
- **Block Size**: 128 bits
- **Rounds**: 10, 12, 14 (based on key size)
- **Usage**: Most widely used symmetric algorithm

### Asymmetric Encryption (Public Key)
- **Keys**: Public key (encryption) and private key (decryption)
- **Speed**: Slower than symmetric encryption
- **Key Distribution**: Easier, public keys can be shared
- **Algorithms**: RSA, ECC, Diffie-Hellman

#### RSA Algorithm
- **Key Generation**: Based on large prime numbers
- **Key Sizes**: 1024, 2048, 4096 bits
- **Usage**: Digital signatures, key exchange
- **Security**: Based on factoring large numbers

### Hash Functions
- **Purpose**: Create fixed-size digest from variable input
- **Properties**: One-way, deterministic, avalanche effect
- **Algorithms**: SHA-256, SHA-3, MD5 (deprecated)
- **Uses**: Data integrity, password storage, digital signatures

#### SHA-256
- **Output**: 256-bit hash
- **Input**: Any size
- **Security**: Cryptographically secure
- **Usage**: Bitcoin, SSL certificates

### Digital Signatures
- **Purpose**: Authenticate sender and ensure integrity
- **Process**: 
  1. Hash the message
  2. Encrypt hash with private key
  3. Attach signature to message
  4. Receiver verifies with public key

## Authentication Methods

### 1. Something You Know (Knowledge)
- **Examples**: Passwords, PINs, security questions
- **Vulnerabilities**: Brute force, dictionary attacks, social engineering

### 2. Something You Have (Possession)
- **Examples**: Smart cards, tokens, mobile phones
- **Vulnerabilities**: Theft, loss, cloning

### 3. Something You Are (Inherence)
- **Examples**: Fingerprints, iris scans, voice recognition
- **Vulnerabilities**: Spoofing, false positives/negatives

### Multi-Factor Authentication (MFA)
- **Definition**: Combination of two or more authentication factors
- **Benefits**: Enhanced security, reduced risk
- **Examples**: Password + SMS code, biometric + smart card

## Network Security Devices

### Firewalls

#### Types of Firewalls

**1. Packet Filtering Firewall**
- **Operation**: Examines packet headers
- **Decisions**: Based on IP addresses, ports, protocols
- **Advantages**: Fast, simple
- **Disadvantages**: No application awareness

**2. Stateful Inspection Firewall**
- **Operation**: Tracks connection state
- **Features**: Connection table, return traffic validation
- **Advantages**: Better security than packet filtering
- **Disadvantages**: More resource intensive

**3. Application Layer Firewall (Proxy)**
- **Operation**: Examines application data
- **Features**: Deep packet inspection, content filtering
- **Advantages**: Highest security level
- **Disadvantages**: Slower performance

**4. Next-Generation Firewall (NGFW)**
- **Features**: 
  - Application awareness
  - Intrusion prevention
  - User identity integration
  - Advanced threat protection

#### Firewall Rules
```
Rule 1: ALLOW TCP 192.168.1.0/24 ANY 80 (HTTP)
Rule 2: ALLOW TCP 192.168.1.0/24 ANY 443 (HTTPS)
Rule 3: DENY TCP ANY ANY 23 (Telnet)
Rule 4: ALLOW ICMP 192.168.1.0/24 ANY (Ping)
Rule 5: DENY ALL ANY ANY (Default deny)
```

### Intrusion Detection Systems (IDS)

#### Types
**1. Network-based IDS (NIDS)**
- **Monitoring**: Network traffic
- **Placement**: Network segments, DMZ
- **Detection**: Signature-based, anomaly-based

**2. Host-based IDS (HIDS)**
- **Monitoring**: Individual host activities
- **Data Sources**: System logs, file changes
- **Detection**: Behavior analysis, integrity checking

#### Detection Methods
**1. Signature-based Detection**
- **Method**: Pattern matching against known attacks
- **Advantages**: Low false positives, fast detection
- **Disadvantages**: Cannot detect new attacks

**2. Anomaly-based Detection**
- **Method**: Statistical analysis of normal behavior
- **Advantages**: Can detect new attacks
- **Disadvantages**: High false positives

### Intrusion Prevention Systems (IPS)
- **Function**: Active blocking of detected threats
- **Placement**: Inline with network traffic
- **Actions**: Block, drop, reset connections
- **Integration**: Often combined with firewalls

## VPN (Virtual Private Network)

### Purpose
- **Secure Communication**: Over public networks
- **Remote Access**: Connect remote users to corporate network
- **Site-to-Site**: Connect multiple office locations
- **Privacy**: Hide user activity from ISPs

### VPN Types

#### 1. Remote Access VPN
- **Users**: Individual remote workers
- **Connection**: Client software to VPN gateway
- **Protocols**: PPTP, L2TP/IPSec, SSL/TLS

#### 2. Site-to-Site VPN
- **Connection**: Between network gateways
- **Types**: Intranet (same organization), Extranet (different organizations)
- **Protocols**: IPSec, MPLS

### VPN Protocols

#### IPSec (Internet Protocol Security)
- **Components**: 
  - **AH (Authentication Header)**: Authentication and integrity
  - **ESP (Encapsulating Security Payload)**: Encryption and authentication
- **Modes**:
  - **Transport Mode**: Protects payload only
  - **Tunnel Mode**: Protects entire IP packet

#### SSL/TLS VPN
- **Advantages**: No client software, web browser access
- **Disadvantages**: Limited application support
- **Usage**: Web-based applications, email

#### L2TP/IPSec
- **L2TP**: Layer 2 Tunneling Protocol
- **IPSec**: Provides encryption and authentication
- **Usage**: Windows built-in VPN client

## Common Network Attacks

### 1. Denial of Service (DoS)
- **Purpose**: Make service unavailable
- **Methods**: Resource exhaustion, protocol exploitation
- **Examples**: Ping flood, SYN flood, UDP flood

### 2. Distributed Denial of Service (DDoS)
- **Method**: Multiple sources attack single target
- **Amplification**: DNS, NTP reflection attacks
- **Mitigation**: Rate limiting, traffic filtering, CDN

### 3. Man-in-the-Middle (MITM)
- **Method**: Intercept communication between parties
- **Techniques**: ARP spoofing, DNS spoofing, rogue access points
- **Prevention**: Encryption, certificate validation

### 4. Packet Sniffing
- **Method**: Capture and analyze network traffic
- **Tools**: Wireshark, tcpdump
- **Prevention**: Encryption, switched networks

### 5. Password Attacks
- **Brute Force**: Try all possible combinations
- **Dictionary**: Use common passwords
- **Rainbow Tables**: Precomputed hash lookups
- **Prevention**: Strong passwords, account lockout, MFA

### 6. Social Engineering
- **Method**: Manipulate people to reveal information
- **Types**: Phishing, pretexting, baiting
- **Prevention**: Security awareness training

### 7. Malware
- **Types**: Viruses, worms, trojans, ransomware
- **Propagation**: Email, downloads, removable media
- **Prevention**: Antivirus, firewalls, user education

## Wireless Security

### WiFi Security Protocols

#### WEP (Wired Equivalent Privacy)
- **Encryption**: RC4 with 40/104-bit keys
- **Authentication**: Shared key
- **Vulnerabilities**: Weak encryption, key reuse
- **Status**: Deprecated, easily broken

#### WPA (WiFi Protected Access)
- **Encryption**: TKIP (Temporal Key Integrity Protocol)
- **Authentication**: PSK or 802.1X
- **Improvements**: Dynamic keys, stronger authentication
- **Vulnerabilities**: TKIP weaknesses

#### WPA2
- **Encryption**: AES-CCMP
- **Authentication**: PSK or 802.1X
- **Security**: Strong encryption and authentication
- **Vulnerabilities**: KRACK attack (patched)

#### WPA3
- **Encryption**: AES-256
- **Authentication**: SAE (Simultaneous Authentication of Equals)
- **Features**: Forward secrecy, protection against offline attacks
- **Status**: Latest standard

### Wireless Attack Methods
- **War Driving**: Search for wireless networks
- **Evil Twin**: Rogue access point
- **WPS Attack**: Exploit WiFi Protected Setup
- **Deauthentication**: Force client disconnection

## Network Security Best Practices

### 1. Defense in Depth
- **Principle**: Multiple layers of security
- **Components**: Firewalls, IDS/IPS, antivirus, access controls
- **Benefit**: If one layer fails, others provide protection

### 2. Principle of Least Privilege
- **Definition**: Minimum access necessary for job function
- **Implementation**: Role-based access control (RBAC)
- **Review**: Regular access reviews and updates

### 3. Network Segmentation
- **Purpose**: Isolate network segments
- **Methods**: VLANs, subnets, firewalls
- **Benefits**: Contain breaches, reduce attack surface

### 4. Regular Updates
- **Components**: Operating systems, applications, firmware
- **Process**: Patch management, vulnerability scanning
- **Testing**: Validate patches before deployment

### 5. Monitoring and Logging
- **Components**: SIEM, log analysis, network monitoring
- **Purpose**: Detect and respond to incidents
- **Retention**: Maintain logs for forensic analysis

## Interview Questions

### Basic Level

**Q1: What is the CIA triad in network security?**
**Answer**:
- **Confidentiality**: Information accessible only to authorized parties
- **Integrity**: Information remains accurate and unmodified
- **Availability**: Information and services accessible when needed

These three principles form the foundation of information security.

**Q2: What is the difference between symmetric and asymmetric encryption?**
**Answer**:
| Feature | Symmetric | Asymmetric |
|---------|-----------|------------|
| Keys | Same key for encrypt/decrypt | Public/private key pair |
| Speed | Fast | Slower |
| Key Distribution | Difficult | Easier |
| Usage | Bulk data encryption | Key exchange, digital signatures |
| Examples | AES, DES | RSA, ECC |

**Q3: What are the different types of firewalls?**
**Answer**:
1. **Packet Filtering**: Examines packet headers (IP, port, protocol)
2. **Stateful Inspection**: Tracks connection state and context
3. **Application Layer**: Deep packet inspection, application awareness
4. **Next-Generation**: Advanced features like IPS, application control

### Intermediate Level

**Q4: How does a VPN work and what are its benefits?**
**Answer**:
**How VPN Works**:
1. Client establishes encrypted tunnel to VPN server
2. All traffic routed through tunnel
3. VPN server forwards traffic to destination
4. Return traffic follows same encrypted path

**Benefits**:
- **Security**: Encrypted communication over public networks
- **Privacy**: Hide user activity from ISPs
- **Remote Access**: Secure connection to corporate resources
- **Bypass Restrictions**: Access geo-blocked content

**Q5: What is the difference between IDS and IPS?**
**Answer**:
| Feature | IDS | IPS |
|---------|-----|-----|
| Function | Detection and alerting | Detection and prevention |
| Deployment | Out-of-band (passive) | Inline (active) |
| Response | Notification only | Block/drop traffic |
| Performance Impact | Minimal | Can introduce latency |
| False Positives | Alerts only | Can block legitimate traffic |

**Q6: Explain how digital signatures work.**
**Answer**:
**Process**:
1. **Sender**: Creates hash of message
2. **Sender**: Encrypts hash with private key (signature)
3. **Sender**: Sends message + signature
4. **Receiver**: Decrypts signature with sender's public key
5. **Receiver**: Creates hash of received message
6. **Receiver**: Compares hashes to verify integrity and authenticity

**Benefits**: Authentication, integrity, non-repudiation

### Advanced Level

**Q7: How does a DDoS attack work and how can it be mitigated?**
**Answer**:
**DDoS Attack Process**:
1. **Botnet Creation**: Compromise multiple devices
2. **Command and Control**: Coordinate attack from compromised devices
3. **Attack Launch**: Simultaneous requests from multiple sources
4. **Resource Exhaustion**: Target server overwhelmed

**Mitigation Strategies**:
- **Rate Limiting**: Limit requests per IP/user
- **Traffic Filtering**: Block malicious traffic patterns
- **CDN**: Distribute load across multiple servers
- **DDoS Protection Services**: Specialized filtering services
- **Overprovisioning**: Maintain excess capacity

**Q8: What is Perfect Forward Secrecy and why is it important?**
**Answer**:
**Perfect Forward Secrecy (PFS)**: Each session uses unique encryption keys that are not derived from long-term keys.

**How it Works**:
- Generate ephemeral (temporary) keys for each session
- Keys deleted after session ends
- Compromise of long-term keys doesn't affect past sessions

**Importance**:
- **Protection**: Past communications remain secure even if long-term keys compromised
- **Compliance**: Required by many security standards
- **Implementation**: Diffie-Hellman key exchange, ECDHE

**Q9: Explain the KRACK attack against WPA2.**
**Answer**:
**KRACK (Key Reinstallation Attack)**:
- **Vulnerability**: Flaw in WPA2 4-way handshake
- **Method**: Force reinstallation of encryption key
- **Impact**: Decrypt WiFi traffic, inject malicious packets
- **Affected**: All WPA2 implementations

**Attack Process**:
1. Attacker blocks final handshake message
2. Access point retransmits message
3. Client reinstalls same encryption key
4. Key reuse allows traffic decryption

**Mitigation**: Software patches, WPA3 adoption

**Q10: What are the security considerations for IoT devices?**
**Answer**:
**IoT Security Challenges**:
- **Weak Authentication**: Default/weak passwords
- **Lack of Updates**: No patch management
- **Insecure Communication**: Unencrypted data transmission
- **Physical Access**: Devices in unsecured locations

**Security Measures**:
- **Network Segmentation**: Isolate IoT devices
- **Strong Authentication**: Change default credentials
- **Encryption**: Secure communication protocols
- **Regular Updates**: Firmware and software patches
- **Monitoring**: Network traffic analysis
- **Access Control**: Limit device permissions

## Emerging Security Threats

### Advanced Persistent Threats (APT)
- **Characteristics**: Long-term, stealthy attacks
- **Targets**: High-value organizations
- **Methods**: Multiple attack vectors, social engineering

### Zero-Day Exploits
- **Definition**: Attacks using unknown vulnerabilities
- **Detection**: Behavior-based analysis, sandboxing
- **Prevention**: Defense in depth, rapid patching

### AI-Powered Attacks
- **Capabilities**: Automated vulnerability discovery
- **Social Engineering**: Deepfakes, voice cloning
- **Defense**: AI-powered security tools

## Key Takeaways

- Network security requires multiple layers of protection
- Encryption is fundamental for data protection
- Authentication and access control prevent unauthorized access
- Firewalls and IPS provide network perimeter security
- VPNs enable secure remote access
- Regular updates and monitoring are essential
- Security awareness training reduces human vulnerabilities
- Emerging threats require adaptive security strategies