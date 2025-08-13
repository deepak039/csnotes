# 10. Wireless Networks

## Overview

Wireless networks use radio waves, microwaves, or infrared signals to transmit data without physical cables. They provide mobility, flexibility, and easy deployment but face challenges in security, interference, and limited bandwidth.

## Wireless Communication Fundamentals

### Radio Wave Properties
- **Frequency**: Number of cycles per second (Hz)
- **Wavelength**: Distance between wave peaks
- **Amplitude**: Signal strength
- **Phase**: Position in wave cycle

### Frequency Bands
| Band | Frequency | Wavelength | Characteristics |
|------|-----------|------------|-----------------|
| LF | 30-300 kHz | 10-1 km | Long range, low data rate |
| MF | 300 kHz-3 MHz | 1 km-100 m | AM radio |
| HF | 3-30 MHz | 100-10 m | Shortwave radio |
| VHF | 30-300 MHz | 10-1 m | FM radio, TV |
| UHF | 300 MHz-3 GHz | 1 m-10 cm | Cell phones, WiFi |
| SHF | 3-30 GHz | 10-1 cm | Satellite, radar |

### Propagation Characteristics
- **Line of Sight**: Direct path between antennas
- **Reflection**: Signal bounces off surfaces
- **Refraction**: Signal bends through different media
- **Diffraction**: Signal bends around obstacles
- **Scattering**: Signal disperses from small objects

## WiFi (IEEE 802.11)

### WiFi Standards Evolution

| Standard | Year | Frequency | Max Speed | Range | Key Features |
|----------|------|-----------|-----------|-------|--------------|
| 802.11 | 1997 | 2.4 GHz | 2 Mbps | 20m | Original standard |
| 802.11a | 1999 | 5 GHz | 54 Mbps | 35m | OFDM modulation |
| 802.11b | 1999 | 2.4 GHz | 11 Mbps | 35m | DSSS, popular |
| 802.11g | 2003 | 2.4 GHz | 54 Mbps | 38m | OFDM + backward compatible |
| 802.11n | 2009 | 2.4/5 GHz | 600 Mbps | 70m | MIMO, channel bonding |
| 802.11ac | 2013 | 5 GHz | 6.93 Gbps | 35m | MU-MIMO, wider channels |
| 802.11ax (WiFi 6) | 2019 | 2.4/5 GHz | 9.6 Gbps | 30m | OFDMA, improved efficiency |
| 802.11be (WiFi 7) | 2024 | 2.4/5/6 GHz | 46 Gbps | 30m | Multi-link operation |

### WiFi Architecture

#### Basic Service Set (BSS)
- **Infrastructure BSS**: Access Point + stations
- **Independent BSS (IBSS)**: Ad-hoc network, no AP
- **Extended Service Set (ESS)**: Multiple BSSs connected

#### WiFi Components
- **Station (STA)**: WiFi-enabled device
- **Access Point (AP)**: Central connection point
- **Distribution System (DS)**: Backbone network
- **Service Set Identifier (SSID)**: Network name

### WiFi Frame Structure
```
|Frame Control|Duration|Address1|Address2|Address3|Seq Control|Address4|Data|FCS|
|     2       |   2    |   6    |   6    |   6    |     2     |   6    |var | 4 |
```

#### Frame Types
1. **Management Frames**: Network management (beacon, probe, authentication)
2. **Control Frames**: Medium access control (RTS, CTS, ACK)
3. **Data Frames**: User data transmission

### WiFi Security

#### Security Evolution
1. **Open**: No security (deprecated)
2. **WEP**: Wired Equivalent Privacy (broken)
3. **WPA**: WiFi Protected Access (improved)
4. **WPA2**: Strong AES encryption
5. **WPA3**: Latest with enhanced security

#### WPA2 Security
- **Encryption**: AES-CCMP (Counter Mode CBC-MAC Protocol)
- **Authentication**: PSK (Personal) or 802.1X (Enterprise)
- **Key Management**: 4-way handshake
- **Integrity**: Message authentication codes

#### WPA3 Enhancements
- **SAE**: Simultaneous Authentication of Equals
- **Forward Secrecy**: Past sessions remain secure
- **Brute Force Protection**: Offline dictionary attack prevention
- **Enhanced Open**: Opportunistic Wireless Encryption (OWE)

### WiFi 6 (802.11ax) Features

#### OFDMA (Orthogonal Frequency Division Multiple Access)
- **Benefit**: Multiple users share channel simultaneously
- **Efficiency**: Better spectrum utilization
- **Latency**: Reduced waiting time

#### MU-MIMO (Multi-User MIMO)
- **Uplink**: Multiple devices transmit simultaneously
- **Downlink**: AP transmits to multiple devices
- **Capacity**: Increased network throughput

#### Target Wake Time (TWT)
- **Purpose**: Power saving for IoT devices
- **Method**: Schedule transmission times
- **Benefit**: Extended battery life

## Bluetooth

### Bluetooth Overview
- **Frequency**: 2.4 GHz ISM band
- **Range**: 1-100 meters (class dependent)
- **Topology**: Personal Area Network (PAN)
- **Power**: Low power consumption

### Bluetooth Versions
| Version | Year | Range | Speed | Key Features |
|---------|------|-------|-------|--------------|
| 1.0/1.1 | 1999 | 10m | 1 Mbps | Basic functionality |
| 2.0 + EDR | 2004 | 10m | 3 Mbps | Enhanced Data Rate |
| 3.0 + HS | 2009 | 10m | 24 Mbps | High Speed (WiFi) |
| 4.0 (LE) | 2010 | 10m | 1 Mbps | Low Energy |
| 5.0 | 2016 | 50m | 2 Mbps | Improved range/speed |
| 5.2 | 2020 | 50m | 2 Mbps | LE Audio, Isochronous Channels |

### Bluetooth Architecture

#### Protocol Stack
- **Radio Layer**: Physical transmission
- **Baseband**: Medium access, error correction
- **LMP**: Link Manager Protocol
- **L2CAP**: Logical Link Control and Adaptation
- **Applications**: Various profiles (A2DP, HID, etc.)

#### Piconet and Scatternet
- **Piconet**: Master device + up to 7 active slaves
- **Scatternet**: Multiple overlapping piconets
- **Roles**: Master (controls), slave (responds)

### Bluetooth Low Energy (BLE)
- **Purpose**: IoT and wearable devices
- **Power**: Ultra-low power consumption
- **Architecture**: Different from classic Bluetooth
- **Profiles**: GATT (Generic Attribute Profile)

## Cellular Networks

### Cellular Evolution (Generations)

#### 1G (First Generation)
- **Technology**: Analog (AMPS)
- **Service**: Voice only
- **Speed**: N/A (analog)
- **Era**: 1980s

#### 2G (Second Generation)
- **Technology**: Digital (GSM, CDMA)
- **Services**: Voice, SMS
- **Speed**: 64 kbps
- **Era**: 1990s

#### 2.5G/2.75G
- **Technology**: GPRS, EDGE
- **Services**: Basic data services
- **Speed**: 144 kbps - 384 kbps
- **Era**: Early 2000s

#### 3G (Third Generation)
- **Technology**: UMTS, CDMA2000
- **Services**: Voice, data, video calling
- **Speed**: 2 Mbps
- **Era**: 2000s

#### 4G/LTE (Fourth Generation)
- **Technology**: LTE, WiMAX
- **Services**: High-speed data, VoIP
- **Speed**: 100 Mbps - 1 Gbps
- **Era**: 2010s

#### 5G (Fifth Generation)
- **Technology**: New Radio (NR)
- **Services**: Ultra-high speed, IoT, AR/VR
- **Speed**: 10 Gbps+
- **Era**: 2020s

### Cellular Architecture

#### Network Components
- **Mobile Station (MS)**: User device
- **Base Station (BS)**: Cell tower/antenna
- **Mobile Switching Center (MSC)**: Call routing
- **Home Location Register (HLR)**: Subscriber database
- **Visitor Location Register (VLR)**: Temporary subscriber info

#### Cell Structure
- **Macrocell**: Large coverage area (1-30 km)
- **Microcell**: Medium coverage (100m-1km)
- **Picocell**: Small coverage (10-100m)
- **Femtocell**: Very small coverage (<10m)

### 5G Technology

#### Key Features
- **Enhanced Mobile Broadband (eMBB)**: High-speed data
- **Ultra-Reliable Low Latency (URLLC)**: Critical applications
- **Massive Machine Type Communications (mMTC)**: IoT devices

#### Technical Improvements
- **Millimeter Wave**: Higher frequency bands (24-100 GHz)
- **Massive MIMO**: Large antenna arrays
- **Network Slicing**: Virtual network instances
- **Edge Computing**: Processing closer to users

## Satellite Communication

### Satellite Types by Orbit

#### Geostationary Earth Orbit (GEO)
- **Altitude**: 35,786 km
- **Period**: 24 hours (matches Earth rotation)
- **Coverage**: Large area (1/3 of Earth)
- **Latency**: High (~250ms)
- **Use**: TV broadcasting, weather

#### Medium Earth Orbit (MEO)
- **Altitude**: 2,000-35,786 km
- **Period**: 2-24 hours
- **Coverage**: Regional
- **Latency**: Medium (~50-150ms)
- **Use**: GPS, navigation

#### Low Earth Orbit (LEO)
- **Altitude**: 160-2,000 km
- **Period**: 90-120 minutes
- **Coverage**: Small area, requires constellation
- **Latency**: Low (~1-40ms)
- **Use**: Internet (Starlink), imaging

### Satellite Internet
- **Providers**: Starlink, OneWeb, Amazon Kuiper
- **Technology**: LEO constellations
- **Benefits**: Global coverage, low latency
- **Challenges**: Weather interference, cost

## Wireless Security Challenges

### Common Vulnerabilities
1. **Eavesdropping**: Intercepting wireless signals
2. **Man-in-the-Middle**: Rogue access points
3. **Denial of Service**: Jamming, deauthentication
4. **Weak Authentication**: Default passwords, WPS
5. **Rogue Devices**: Unauthorized access points

### Security Best Practices
1. **Strong Encryption**: WPA3, enterprise authentication
2. **Network Segmentation**: Separate guest networks
3. **Regular Updates**: Firmware and security patches
4. **Monitoring**: Wireless intrusion detection
5. **Physical Security**: Secure device placement

## Interview Questions

### Basic Level

**Q1: What are the main differences between WiFi standards?**
**Answer**:
| Standard | Frequency | Max Speed | Key Feature |
|----------|-----------|-----------|-------------|
| 802.11b | 2.4 GHz | 11 Mbps | First popular standard |
| 802.11g | 2.4 GHz | 54 Mbps | Backward compatible with b |
| 802.11n | 2.4/5 GHz | 600 Mbps | MIMO technology |
| 802.11ac | 5 GHz | 6.93 Gbps | MU-MIMO, wider channels |
| 802.11ax (WiFi 6) | 2.4/5 GHz | 9.6 Gbps | OFDMA, better efficiency |

**Q2: What is the difference between 2.4 GHz and 5 GHz WiFi bands?**
**Answer**:
| Feature | 2.4 GHz | 5 GHz |
|---------|---------|-------|
| Range | Longer | Shorter |
| Penetration | Better through walls | Limited |
| Interference | More crowded | Less crowded |
| Speed | Lower | Higher |
| Channels | 3 non-overlapping | 23 non-overlapping |
| Devices | More compatible | Newer devices |

**Q3: What are the different cellular network generations?**
**Answer**:
- **1G**: Analog voice (1980s)
- **2G**: Digital voice + SMS (1990s)
- **3G**: Data services, video calling (2000s)
- **4G/LTE**: High-speed data, mobile broadband (2010s)
- **5G**: Ultra-high speed, IoT, low latency (2020s)

### Intermediate Level

**Q4: How does WiFi authentication work in WPA2?**
**Answer**:
**4-Way Handshake Process**:
1. **Message 1**: AP sends ANonce (random number)
2. **Message 2**: Client sends SNonce + MIC
3. **Message 3**: AP sends GTK (Group Temporal Key)
4. **Message 4**: Client confirms GTK receipt

**Key Derivation**:
- **PMK**: Pairwise Master Key (from password/802.1X)
- **PTK**: Pairwise Transient Key (derived from PMK + nonces)
- **GTK**: Group Temporal Key (for multicast/broadcast)

**Q5: What is MIMO and how does it improve wireless performance?**
**Answer**:
**MIMO (Multiple Input Multiple Output)**:
- **Concept**: Multiple antennas at transmitter and receiver
- **Benefits**:
  - **Spatial Diversity**: Reduces fading effects
  - **Spatial Multiplexing**: Multiple data streams
  - **Beamforming**: Directional signal transmission

**Types**:
- **SU-MIMO**: Single user, multiple streams
- **MU-MIMO**: Multiple users simultaneously
- **Massive MIMO**: Large antenna arrays (5G)

**Q6: How does Bluetooth device discovery and pairing work?**
**Answer**:
**Discovery Process**:
1. **Inquiry**: Device scans for other Bluetooth devices
2. **Inquiry Response**: Discoverable devices respond
3. **Name Request**: Get friendly device names

**Pairing Process**:
1. **Authentication**: Exchange security keys
2. **Authorization**: Grant access permissions
3. **Key Exchange**: Establish encryption keys
4. **Connection**: Establish data link

### Advanced Level

**Q7: Explain how WiFi 6 (802.11ax) improves network efficiency.**
**Answer**:
**Key Improvements**:

1. **OFDMA (Orthogonal Frequency Division Multiple Access)**:
   - Divides channel into smaller sub-channels
   - Multiple users transmit simultaneously
   - Reduces latency and improves efficiency

2. **MU-MIMO Enhancements**:
   - Uplink MU-MIMO (multiple devices to AP)
   - Up to 8 spatial streams
   - Better spatial reuse

3. **BSS Coloring**:
   - Identifies overlapping networks
   - Reduces interference between networks
   - Improves spatial reuse

4. **Target Wake Time (TWT)**:
   - Schedules device wake times
   - Reduces power consumption
   - Better for IoT devices

**Q8: What are the security improvements in WPA3?**
**Answer**:
**WPA3 Enhancements**:

1. **SAE (Simultaneous Authentication of Equals)**:
   - Replaces PSK authentication
   - Provides forward secrecy
   - Resistant to offline dictionary attacks

2. **Enhanced Open**:
   - Opportunistic Wireless Encryption (OWE)
   - Encryption without authentication
   - Protects against passive eavesdropping

3. **192-bit Security Suite**:
   - Stronger encryption for enterprise
   - GCMP-256, HMAC-SHA384
   - ECDH and ECDSA with 384-bit curves

4. **Easy Connect**:
   - QR code-based device onboarding
   - Simplified IoT device setup
   - Reduces configuration errors

**Q9: How does 5G achieve ultra-low latency?**
**Answer**:
**5G Low Latency Techniques**:

1. **Edge Computing**:
   - Processing closer to users
   - Reduces round-trip time
   - Local content caching

2. **Network Slicing**:
   - Dedicated virtual networks
   - Optimized for specific applications
   - Guaranteed performance

3. **Shorter Frame Structure**:
   - Mini-slots for urgent data
   - Flexible numerology
   - Faster scheduling

4. **Massive MIMO**:
   - Beamforming reduces interference
   - Better signal quality
   - Faster data transmission

**Target Latencies**:
- **eMBB**: <10ms
- **URLLC**: <1ms
- **mMTC**: Variable

**Q10: What are the challenges and solutions for satellite internet?**
**Answer**:
**Challenges**:
1. **High Latency**: GEO satellites ~250ms delay
2. **Weather Interference**: Rain fade, atmospheric absorption
3. **Limited Bandwidth**: Shared among many users
4. **High Cost**: Expensive infrastructure and equipment
5. **Line of Sight**: Obstacles block signals

**Solutions**:
1. **LEO Constellations**: Lower altitude reduces latency to ~20-40ms
2. **Advanced Modulation**: Higher spectral efficiency
3. **Spot Beams**: Focused coverage areas increase capacity
4. **Adaptive Coding**: Adjust to channel conditions
5. **Ground Station Diversity**: Multiple ground stations
6. **Ka/Ku Band**: Higher frequency bands for more bandwidth

**Modern Examples**: Starlink, OneWeb, Amazon Kuiper

## Emerging Wireless Technologies

### WiFi 7 (802.11be)
- **Multi-Link Operation**: Simultaneous connections on multiple bands
- **320 MHz Channels**: Doubled channel width
- **4K-QAM**: Higher modulation order
- **Enhanced MU-MIMO**: Up to 16 spatial streams

### 6G (Sixth Generation)
- **Timeline**: 2030s deployment
- **Speed**: 100 Gbps - 1 Tbps
- **Latency**: <1ms
- **Applications**: Holographic communications, brain-computer interfaces

### Li-Fi (Light Fidelity)
- **Technology**: Visible light communication
- **Speed**: Potentially faster than WiFi
- **Security**: Light doesn't penetrate walls
- **Limitations**: Requires line of sight

## Key Takeaways

- Wireless networks provide mobility but face security and interference challenges
- WiFi standards continue evolving with better speed, efficiency, and security
- Cellular networks have progressed from voice-only to high-speed data and IoT
- 5G enables new applications with ultra-low latency and high bandwidth
- Bluetooth serves short-range personal area networking needs
- Satellite internet is becoming viable with LEO constellations
- Security is crucial in wireless networks due to broadcast nature
- Understanding wireless fundamentals is essential for modern networking