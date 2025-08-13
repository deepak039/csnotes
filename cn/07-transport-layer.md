# 7. Transport Layer

## Overview

The Transport Layer provides end-to-end communication services between applications running on different hosts. It ensures reliable data delivery, flow control, and error recovery.

## Key Functions

1. **End-to-End Communication**: Direct communication between source and destination applications
2. **Segmentation**: Breaking large messages into smaller segments
3. **Reassembly**: Reconstructing original message from segments
4. **Connection Management**: Establishing, maintaining, and terminating connections
5. **Flow Control**: Managing data transmission rate
6. **Error Control**: Detecting and correcting transmission errors
7. **Multiplexing**: Multiple applications sharing network connection

## Transport Layer Protocols

### TCP (Transmission Control Protocol)

#### Characteristics
- **Connection-oriented**: Establishes connection before data transfer
- **Reliable**: Guarantees delivery and order
- **Full-duplex**: Bidirectional communication
- **Stream-oriented**: Treats data as continuous stream
- **Flow control**: Prevents buffer overflow
- **Congestion control**: Adapts to network conditions

#### TCP Header Structure
```
0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|          Source Port          |       Destination Port        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                        Sequence Number                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                    Acknowledgment Number                      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|  Data |           |U|A|P|R|S|F|                               |
| Offset| Reserved  |R|C|S|S|Y|I|            Window             |
|       |           |G|K|H|T|N|N|                               |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|           Checksum            |         Urgent Pointer        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

#### TCP Flags
- **SYN**: Synchronize sequence numbers (connection establishment)
- **ACK**: Acknowledgment field significant
- **FIN**: Finish, no more data (connection termination)
- **RST**: Reset connection (abort)
- **PSH**: Push data immediately
- **URG**: Urgent pointer field significant

### UDP (User Datagram Protocol)

#### Characteristics
- **Connectionless**: No connection establishment
- **Unreliable**: Best-effort delivery
- **Message-oriented**: Preserves message boundaries
- **Low overhead**: Minimal header
- **Fast**: No connection setup delay
- **No flow control**: No rate limiting
- **No congestion control**: No network adaptation

#### UDP Header Structure
```
0      7 8     15 16    23 24    31
+--------+--------+--------+--------+
|     Source      |   Destination   |
|      Port       |      Port       |
+--------+--------+--------+--------+
|                 |                 |
|     Length      |    Checksum     |
+--------+--------+--------+--------+
```

## Port Numbers

### Purpose
- Identify specific applications/services
- Enable multiplexing of connections
- 16-bit number (0-65535)

### Port Categories

#### Well-Known Ports (0-1023)
| Port | Protocol | Service |
|------|----------|---------|
| 20/21 | TCP | FTP (Data/Control) |
| 22 | TCP | SSH |
| 23 | TCP | Telnet |
| 25 | TCP | SMTP |
| 53 | TCP/UDP | DNS |
| 67/68 | UDP | DHCP |
| 80 | TCP | HTTP |
| 110 | TCP | POP3 |
| 143 | TCP | IMAP |
| 443 | TCP | HTTPS |
| 993 | TCP | IMAPS |
| 995 | TCP | POP3S |

#### Registered Ports (1024-49151)
- **1433**: Microsoft SQL Server
- **3306**: MySQL
- **5432**: PostgreSQL
- **8080**: HTTP Alternate

#### Dynamic/Private Ports (49152-65535)
- Used by client applications
- Assigned dynamically by OS

## TCP Connection Management

### 3-Way Handshake (Connection Establishment)
```
Client                    Server
  |                         |
  |-------SYN(seq=x)------->|  1. Client sends SYN
  |                         |
  |<--SYN-ACK(seq=y,ack=x+1)|  2. Server responds SYN-ACK
  |                         |
  |-------ACK(ack=y+1)----->|  3. Client sends ACK
  |                         |
  |    Connection Established|
```

**Process**:
1. **Client → Server**: SYN packet with initial sequence number
2. **Server → Client**: SYN-ACK packet acknowledging client's SYN
3. **Client → Server**: ACK packet acknowledging server's SYN

### 4-Way Handshake (Connection Termination)
```
Client                    Server
  |                         |
  |-------FIN(seq=x)------->|  1. Client initiates close
  |                         |
  |<------ACK(ack=x+1)------|  2. Server acknowledges
  |                         |
  |<------FIN(seq=y)--------|  3. Server initiates close
  |                         |
  |-------ACK(ack=y+1)----->|  4. Client acknowledges
  |                         |
  |    Connection Closed    |
```

### TCP States
- **CLOSED**: No connection
- **LISTEN**: Waiting for connection request
- **SYN-SENT**: SYN sent, waiting for SYN-ACK
- **SYN-RECEIVED**: SYN received, SYN-ACK sent
- **ESTABLISHED**: Connection established
- **FIN-WAIT-1**: FIN sent, waiting for ACK
- **FIN-WAIT-2**: FIN acknowledged, waiting for peer's FIN
- **CLOSE-WAIT**: Received FIN, waiting for application to close
- **LAST-ACK**: FIN sent, waiting for final ACK
- **TIME-WAIT**: Waiting for delayed packets

## Flow Control

### Purpose
- Prevent fast sender from overwhelming slow receiver
- Ensure reliable data delivery
- Optimize network performance

### TCP Sliding Window
- **Window Size**: Amount of unacknowledged data allowed
- **Advertised by receiver** in TCP header
- **Dynamic adjustment** based on buffer space

#### Example
```
Sender Window: [1000][1001][1002][1003]  (Window size = 4)
Send: 1000, 1001, 1002, 1003
Receive ACK 1001: Window slides to [1001][1002][1003][1004]
```

### Zero Window
- Receiver buffer full
- Advertises window size = 0
- Sender stops transmission
- Receiver sends window update when space available

## Congestion Control

### Purpose
- Prevent network congestion
- Adapt to network conditions
- Maintain network stability
- Ensure fair bandwidth sharing

### TCP Congestion Control Algorithms

#### 1. Slow Start
- **Initial**: Congestion window (cwnd) = 1 MSS
- **Growth**: Exponential increase (double each RTT)
- **Threshold**: Slow start threshold (ssthresh)
- **Transition**: Switch to congestion avoidance at ssthresh

#### 2. Congestion Avoidance
- **Growth**: Linear increase (1 MSS per RTT)
- **Formula**: cwnd = cwnd + MSS²/cwnd
- **Purpose**: Probe for available bandwidth

#### 3. Fast Retransmit
- **Trigger**: 3 duplicate ACKs received
- **Action**: Retransmit lost segment immediately
- **Benefit**: Avoid timeout delay

#### 4. Fast Recovery
- **After**: Fast retransmit
- **Action**: Set ssthresh = cwnd/2, cwnd = ssthresh
- **Benefit**: Avoid slow start phase

### Congestion Control Phases
```
cwnd
 |
 |    /\
 |   /  \     Congestion Avoidance
 |  /    \   (Linear increase)
 | /      \
 |/        \
 |          \
 |           \____
 |                \____
 |_________________\____> Time
   Slow Start      Timeout/Loss
   (Exponential)   (Multiplicative decrease)
```

## Error Control

### TCP Error Control Mechanisms

#### 1. Checksums
- Detect corrupted segments
- Calculated over header and data
- Receiver discards corrupted segments

#### 2. Sequence Numbers
- Track segment order
- Detect missing segments
- Enable reassembly

#### 3. Acknowledgments
- Confirm successful receipt
- Cumulative ACKs (acknowledge all up to sequence number)
- Selective ACKs (SACK) for specific segments

#### 4. Timeouts and Retransmission
- **RTO (Retransmission Timeout)**: Time to wait for ACK
- **RTT Estimation**: Measure round-trip time
- **Exponential Backoff**: Increase timeout on successive failures

### UDP Error Control
- **Checksum**: Optional error detection
- **No retransmission**: Application responsibility
- **No ordering**: Application must handle

## Multiplexing and Demultiplexing

### Multiplexing (Sender)
- Multiple applications send data
- Transport layer adds port numbers
- Creates segments for network layer

### Demultiplexing (Receiver)
- Receive segments from network layer
- Use port numbers to identify applications
- Deliver data to correct application

### Socket Identification
- **TCP**: 4-tuple (src IP, src port, dest IP, dest port)
- **UDP**: 2-tuple (dest IP, dest port)

## Interview Questions

### Basic Level

**Q1: What is the difference between TCP and UDP?**
**Answer**:
| Feature | TCP | UDP |
|---------|-----|-----|
| Connection | Connection-oriented | Connectionless |
| Reliability | Reliable | Unreliable |
| Ordering | Ordered delivery | No ordering |
| Speed | Slower | Faster |
| Header Size | 20+ bytes | 8 bytes |
| Flow Control | Yes | No |
| Congestion Control | Yes | No |
| Use Cases | Web, Email, File Transfer | DNS, Video Streaming, Gaming |

**Q2: What is a port number and why is it needed?**
**Answer**:
- **Port Number**: 16-bit identifier for applications/services
- **Purpose**: Enable multiple applications on same host
- **Range**: 0-65535
- **Categories**: Well-known (0-1023), Registered (1024-49151), Dynamic (49152-65535)
- **Example**: Web server (port 80) and email server (port 25) on same machine

**Q3: Explain TCP 3-way handshake.**
**Answer**:
1. **Client → Server**: SYN (seq=x) - "I want to connect"
2. **Server → Client**: SYN-ACK (seq=y, ack=x+1) - "OK, I'm ready too"
3. **Client → Server**: ACK (ack=y+1) - "Connection established"

**Purpose**: Synchronize sequence numbers and establish connection parameters

### Intermediate Level

**Q4: What happens if a SYN packet is lost during handshake?**
**Answer**:
- Client waits for SYN-ACK response
- After timeout (typically 3 seconds), client retransmits SYN
- Uses exponential backoff: 3s, 6s, 12s, 24s...
- After multiple failures (usually 5-6 attempts), connection fails
- Total timeout: ~75 seconds

**Q5: How does TCP flow control work?**
**Answer**:
**Sliding Window Protocol**:
- Receiver advertises window size in TCP header
- Window size = available buffer space
- Sender can transmit up to window size without ACK
- As data acknowledged, window slides forward
- **Zero Window**: Receiver buffer full, sender stops
- **Window Update**: Receiver advertises new window size

**Q6: What is the purpose of sequence numbers in TCP?**
**Answer**:
- **Ordering**: Ensure data delivered in correct order
- **Duplicate Detection**: Identify retransmitted segments
- **Reassembly**: Reconstruct original message
- **Flow Control**: Track acknowledged data
- **Initial Value**: Random number for security

### Advanced Level

**Q7: Explain TCP congestion control algorithms.**
**Answer**:
**Four Main Algorithms**:

1. **Slow Start**:
   - Start with cwnd = 1 MSS
   - Double cwnd each RTT (exponential growth)
   - Continue until ssthresh reached

2. **Congestion Avoidance**:
   - Linear increase: cwnd += MSS²/cwnd per ACK
   - Probe for additional bandwidth carefully

3. **Fast Retransmit**:
   - Retransmit on 3 duplicate ACKs
   - Don't wait for timeout

4. **Fast Recovery**:
   - Set ssthresh = cwnd/2
   - Set cwnd = ssthresh (skip slow start)

**Q8: How does TCP estimate RTT and calculate RTO?**
**Answer**:
**RTT Estimation**:
```
EstimatedRTT = (1-α) × EstimatedRTT + α × SampleRTT
DevRTT = (1-β) × DevRTT + β × |SampleRTT - EstimatedRTT|
```
Where α = 0.125, β = 0.25

**RTO Calculation**:
```
RTO = EstimatedRTT + 4 × DevRTT
```

**Karn's Algorithm**: Don't use RTT samples from retransmitted segments

**Q9: What is the TIME-WAIT state and why is it needed?**
**Answer**:
**TIME-WAIT State**: Final state in TCP connection termination

**Duration**: 2 × MSL (Maximum Segment Lifetime) = 2-4 minutes

**Purposes**:
1. **Reliable Termination**: Ensure final ACK reaches peer
2. **Prevent Confusion**: Avoid old segments affecting new connections
3. **Port Reuse**: Prevent immediate reuse of same port pair

**Problem**: Can exhaust available ports on busy servers

**Q10: How does UDP achieve reliability in applications?**
**Answer**:
**Application-Level Mechanisms**:
- **Acknowledgments**: Application sends ACKs
- **Timeouts**: Application implements retransmission timers
- **Sequence Numbers**: Application tracks message order
- **Checksums**: Additional error detection
- **Flow Control**: Application-level rate limiting

**Examples**:
- **DNS**: Simple request-response with timeout
- **DHCP**: Retransmission with exponential backoff
- **RTP**: Sequence numbers for media streaming
- **QUIC**: Reliable transport over UDP

## Performance Considerations

### TCP Performance Factors
- **Bandwidth-Delay Product**: Optimal window size
- **Congestion**: Network capacity limitations
- **Loss Rate**: Affects throughput significantly
- **RTT**: Round-trip time impacts performance

### TCP Throughput Formula
```
Throughput ≈ (Window Size) / RTT
```

For loss-based congestion control:
```
Throughput ≈ (MSS × C) / (RTT × √p)
```
Where C is constant, p is loss probability

### Optimization Techniques
- **Window Scaling**: Support windows > 64KB
- **Selective ACK (SACK)**: Acknowledge specific segments
- **Timestamp Option**: Better RTT measurement
- **TCP Fast Open**: Reduce connection setup latency

## Key Takeaways

- Transport layer provides end-to-end communication services
- TCP offers reliability at cost of complexity and overhead
- UDP offers simplicity and speed at cost of reliability
- Port numbers enable application multiplexing
- TCP connection management ensures reliable communication
- Flow control prevents receiver buffer overflow
- Congestion control adapts to network conditions
- Understanding transport protocols crucial for application design