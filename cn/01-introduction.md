# 1. Introduction to Computer Networks

## What is a Computer Network?

A computer network is a collection of interconnected devices that can communicate and share resources with each other. These devices include computers, servers, routers, switches, and other networking equipment.

### Key Characteristics:
- **Resource Sharing**: Files, printers, internet connection
- **Communication**: Email, messaging, video calls
- **Reliability**: Backup and redundancy
- **Scalability**: Easy to add new devices

## Types of Networks

### 1. Personal Area Network (PAN)
- **Range**: 1-10 meters
- **Examples**: Bluetooth, USB connections
- **Use Cases**: Connecting phone to headphones, mouse to laptop

### 2. Local Area Network (LAN)
- **Range**: Up to 1 km
- **Examples**: Home WiFi, office network
- **Technologies**: Ethernet, WiFi
- **Topology**: Star, Bus, Ring

### 3. Metropolitan Area Network (MAN)
- **Range**: 10-100 km
- **Examples**: City-wide networks, cable TV networks
- **Technologies**: Fiber optic, microwave

### 4. Wide Area Network (WAN)
- **Range**: Unlimited (global)
- **Examples**: Internet, corporate networks across cities
- **Technologies**: Satellite, fiber optic, cellular

## Network Topologies

### 1. Bus Topology
```
Device1 ---- Device2 ---- Device3 ---- Device4
```
- **Pros**: Simple, cost-effective
- **Cons**: Single point of failure, collision issues

### 2. Star Topology
```
    Device2
        |
Device1-Hub-Device3
        |
    Device4
```
- **Pros**: Easy to manage, no collisions
- **Cons**: Central hub failure affects all

### 3. Ring Topology
```
Device1 ---- Device2
   |            |
Device4 ---- Device3
```
- **Pros**: Equal access, no collisions
- **Cons**: Break in ring affects all

### 4. Mesh Topology
```
Device1 ---- Device2
  |  \    /  |
  |   \  /   |
  |    \/    |
  |    /\    |
  |   /  \   |
Device4 ---- Device3
```
- **Pros**: High reliability, multiple paths
- **Cons**: Expensive, complex

## Network Components

### 1. Network Interface Card (NIC)
- Connects device to network
- Has unique MAC address
- Handles data transmission

### 2. Hub
- Operates at Physical Layer
- Broadcasts data to all ports
- Creates single collision domain

### 3. Switch
- Operates at Data Link Layer
- Learns MAC addresses
- Creates separate collision domains

### 4. Router
- Operates at Network Layer
- Routes data between networks
- Uses IP addresses

### 5. Gateway
- Connects different network protocols
- Protocol translation
- Often combined with router

## Network Protocols

### Protocol Stack
- **Application Layer**: HTTP, FTP, SMTP
- **Transport Layer**: TCP, UDP
- **Network Layer**: IP, ICMP
- **Data Link Layer**: Ethernet, WiFi
- **Physical Layer**: Cables, radio waves

## Interview Questions

### Basic Level

**Q1: What is the difference between LAN and WAN?**
**Answer**: 
- LAN covers small area (building/campus), high speed, low cost
- WAN covers large area (cities/countries), lower speed, higher cost
- LAN uses Ethernet/WiFi, WAN uses leased lines/satellite

**Q2: What is a MAC address?**
**Answer**: 
- Media Access Control address
- 48-bit unique identifier for network interfaces
- Format: XX:XX:XX:XX:XX:XX (hexadecimal)
- First 24 bits: Manufacturer ID, Last 24 bits: Device ID

**Q3: Difference between Hub and Switch?**
**Answer**:
| Hub | Switch |
|-----|--------|
| Physical Layer | Data Link Layer |
| Half-duplex | Full-duplex |
| Single collision domain | Multiple collision domains |
| Broadcasts to all ports | Unicasts to specific port |
| Cheaper | More expensive |

### Intermediate Level

**Q4: What is network topology and which is most commonly used?**
**Answer**: 
Network topology defines physical/logical arrangement of network devices.
- **Most common**: Star topology
- **Reasons**: Easy troubleshooting, centralized management, failure isolation
- **Used in**: Modern Ethernet networks, WiFi networks

**Q5: Explain the difference between collision domain and broadcast domain.**
**Answer**:
- **Collision Domain**: Area where data collisions can occur
  - Hub creates single collision domain
  - Switch creates separate collision domains per port
- **Broadcast Domain**: Area where broadcast frames are propagated
  - Switch maintains single broadcast domain
  - Router separates broadcast domains

### Advanced Level

**Q6: How does a switch learn MAC addresses?**
**Answer**:
1. **Initial State**: MAC table empty
2. **Frame Arrival**: Switch examines source MAC
3. **Learning**: Records MAC-to-port mapping
4. **Forwarding Decision**:
   - Known destination: Forward to specific port
   - Unknown destination: Flood to all ports except source
5. **Aging**: Entries expire after timeout (default 300 seconds)

**Q7: What happens when you type a URL in browser? (Network perspective)**
**Answer**:
1. **DNS Resolution**: Browser queries DNS server for IP address
2. **TCP Connection**: 3-way handshake with web server
3. **HTTP Request**: Browser sends GET request
4. **Routing**: Packets routed through multiple routers
5. **Response**: Server sends HTML content
6. **Rendering**: Browser displays webpage

## Key Takeaways

- Networks enable resource sharing and communication
- Different network types serve different geographical areas
- Topology choice affects performance and reliability
- Network devices operate at different OSI layers
- Understanding basics is crucial for advanced networking concepts