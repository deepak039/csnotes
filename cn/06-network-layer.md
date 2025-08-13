# 6. Network Layer

## Overview

The Network Layer is responsible for routing packets from source to destination across multiple networks. It provides logical addressing, path determination, and packet forwarding services.

## Key Functions

1. **Logical Addressing**: Using IP addresses for global identification
2. **Routing**: Determining best path to destination
3. **Path Determination**: Finding route through internetwork
4. **Packet Forwarding**: Moving packets toward destination
5. **Fragmentation**: Breaking large packets into smaller ones
6. **Congestion Control**: Managing network traffic

## IP Addressing

### IPv4 Addressing

#### Address Structure
- **Size**: 32 bits (4 bytes)
- **Format**: Dotted decimal notation (e.g., 192.168.1.1)
- **Range**: 0.0.0.0 to 255.255.255.255
- **Total Addresses**: 2³² = 4,294,967,296

#### Address Classes
| Class | Range | Default Mask | Network Bits | Host Bits | Use |
|-------|-------|--------------|--------------|-----------|-----|
| A | 1.0.0.0 - 126.255.255.255 | /8 (255.0.0.0) | 8 | 24 | Large networks |
| B | 128.0.0.0 - 191.255.255.255 | /16 (255.255.0.0) | 16 | 16 | Medium networks |
| C | 192.0.0.0 - 223.255.255.255 | /24 (255.255.255.0) | 24 | 8 | Small networks |
| D | 224.0.0.0 - 239.255.255.255 | N/A | N/A | N/A | Multicast |
| E | 240.0.0.0 - 255.255.255.255 | N/A | N/A | N/A | Experimental |

#### Special Addresses
- **0.0.0.0**: This network
- **127.0.0.1**: Loopback address
- **255.255.255.255**: Limited broadcast
- **169.254.x.x**: APIPA (Automatic Private IP Addressing)

#### Private IP Ranges (RFC 1918)
- **Class A**: 10.0.0.0/8 (10.0.0.0 - 10.255.255.255)
- **Class B**: 172.16.0.0/12 (172.16.0.0 - 172.31.255.255)
- **Class C**: 192.168.0.0/16 (192.168.0.0 - 192.168.255.255)

### Subnetting

#### Purpose
- Efficient IP address utilization
- Network segmentation
- Improved security
- Reduced broadcast traffic

#### Subnet Mask
- Identifies network and host portions
- **Format**: Dotted decimal or CIDR notation
- **Example**: 255.255.255.0 or /24

#### CIDR (Classless Inter-Domain Routing)
- Eliminates class boundaries
- Variable Length Subnet Mask (VLSM)
- **Notation**: IP/prefix (e.g., 192.168.1.0/24)

#### Subnetting Example
**Network**: 192.168.1.0/24
**Requirement**: 4 subnets

**Solution**:
- Borrow 2 bits from host portion (2² = 4 subnets)
- New mask: /26 (255.255.255.192)
- Subnets:
  - 192.168.1.0/26 (192.168.1.1 - 192.168.1.62)
  - 192.168.1.64/26 (192.168.1.65 - 192.168.1.126)
  - 192.168.1.128/26 (192.168.1.129 - 192.168.1.190)
  - 192.168.1.192/26 (192.168.1.193 - 192.168.1.254)

### IPv6 Addressing

#### Address Structure
- **Size**: 128 bits (16 bytes)
- **Format**: Hexadecimal with colons (e.g., 2001:0db8:85a3::8a2e:0370:7334)
- **Total Addresses**: 2¹²⁸ ≈ 3.4 × 10³⁸

#### Address Types
1. **Unicast**: Single interface
2. **Multicast**: Multiple interfaces
3. **Anycast**: Nearest interface from group

#### Address Notation
- **Full**: 2001:0db8:85a3:0000:0000:8a2e:0370:7334
- **Compressed**: 2001:db8:85a3::8a2e:370:7334
- **Rules**:
  - Leading zeros can be omitted
  - Consecutive zero groups replaced with ::
  - :: can appear only once

#### IPv6 Address Categories
- **Global Unicast**: 2000::/3 (Internet routable)
- **Link-Local**: fe80::/10 (Local link only)
- **Unique Local**: fc00::/7 (Private addressing)
- **Multicast**: ff00::/8
- **Loopback**: ::1

## Routing

### Routing Concepts

#### Routing Table
Contains information about:
- **Destination Network**: Target network address
- **Next Hop**: Next router in path
- **Interface**: Outgoing interface
- **Metric**: Cost of route
- **Administrative Distance**: Route preference

#### Route Types
1. **Directly Connected**: Local network segments
2. **Static Routes**: Manually configured
3. **Dynamic Routes**: Learned via routing protocols

### Routing Algorithms

#### 1. Distance Vector
- **Principle**: Share routing table with neighbors
- **Algorithm**: Bellman-Ford
- **Protocols**: RIP, EIGRP
- **Metric**: Hop count, bandwidth, delay

**Characteristics**:
- Periodic updates
- Count-to-infinity problem
- Split horizon to prevent loops

#### 2. Link State
- **Principle**: Share link state information
- **Algorithm**: Dijkstra's shortest path
- **Protocols**: OSPF, IS-IS
- **Metric**: Cost based on bandwidth

**Characteristics**:
- Topology database
- Faster convergence
- More CPU and memory intensive

#### 3. Path Vector
- **Principle**: Share path information
- **Protocol**: BGP
- **Use**: Inter-domain routing

### Routing Protocols

#### RIP (Routing Information Protocol)
- **Type**: Distance Vector
- **Metric**: Hop count (max 15)
- **Updates**: Every 30 seconds
- **Versions**: RIPv1, RIPv2, RIPng (IPv6)

#### OSPF (Open Shortest Path First)
- **Type**: Link State
- **Metric**: Cost (based on bandwidth)
- **Features**: 
  - Hierarchical design (areas)
  - Fast convergence
  - Load balancing
  - Authentication

#### EIGRP (Enhanced Interior Gateway Routing Protocol)
- **Type**: Advanced Distance Vector
- **Metric**: Composite (bandwidth, delay, reliability, load)
- **Features**:
  - Fast convergence
  - Unequal cost load balancing
  - Cisco proprietary

#### BGP (Border Gateway Protocol)
- **Type**: Path Vector
- **Use**: Internet routing between ASes
- **Features**:
  - Policy-based routing
  - Scalable
  - Prevents loops using AS path

## IP Protocol

### IPv4 Header
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

#### Key Fields
- **Version**: IP version (4 for IPv4)
- **IHL**: Internet Header Length
- **ToS**: Type of Service (QoS)
- **Total Length**: Packet size including header
- **TTL**: Time to Live (hop limit)
- **Protocol**: Next layer protocol (TCP=6, UDP=17)
- **Checksum**: Header error detection

### Fragmentation
- **Purpose**: Handle different MTU sizes
- **Process**:
  1. Large packet exceeds link MTU
  2. Router fragments packet
  3. Each fragment has IP header
  4. Destination reassembles fragments

- **Fields Used**:
  - **Identification**: Same for all fragments
  - **Flags**: More Fragments (MF), Don't Fragment (DF)
  - **Fragment Offset**: Position in original packet

## ICMP (Internet Control Message Protocol)

### Purpose
- Error reporting
- Network diagnostics
- Control messages

### ICMP Message Types
| Type | Description | Use |
|------|-------------|-----|
| 0 | Echo Reply | ping response |
| 3 | Destination Unreachable | Routing errors |
| 5 | Redirect | Route optimization |
| 8 | Echo Request | ping request |
| 11 | Time Exceeded | TTL expired, traceroute |

### Common ICMP Tools
- **ping**: Test connectivity
- **traceroute**: Trace packet path
- **pathping**: Combines ping and traceroute

## ARP (Address Resolution Protocol)

### Purpose
Map IP addresses to MAC addresses on local network

### ARP Process
1. Host needs to send packet to IP address
2. Check ARP cache for MAC address
3. If not found, broadcast ARP request
4. Target host responds with MAC address
5. Sender caches mapping and sends packet

### ARP Table
```
IP Address      MAC Address       Type
192.168.1.1     00:1a:2b:3c:4d:5e dynamic
192.168.1.100   00:aa:bb:cc:dd:ee static
```

## NAT (Network Address Translation)

### Purpose
- Conserve public IP addresses
- Provide security through address hiding
- Enable private networks to access Internet

### NAT Types
1. **Static NAT**: One-to-one mapping
2. **Dynamic NAT**: Pool of public addresses
3. **PAT (Port Address Translation)**: Many-to-one with ports

### NAT Process
1. Private host sends packet to Internet
2. NAT router replaces source IP with public IP
3. NAT maintains translation table
4. Return traffic translated back to private IP

## Interview Questions

### Basic Level

**Q1: What is the difference between IPv4 and IPv6?**
**Answer**:
| Feature | IPv4 | IPv6 |
|---------|------|------|
| Address Size | 32 bits | 128 bits |
| Address Format | Dotted decimal | Hexadecimal with colons |
| Address Space | 4.3 billion | 340 undecillion |
| Header Size | Variable (20-60 bytes) | Fixed (40 bytes) |
| Fragmentation | Router and sender | Sender only |
| Security | Optional (IPSec) | Built-in (IPSec) |

**Q2: What are the classes of IPv4 addresses?**
**Answer**:
- **Class A**: 1.0.0.0 - 126.255.255.255 (/8) - Large networks
- **Class B**: 128.0.0.0 - 191.255.255.255 (/16) - Medium networks  
- **Class C**: 192.0.0.0 - 223.255.255.255 (/24) - Small networks
- **Class D**: 224.0.0.0 - 239.255.255.255 - Multicast
- **Class E**: 240.0.0.0 - 255.255.255.255 - Experimental

**Q3: What is subnetting and why is it used?**
**Answer**:
**Subnetting**: Dividing a network into smaller subnetworks

**Benefits**:
- Efficient IP address utilization
- Network segmentation and organization
- Improved security through isolation
- Reduced broadcast traffic
- Better network management

### Intermediate Level

**Q4: Calculate the number of subnets and hosts for 192.168.1.0/26.**
**Answer**:
**Given**: 192.168.1.0/26
- **Original**: Class C (/24)
- **Subnet mask**: /26 (255.255.255.192)
- **Borrowed bits**: 26 - 24 = 2 bits

**Calculations**:
- **Subnets**: 2² = 4 subnets
- **Host bits**: 32 - 26 = 6 bits
- **Hosts per subnet**: 2⁶ - 2 = 62 hosts (subtract network and broadcast)

**Subnets**:
1. 192.168.1.0/26 (192.168.1.1 - 192.168.1.62)
2. 192.168.1.64/26 (192.168.1.65 - 192.168.1.126)
3. 192.168.1.128/26 (192.168.1.129 - 192.168.1.190)
4. 192.168.1.192/26 (192.168.1.193 - 192.168.1.254)

**Q5: Explain the difference between distance vector and link state routing.**
**Answer**:
| Feature | Distance Vector | Link State |
|---------|-----------------|------------|
| Information Shared | Routing table | Link state |
| Algorithm | Bellman-Ford | Dijkstra |
| Convergence | Slower | Faster |
| Memory Usage | Lower | Higher |
| CPU Usage | Lower | Higher |
| Examples | RIP, EIGRP | OSPF, IS-IS |
| Loop Prevention | Split horizon | Topology database |

**Q6: How does ARP work?**
**Answer**:
1. **Need**: Host A wants to send packet to Host B (knows IP, needs MAC)
2. **Cache Check**: A checks ARP cache for B's MAC address
3. **ARP Request**: If not found, A broadcasts ARP request: "Who has IP B?"
4. **ARP Reply**: B responds with its MAC address
5. **Cache Update**: A updates ARP cache with B's MAC address
6. **Communication**: A can now send packet to B using MAC address

### Advanced Level

**Q7: Explain VLSM with an example.**
**Answer**:
**VLSM (Variable Length Subnet Mask)**: Using different subnet mask lengths within the same network

**Example**: 192.168.1.0/24 network
**Requirements**:
- Subnet A: 50 hosts
- Subnet B: 25 hosts  
- Subnet C: 10 hosts
- Subnet D: 5 hosts

**Solution**:
1. **Subnet A**: /26 (62 hosts) → 192.168.1.0/26
2. **Subnet B**: /27 (30 hosts) → 192.168.1.64/27
3. **Subnet C**: /28 (14 hosts) → 192.168.1.96/28
4. **Subnet D**: /29 (6 hosts) → 192.168.1.112/29

**Q8: How does NAT work and what are its types?**
**Answer**:
**NAT Process**:
1. Private host (192.168.1.10) sends packet to Internet
2. NAT router replaces source IP with public IP (203.0.113.1)
3. NAT maintains translation table
4. Return traffic translated back to private IP

**NAT Types**:
1. **Static NAT**: 1:1 mapping (192.168.1.10 ↔ 203.0.113.10)
2. **Dynamic NAT**: Pool of public IPs assigned dynamically
3. **PAT**: Many private IPs share one public IP using different ports

**Q9: What happens when IP packet size exceeds MTU?**
**Answer**:
**Fragmentation Process**:
1. Router receives packet larger than outgoing link MTU
2. Router fragments packet into smaller pieces
3. Each fragment gets IP header with:
   - Same Identification field
   - Fragment Offset indicating position
   - More Fragments flag (except last fragment)
4. Destination host reassembles fragments
5. If any fragment lost, entire packet discarded

**IPv6 Difference**: Only source can fragment, routers drop oversized packets

## Key Takeaways

- Network Layer provides logical addressing and routing
- IPv4 uses 32-bit addresses with classful/classless addressing
- IPv6 provides virtually unlimited address space
- Subnetting enables efficient address utilization
- Routing protocols find best paths through networks
- ICMP provides error reporting and diagnostics
- ARP maps IP addresses to MAC addresses
- NAT enables private networks to access Internet
- Understanding these concepts is crucial for network design and troubleshooting