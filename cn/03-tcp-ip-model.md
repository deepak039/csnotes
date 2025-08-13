# 3. TCP/IP Model

## Overview

The TCP/IP model is a practical, 4-layer networking model that forms the foundation of the Internet. Unlike the theoretical OSI model, TCP/IP was designed based on existing protocols and is widely implemented.

## The 4 Layers

### Layer 4: Application Layer
**Purpose**: Provides network services directly to applications

**Combines OSI Layers**: Application + Presentation + Session

**Key Protocols**:
- **HTTP/HTTPS**: Web browsing
- **FTP**: File transfer
- **SMTP**: Email sending
- **POP3/IMAP**: Email receiving
- **DNS**: Domain name resolution
- **DHCP**: IP address assignment
- **Telnet/SSH**: Remote access
- **SNMP**: Network management

**Functions**:
- User interface
- Data encryption/compression
- Session management
- Application-specific protocols

### Layer 3: Transport Layer
**Purpose**: End-to-end communication and reliability

**Key Protocols**:
- **TCP (Transmission Control Protocol)**
- **UDP (User Datagram Protocol)**

**TCP Features**:
- Connection-oriented
- Reliable delivery
- Flow control
- Congestion control
- Error detection and correction
- Ordered delivery

**UDP Features**:
- Connectionless
- Unreliable (best-effort)
- Low overhead
- No flow control
- Fast transmission

### Layer 2: Internet Layer
**Purpose**: Routing and logical addressing

**Equivalent to**: OSI Network Layer

**Key Protocols**:
- **IP (Internet Protocol)**: IPv4, IPv6
- **ICMP**: Error reporting and diagnostics
- **ARP**: Address resolution
- **RARP**: Reverse address resolution

**Functions**:
- Logical addressing (IP addresses)
- Routing between networks
- Packet forwarding
- Fragmentation and reassembly

### Layer 1: Network Access Layer
**Purpose**: Physical transmission and local network access

**Combines OSI Layers**: Data Link + Physical

**Technologies**:
- **Ethernet**: Wired LAN
- **WiFi (802.11)**: Wireless LAN
- **PPP**: Point-to-Point Protocol
- **Frame Relay**: WAN technology

**Functions**:
- Physical addressing (MAC)
- Frame formatting
- Error detection
- Media access control

## TCP/IP vs OSI Model Comparison

| TCP/IP Layer | OSI Equivalent | Key Protocols |
|--------------|----------------|---------------|
| Application | Application + Presentation + Session | HTTP, FTP, SMTP, DNS |
| Transport | Transport | TCP, UDP |
| Internet | Network | IP, ICMP, ARP |
| Network Access | Data Link + Physical | Ethernet, WiFi |

## TCP Protocol Details

### TCP Header Structure
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

### TCP Flags
- **SYN**: Synchronize sequence numbers
- **ACK**: Acknowledgment field significant
- **FIN**: No more data from sender
- **RST**: Reset the connection
- **PSH**: Push function
- **URG**: Urgent pointer field significant

### TCP 3-Way Handshake
```
Client                    Server
  |                         |
  |-------SYN(seq=x)------->|
  |                         |
  |<--SYN-ACK(seq=y,ack=x+1)|
  |                         |
  |-------ACK(ack=y+1)----->|
  |                         |
  |    Connection Established|
```

### TCP Connection Termination (4-Way Handshake)
```
Client                    Server
  |                         |
  |-------FIN(seq=x)------->|
  |                         |
  |<------ACK(ack=x+1)------|
  |                         |
  |<------FIN(seq=y)--------|
  |                         |
  |-------ACK(ack=y+1)----->|
  |                         |
  |    Connection Closed    |
```

## UDP Protocol Details

### UDP Header Structure
```
0      7 8     15 16    23 24    31
+--------+--------+--------+--------+
|     Source      |   Destination   |
|      Port       |      Port       |
+--------+--------+--------+--------+
|                 |                 |
|     Length      |    Checksum     |
+--------+--------+--------+--------+
|                                   |
|              Data                 |
+-----------------------------------+
```

## IP Protocol Details

### IPv4 Header Structure
```
0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|Version|  IHL  |Type of Service|          Total Length         |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|         Identification        |Flags|      Fragment Offset    |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|  Time to Live |    Protocol   |         Header Checksum       |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       Source Address                          |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                    Destination Address                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

## Port Numbers

### Well-Known Ports (0-1023)
- **20/21**: FTP (Data/Control)
- **22**: SSH
- **23**: Telnet
- **25**: SMTP
- **53**: DNS
- **67/68**: DHCP
- **80**: HTTP
- **110**: POP3
- **143**: IMAP
- **443**: HTTPS
- **993**: IMAPS
- **995**: POP3S

### Registered Ports (1024-49151)
- **1433**: Microsoft SQL Server
- **3306**: MySQL
- **5432**: PostgreSQL
- **8080**: HTTP Alternate

### Dynamic/Private Ports (49152-65535)
- Used for client-side connections
- Assigned dynamically by OS

## Interview Questions

### Basic Level

**Q1: What are the 4 layers of TCP/IP model?**
**Answer**:
1. **Application Layer**: User applications and services
2. **Transport Layer**: End-to-end communication (TCP/UDP)
3. **Internet Layer**: Routing and logical addressing (IP)
4. **Network Access Layer**: Physical transmission and local access

**Q2: What is the difference between TCP and UDP?**
**Answer**:
| Feature | TCP | UDP |
|---------|-----|-----|
| Connection | Connection-oriented | Connectionless |
| Reliability | Reliable | Unreliable |
| Speed | Slower | Faster |
| Header Size | 20 bytes minimum | 8 bytes |
| Use Cases | Web browsing, Email | DNS, Video streaming |

**Q3: What is a port number and why is it needed?**
**Answer**:
- Port number identifies specific application/service on a device
- 16-bit number (0-65535)
- **Need**: Multiple applications can run on same IP address
- **Example**: Web server (port 80) and email server (port 25) on same machine

### Intermediate Level

**Q4: Explain TCP 3-way handshake.**
**Answer**:
1. **Client → Server**: SYN packet (seq=x)
2. **Server → Client**: SYN-ACK packet (seq=y, ack=x+1)
3. **Client → Server**: ACK packet (ack=y+1)

**Purpose**: Establishes connection, synchronizes sequence numbers, negotiates parameters

**Q5: What happens if SYN packet is lost during handshake?**
**Answer**:
- Client waits for SYN-ACK response
- After timeout, client retransmits SYN packet
- Exponential backoff used for retransmission intervals
- After multiple failures, connection attempt fails

**Q6: How does TCP ensure reliable delivery?**
**Answer**:
- **Sequence Numbers**: Track data order
- **Acknowledgments**: Confirm receipt
- **Retransmission**: Resend lost packets
- **Checksums**: Detect corruption
- **Flow Control**: Prevent buffer overflow
- **Congestion Control**: Adapt to network conditions

### Advanced Level

**Q7: Explain TCP flow control mechanism.**
**Answer**:
**Sliding Window Protocol**:
- Receiver advertises window size in TCP header
- Sender can send data up to window size without ACK
- Window size adjusts based on receiver's buffer space
- **Zero Window**: Receiver buffer full, sender stops
- **Window Update**: Receiver sends new window size

**Q8: What is TCP congestion control and name its algorithms.**
**Answer**:
**Purpose**: Prevent network congestion and packet loss

**Algorithms**:
1. **Slow Start**: Exponentially increase congestion window
2. **Congestion Avoidance**: Linear increase after threshold
3. **Fast Retransmit**: Retransmit on 3 duplicate ACKs
4. **Fast Recovery**: Avoid slow start after fast retransmit

**Q9: How does ARP work in TCP/IP model?**
**Answer**:
**ARP (Address Resolution Protocol)**:
1. Host needs to send packet to IP address on local network
2. Checks ARP cache for corresponding MAC address
3. If not found, broadcasts ARP request: "Who has IP X.X.X.X?"
4. Target host responds: "IP X.X.X.X is at MAC YY:YY:YY:YY:YY:YY"
5. Sender caches mapping and sends original packet

**Q10: What is the difference between IPv4 and IPv6?**
**Answer**:
| Feature | IPv4 | IPv6 |
|---------|------|------|
| Address Size | 32 bits | 128 bits |
| Address Format | Dotted decimal | Hexadecimal |
| Address Space | 4.3 billion | 340 undecillion |
| Header Size | Variable (20-60 bytes) | Fixed (40 bytes) |
| Fragmentation | Router and sender | Sender only |
| Security | Optional (IPSec) | Built-in (IPSec) |

## Common TCP/IP Utilities

### Diagnostic Tools
- **ping**: Test connectivity using ICMP
- **traceroute/tracert**: Trace packet path
- **netstat**: Display network connections
- **nslookup/dig**: DNS lookup
- **telnet**: Test port connectivity

### Example Commands
```bash
ping google.com
tracert google.com
netstat -an
nslookup google.com
telnet google.com 80
```

## Key Takeaways

- TCP/IP is the practical foundation of Internet
- 4-layer model is simpler than 7-layer OSI
- TCP provides reliability, UDP provides speed
- Port numbers enable multiple services per IP
- Understanding protocols essential for troubleshooting
- Each layer has specific responsibilities and protocols