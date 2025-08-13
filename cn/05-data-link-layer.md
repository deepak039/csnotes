# 5. Data Link Layer

## Overview

The Data Link Layer is responsible for node-to-node delivery of frames over a single link. It provides error-free transmission of data frames from one node to another over the physical layer.

## Key Functions

1. **Framing**: Dividing bit stream into manageable frames
2. **Physical Addressing**: Using MAC addresses for local delivery
3. **Flow Control**: Managing data transmission rate
4. **Error Control**: Detecting and correcting transmission errors
5. **Access Control**: Managing access to shared medium

## Sub-layers

### 1. Logical Link Control (LLC)
- **IEEE 802.2 Standard**
- **Functions**:
  - Flow control
  - Error control
  - Interface with Network Layer
- **Services**:
  - Unacknowledged connectionless
  - Connection-oriented
  - Acknowledged connectionless

### 2. Media Access Control (MAC)
- **Functions**:
  - Channel access control
  - Physical addressing (MAC addresses)
  - Frame formatting
- **Protocols**: Ethernet, WiFi, Token Ring

## Framing

### Purpose of Framing
- Organize bits into recognizable units
- Provide synchronization
- Enable error detection
- Facilitate flow control

### Framing Methods

#### 1. Character Count
- First field indicates frame length
- **Problem**: Error in count field corrupts subsequent frames

#### 2. Flag Bytes with Byte Stuffing
- Special flag bytes mark frame boundaries
- **Flag**: 01111110 (0x7E)
- **Byte Stuffing**: Escape special characters in data

#### 3. Flag Bits with Bit Stuffing
- **Flag**: 01111110
- **Bit Stuffing**: Insert 0 after five consecutive 1s
- **Used in**: HDLC, PPP

#### 4. Physical Layer Coding Violations
- Use invalid signal patterns as delimiters
- **Used in**: Some LAN protocols

## Error Detection and Correction

### Error Types
1. **Single-bit Error**: One bit flipped
2. **Burst Error**: Multiple consecutive bits affected
3. **Random Error**: Multiple non-consecutive bits affected

### Error Detection Methods

#### 1. Parity Check
**Simple Parity**:
- Add one parity bit
- Even parity: Total 1s = even number
- Odd parity: Total 1s = odd number
- **Limitation**: Cannot detect even number of errors

**Two-dimensional Parity**:
- Parity for rows and columns
- Can detect and correct single-bit errors

#### 2. Checksum
- Sum all data words
- Send complement of sum
- **Used in**: IP, TCP, UDP headers

#### 3. Cyclic Redundancy Check (CRC)
- Most powerful error detection method
- Based on polynomial division
- **Common Polynomials**:
  - CRC-8: x⁸ + x² + x + 1
  - CRC-16: x¹⁶ + x¹⁵ + x² + 1
  - CRC-32: x³² + x²⁶ + x²³ + ... + x + 1

**CRC Process**:
1. Append n zeros to data (n = degree of polynomial)
2. Divide by generator polynomial
3. Replace zeros with remainder
4. Receiver divides received frame by same polynomial
5. Zero remainder = no error

### Error Correction Methods

#### 1. Hamming Code
- Can correct single-bit errors
- **Formula**: 2ʳ ≥ m + r + 1
  - r = parity bits, m = data bits

#### 2. Reed-Solomon Codes
- Can correct burst errors
- Used in CDs, DVDs, QR codes

## Flow Control

### Purpose
- Prevent fast sender from overwhelming slow receiver
- Ensure reliable data delivery
- Optimize network performance

### Flow Control Protocols

#### 1. Stop-and-Wait Protocol
```
Sender                    Receiver
  |                         |
  |-------Frame 0---------->|
  |                         |
  |<------ACK 0-------------|
  |                         |
  |-------Frame 1---------->|
  |                         |
  |<------ACK 1-------------|
```

**Characteristics**:
- Send one frame, wait for ACK
- Simple but inefficient
- **Efficiency**: 1/(1 + 2a), where a = propagation time/transmission time

#### 2. Sliding Window Protocol
- Sender can send multiple frames before receiving ACK
- **Window Size**: Number of unacknowledged frames allowed
- **Go-Back-N**: Retransmit from error point
- **Selective Repeat**: Retransmit only error frames

**Go-Back-N Example**:
```
Sender Window: [0][1][2][3]    (Window size = 4)
Send: 0, 1, 2, 3
Receive ACK 0: Window slides to [1][2][3][4]
```

## MAC Protocols

### 1. Random Access Protocols

#### ALOHA
- **Pure ALOHA**: Transmit whenever ready
- **Slotted ALOHA**: Transmit only at slot boundaries
- **Efficiency**: Pure ALOHA = 18.4%, Slotted ALOHA = 36.8%

#### CSMA (Carrier Sense Multiple Access)
- **1-persistent**: Transmit immediately when channel idle
- **Non-persistent**: Wait random time if channel busy
- **p-persistent**: Transmit with probability p when channel idle

#### CSMA/CD (Collision Detection)
- **Used in**: Ethernet
- **Process**:
  1. Listen before transmit
  2. Detect collision during transmission
  3. Stop transmission and send jam signal
  4. Wait random backoff time
  5. Retry transmission

**Binary Exponential Backoff**:
- After i collisions, wait random time from [0, 2ⁱ-1] slots
- Maximum backoff: 1024 slots

#### CSMA/CA (Collision Avoidance)
- **Used in**: WiFi (802.11)
- **Process**:
  1. Listen before transmit
  2. If busy, wait random backoff
  3. Send RTS (Request to Send)
  4. Receive CTS (Clear to Send)
  5. Transmit data
  6. Receive ACK

### 2. Controlled Access Protocols

#### Polling
- Central controller polls each station
- **Advantages**: No collisions, fair access
- **Disadvantages**: Overhead, single point of failure

#### Token Passing
- **Token Ring**: Token circulates in ring
- **Token Bus**: Token passed in logical ring on bus
- **Advantages**: Deterministic access, no collisions
- **Disadvantages**: Token maintenance overhead

## Ethernet

### Ethernet Frame Format
```
|Preamble|SFD|Dest MAC|Src MAC|Length/Type|Data|Pad|FCS|
|   7    | 1 |   6    |   6   |     2     |46-1500|0-46|4|
```

**Fields**:
- **Preamble**: 7 bytes of 10101010 (synchronization)
- **SFD**: Start Frame Delimiter (10101011)
- **Destination MAC**: 6-byte destination address
- **Source MAC**: 6-byte source address
- **Length/Type**: Frame length or protocol type
- **Data**: 46-1500 bytes of payload
- **Pad**: Padding to meet minimum frame size
- **FCS**: Frame Check Sequence (CRC-32)

### MAC Address
- **Format**: 48-bit address (6 bytes)
- **Representation**: XX:XX:XX:XX:XX:XX (hexadecimal)
- **Structure**:
  - First 24 bits: OUI (Organizationally Unique Identifier)
  - Last 24 bits: Device identifier
- **Types**:
  - **Unicast**: Single destination (LSB of first byte = 0)
  - **Multicast**: Multiple destinations (LSB of first byte = 1)
  - **Broadcast**: All destinations (FF:FF:FF:FF:FF:FF)

### Ethernet Standards
| Standard | Speed | Medium | Distance |
|----------|-------|--------|----------|
| 10Base-T | 10 Mbps | UTP Cat3 | 100m |
| 100Base-TX | 100 Mbps | UTP Cat5 | 100m |
| 1000Base-T | 1 Gbps | UTP Cat5e | 100m |
| 10GBase-T | 10 Gbps | UTP Cat6a | 100m |

## Switching

### Learning Bridge/Switch Operation
1. **Learning**: Build MAC address table
2. **Filtering**: Drop frames for same segment
3. **Forwarding**: Send to appropriate port
4. **Flooding**: Broadcast unknown destinations

### Spanning Tree Protocol (STP)
- **Purpose**: Prevent loops in switched networks
- **Process**:
  1. Elect root bridge (lowest bridge ID)
  2. Calculate shortest path to root
  3. Block redundant paths
  4. Maintain loop-free topology

### VLAN (Virtual LAN)
- **Purpose**: Logical segmentation of network
- **Benefits**:
  - Broadcast domain separation
  - Security isolation
  - Flexible network design
- **Types**:
  - Port-based VLAN
  - MAC-based VLAN
  - Protocol-based VLAN

## Interview Questions

### Basic Level

**Q1: What are the main functions of Data Link Layer?**
**Answer**:
1. **Framing**: Organizing bits into frames
2. **Physical Addressing**: Using MAC addresses
3. **Flow Control**: Managing transmission rate
4. **Error Control**: Detecting and correcting errors
5. **Access Control**: Managing shared medium access

**Q2: What is the difference between LLC and MAC sublayers?**
**Answer**:
| LLC | MAC |
|-----|-----|
| Logical Link Control | Media Access Control |
| Flow and error control | Channel access control |
| Interface with Network Layer | Physical addressing |
| Protocol independent | Media specific |
| IEEE 802.2 | IEEE 802.3, 802.11, etc. |

**Q3: What is a MAC address and how is it different from IP address?**
**Answer**:
| MAC Address | IP Address |
|-------------|------------|
| 48-bit physical address | 32-bit (IPv4) logical address |
| Burned into NIC | Configurable |
| Layer 2 addressing | Layer 3 addressing |
| Local network scope | Global scope |
| Never changes | Changes with location |

### Intermediate Level

**Q4: Explain CSMA/CD protocol used in Ethernet.**
**Answer**:
**CSMA/CD (Carrier Sense Multiple Access/Collision Detection)**:
1. **Carrier Sense**: Listen before transmit
2. **Multiple Access**: Multiple stations share medium
3. **Collision Detection**: Detect collisions during transmission

**Process**:
1. Check if medium is idle
2. If idle, start transmission
3. Monitor for collisions during transmission
4. If collision detected:
   - Stop transmission
   - Send jam signal
   - Wait random backoff time (binary exponential backoff)
   - Retry transmission

**Q5: How does a switch learn MAC addresses?**
**Answer**:
1. **Initial State**: MAC table empty
2. **Frame Arrival**: Examine source MAC address
3. **Learning**: Record MAC-to-port mapping in table
4. **Forwarding Decision**:
   - Known destination: Forward to specific port
   - Unknown destination: Flood to all ports except source
5. **Aging**: Remove entries after timeout (default 300 seconds)

**Q6: What is the purpose of Spanning Tree Protocol?**
**Answer**:
**Purpose**: Prevent loops in switched networks while maintaining redundancy

**Process**:
1. **Root Bridge Election**: Lowest bridge ID becomes root
2. **Path Calculation**: Each switch calculates shortest path to root
3. **Port States**:
   - **Root Port**: Best path to root bridge
   - **Designated Port**: Best path for segment
   - **Blocked Port**: Redundant path (blocked to prevent loops)

### Advanced Level

**Q7: Calculate the efficiency of Stop-and-Wait protocol.**
**Answer**:
**Efficiency Formula**: η = 1/(1 + 2a)

Where: a = Propagation time / Transmission time

**Example**:
- Distance: 2000 km
- Speed of light: 2×10⁸ m/s
- Frame size: 1000 bits
- Data rate: 1 Mbps

**Calculations**:
- Propagation time = 2×10⁶ / (2×10⁸) = 0.01 s
- Transmission time = 1000 / 10⁶ = 0.001 s
- a = 0.01 / 0.001 = 10
- Efficiency = 1/(1 + 2×10) = 1/21 = 4.76%

**Q8: Explain the difference between Go-Back-N and Selective Repeat protocols.**
**Answer**:
| Feature | Go-Back-N | Selective Repeat |
|---------|-----------|------------------|
| Retransmission | From error point | Only error frames |
| Receiver Buffer | Simple | Complex buffering |
| Sender Window | 2ⁿ - 1 | 2ⁿ⁻¹ |
| Receiver Window | 1 | 2ⁿ⁻¹ |
| Efficiency | Lower | Higher |
| Complexity | Lower | Higher |

**Q9: How does CRC work for error detection?**
**Answer**:
**CRC (Cyclic Redundancy Check)**:
1. **Sender**:
   - Append n zeros to data (n = degree of generator polynomial)
   - Divide by generator polynomial using XOR
   - Replace zeros with remainder (CRC bits)
   - Send data + CRC

2. **Receiver**:
   - Divide received frame by same generator polynomial
   - If remainder is zero: No error
   - If remainder is non-zero: Error detected

**Example**: Data = 1101, Generator = 1011
- 1101000 ÷ 1011 = quotient with remainder 001
- Send: 1101001

## Key Takeaways

- Data Link Layer ensures reliable frame delivery over single link
- Framing organizes bit stream into manageable units
- Error detection/correction maintains data integrity
- Flow control prevents buffer overflow
- MAC protocols manage shared medium access
- Ethernet is the dominant LAN technology
- Switches learn MAC addresses and forward frames efficiently
- Understanding these concepts is crucial for network troubleshooting