# 11. Network Performance

## Overview

Network performance refers to the measurement and optimization of various metrics that determine how well a network operates. Understanding these metrics is crucial for designing, maintaining, and troubleshooting networks to meet user requirements and business objectives.

## Key Performance Metrics

### 1. Bandwidth

#### Definition
- **Theoretical Maximum**: Maximum data rate a link can carry
- **Units**: bits per second (bps), bytes per second (Bps)
- **Common Units**: Kbps, Mbps, Gbps, Tbps

#### Types of Bandwidth
- **Physical Bandwidth**: Hardware capability
- **Available Bandwidth**: Currently unused capacity
- **Effective Bandwidth**: Actual usable bandwidth after protocol overhead

#### Bandwidth vs. Throughput
| Bandwidth | Throughput |
|-----------|------------|
| Theoretical maximum | Actual achieved rate |
| Link capacity | Measured performance |
| Fixed value | Variable value |
| Hardware dependent | Network condition dependent |

### 2. Throughput

#### Definition
- **Actual data rate** achieved in practice
- **Always ≤ Bandwidth** due to various factors
- **Measured**: End-to-end application data transfer

#### Factors Affecting Throughput
1. **Protocol Overhead**: Headers, acknowledgments
2. **Network Congestion**: Shared bandwidth
3. **Error Rates**: Retransmissions reduce throughput
4. **Processing Delays**: Router/switch processing time
5. **Flow Control**: Receiver limitations

#### Throughput Calculation
```
Throughput = Data Transferred / Time Taken
Efficiency = Throughput / Bandwidth × 100%
```

### 3. Latency (Delay)

#### Components of Latency

**1. Propagation Delay**
- Time for signal to travel through medium
- **Formula**: Distance / Speed of light in medium
- **Example**: 1000 km fiber = 1000 km / (2×10⁸ m/s) = 5ms

**2. Transmission Delay**
- Time to put all bits on the wire
- **Formula**: Packet Size / Link Bandwidth
- **Example**: 1500 bytes / 100 Mbps = 0.12ms

**3. Processing Delay**
- Time to examine packet headers and make decisions
- **Typical Range**: Microseconds to milliseconds
- **Factors**: Router CPU, packet complexity

**4. Queuing Delay**
- Time waiting in router buffers
- **Variable**: Depends on network congestion
- **Can be**: Zero to several seconds

#### Total Latency
```
Total Latency = Propagation + Transmission + Processing + Queuing
```

### 4. Jitter

#### Definition
- **Variation in packet delay**
- **Impact**: Affects real-time applications (voice, video)
- **Measurement**: Standard deviation of delay

#### Causes of Jitter
1. **Variable Queuing Delays**: Network congestion
2. **Route Changes**: Different paths have different delays
3. **Processing Variations**: Router load fluctuations
4. **Serialization**: Different packet sizes

#### Jitter Mitigation
- **Buffering**: Smooth out delay variations
- **QoS**: Prioritize time-sensitive traffic
- **Traffic Shaping**: Regulate traffic flow
- **Dedicated Paths**: Reserved bandwidth

### 5. Packet Loss

#### Definition
- **Percentage of packets** that fail to reach destination
- **Causes**: Congestion, errors, equipment failures
- **Impact**: Reduces throughput, increases latency

#### Types of Packet Loss
1. **Congestion Loss**: Buffer overflow
2. **Error Loss**: Transmission errors
3. **Policy Loss**: Traffic shaping/policing
4. **Equipment Loss**: Hardware failures

#### Packet Loss Impact
```
TCP Throughput ∝ 1/√(Packet Loss Rate)
```

## Quality of Service (QoS)

### QoS Overview
- **Purpose**: Provide different service levels to different traffic types
- **Goal**: Meet application requirements for delay, jitter, loss
- **Implementation**: Traffic classification, queuing, scheduling

### QoS Models

#### 1. Best Effort
- **Service**: No guarantees
- **Implementation**: FIFO queuing
- **Use**: Default Internet service

#### 2. Integrated Services (IntServ)
- **Service**: Per-flow reservations
- **Protocol**: RSVP (Resource Reservation Protocol)
- **Scalability**: Limited due to per-flow state

#### 3. Differentiated Services (DiffServ)
- **Service**: Class-based service levels
- **Implementation**: DSCP (Differentiated Services Code Point)
- **Scalability**: Better than IntServ

### QoS Mechanisms

#### 1. Traffic Classification
- **Purpose**: Identify different traffic types
- **Methods**: 
  - **Layer 3**: IP addresses, protocols
  - **Layer 4**: Port numbers
  - **Layer 7**: Application signatures
  - **DSCP**: Differentiated Services markings

#### 2. Traffic Marking
- **Purpose**: Tag packets with service class
- **Locations**: 
  - **IP ToS/DSCP**: Layer 3 marking
  - **802.1p**: Layer 2 marking
  - **MPLS EXP**: MPLS marking

#### 3. Queuing Algorithms

**FIFO (First In, First Out)**
- **Simple**: Single queue, no prioritization
- **Fair**: All traffic treated equally
- **Problem**: No QoS differentiation

**Priority Queuing (PQ)**
- **Multiple Queues**: Different priority levels
- **Strict Priority**: Higher priority always served first
- **Problem**: Lower priority starvation

**Weighted Fair Queuing (WFQ)**
- **Fair Sharing**: Bandwidth allocated proportionally
- **Flow-based**: Separate queue per flow
- **Advantage**: Prevents single flow domination

**Class-Based Weighted Fair Queuing (CBWFQ)**
- **Class-based**: Groups flows into classes
- **Bandwidth Allocation**: Guaranteed minimum bandwidth
- **Flexibility**: Different classes, different treatment

#### 4. Traffic Shaping and Policing

**Traffic Shaping**
- **Purpose**: Smooth traffic to conform to rate
- **Method**: Buffer excess traffic
- **Result**: Delays packets but doesn't drop

**Traffic Policing**
- **Purpose**: Enforce traffic rate limits
- **Method**: Drop or mark excess traffic
- **Result**: May drop packets

**Token Bucket Algorithm**
```
Tokens added at rate R
Bucket capacity B
Packet size P

If tokens ≥ P: Forward packet, remove P tokens
If tokens < P: Drop/delay packet
```

### DSCP Classes

#### Standard Classes
| Class | DSCP | Binary | Decimal | Use |
|-------|------|--------|---------|-----|
| Best Effort | 000000 | 0 | Default traffic |
| AF11 | 001010 | 10 | Low priority data |
| AF21 | 010010 | 18 | Medium priority data |
| AF31 | 011010 | 26 | High priority data |
| EF | 101110 | 46 | Voice traffic |
| CS6 | 110000 | 48 | Network control |

## Network Optimization Techniques

### 1. Bandwidth Optimization

#### Link Aggregation
- **Purpose**: Combine multiple links for higher bandwidth
- **Standards**: IEEE 802.3ad (LACP)
- **Benefits**: Increased capacity, redundancy
- **Load Balancing**: Distribute traffic across links

#### Compression
- **Header Compression**: Reduce protocol overhead
- **Data Compression**: Compress payload data
- **Benefits**: Effective bandwidth increase
- **Trade-off**: CPU processing vs. bandwidth

### 2. Latency Optimization

#### Route Optimization
- **Shortest Path**: Minimize hop count
- **Fastest Path**: Consider link speeds
- **Load Balancing**: Distribute traffic
- **Traffic Engineering**: MPLS, SDN

#### Caching
- **Web Caching**: Store frequently accessed content
- **DNS Caching**: Cache name resolution results
- **CDN**: Content Delivery Networks
- **Benefits**: Reduced latency, bandwidth savings

### 3. Protocol Optimization

#### TCP Optimization
- **Window Scaling**: Support large windows
- **Selective ACK**: Acknowledge specific segments
- **Fast Recovery**: Avoid slow start after loss
- **Congestion Control**: Adapt to network conditions

#### Application Layer Optimization
- **HTTP/2**: Multiplexing, server push
- **HTTP/3**: QUIC transport protocol
- **Compression**: gzip, brotli
- **Minification**: Reduce file sizes

## Performance Monitoring and Measurement

### Monitoring Tools

#### 1. SNMP (Simple Network Management Protocol)
- **Purpose**: Network device monitoring
- **Components**: Manager, Agent, MIB
- **Metrics**: Interface statistics, CPU, memory
- **Versions**: SNMPv1, v2c, v3 (secure)

#### 2. Flow-based Monitoring
- **NetFlow**: Cisco's flow monitoring
- **sFlow**: Sampling-based monitoring
- **IPFIX**: Internet Protocol Flow Information Export
- **Benefits**: Traffic analysis, capacity planning

#### 3. Active Monitoring
- **Ping**: Basic connectivity and RTT
- **Traceroute**: Path discovery and hop delays
- **Iperf**: Bandwidth and throughput testing
- **Synthetic Transactions**: Application-level testing

#### 4. Passive Monitoring
- **Packet Capture**: Wireshark, tcpdump
- **Flow Analysis**: Traffic patterns
- **Log Analysis**: System and application logs
- **Benefits**: No additional network load

### Key Performance Indicators (KPIs)

#### Network KPIs
- **Availability**: Uptime percentage
- **Utilization**: Bandwidth usage percentage
- **Error Rate**: Packet loss, bit errors
- **Response Time**: Application response delays

#### Service Level Agreements (SLAs)
- **Availability**: 99.9% uptime
- **Latency**: <50ms for voice
- **Jitter**: <10ms for video
- **Packet Loss**: <0.1% for data

## Capacity Planning

### Traffic Analysis
1. **Baseline Measurement**: Current traffic patterns
2. **Growth Projection**: Historical trends
3. **Peak Analysis**: Busy hour traffic
4. **Application Mix**: Different traffic types

### Capacity Planning Process
1. **Data Collection**: Monitor current performance
2. **Trend Analysis**: Identify growth patterns
3. **Forecasting**: Predict future requirements
4. **Scenario Planning**: Consider different growth scenarios
5. **Upgrade Planning**: Timeline and budget

### Bandwidth Calculation
```
Required Bandwidth = Peak Traffic × Growth Factor × Safety Margin

Example:
Current Peak: 100 Mbps
Annual Growth: 20%
Planning Period: 3 years
Safety Margin: 50%

Required = 100 × (1.2)³ × 1.5 = 259 Mbps
```

## Interview Questions

### Basic Level

**Q1: What is the difference between bandwidth and throughput?**
**Answer**:
| Bandwidth | Throughput |
|-----------|------------|
| Theoretical maximum capacity | Actual data rate achieved |
| Fixed link capacity | Variable based on conditions |
| Hardware specification | Measured performance |
| Example: 100 Mbps Ethernet | Example: 85 Mbps actual transfer |

**Factors affecting throughput**: Protocol overhead, congestion, errors, processing delays

**Q2: What are the components of network latency?**
**Answer**:
1. **Propagation Delay**: Signal travel time (distance/speed)
2. **Transmission Delay**: Time to put bits on wire (packet size/bandwidth)
3. **Processing Delay**: Router processing time
4. **Queuing Delay**: Time waiting in buffers

**Total Latency = Propagation + Transmission + Processing + Queuing**

**Q3: What is jitter and why is it important?**
**Answer**:
**Jitter**: Variation in packet delay over time

**Importance**:
- **Voice/Video**: Causes choppy audio/video
- **Real-time Apps**: Affects user experience
- **Gaming**: Causes lag and poor responsiveness

**Mitigation**: Buffering, QoS, traffic shaping

### Intermediate Level

**Q4: Explain the difference between traffic shaping and traffic policing.**
**Answer**:
| Traffic Shaping | Traffic Policing |
|-----------------|------------------|
| Buffers excess traffic | Drops excess traffic |
| Smooths traffic flow | Enforces rate limits |
| Adds delay | May cause packet loss |
| Used at network edge | Used at provider edge |
| Better for TCP | May cause TCP retransmissions |

**Use Cases**:
- **Shaping**: Customer premises, smooth uploads
- **Policing**: Service provider, enforce SLAs

**Q5: How does QoS help improve network performance?**
**Answer**:
**QoS Benefits**:
1. **Prioritization**: Critical traffic gets preference
2. **Bandwidth Allocation**: Guaranteed minimum bandwidth
3. **Latency Control**: Reduce delays for time-sensitive traffic
4. **Jitter Reduction**: Consistent delay for real-time apps

**QoS Mechanisms**:
- **Classification**: Identify traffic types
- **Marking**: Tag packets with priority
- **Queuing**: Different treatment per class
- **Shaping/Policing**: Rate control

**Q6: What is the relationship between packet loss and TCP throughput?**
**Answer**:
**TCP Throughput Formula**:
```
Throughput ≈ (MSS × C) / (RTT × √p)
```
Where:
- MSS = Maximum Segment Size
- C = Constant (~1.22)
- RTT = Round Trip Time
- p = Packet loss probability

**Key Points**:
- Throughput inversely proportional to √(packet loss)
- Small increases in loss significantly reduce throughput
- 1% loss can reduce throughput by ~90%

### Advanced Level

**Q7: How do you calculate the bandwidth-delay product and why is it important?**
**Answer**:
**Bandwidth-Delay Product (BDP)**:
```
BDP = Bandwidth × Round Trip Time
```

**Example**:
- Bandwidth: 100 Mbps
- RTT: 50ms
- BDP = 100 Mbps × 0.05s = 5 Mb = 625 KB

**Importance**:
- **TCP Window Size**: Should be ≥ BDP for optimal performance
- **Buffer Sizing**: Router buffers should accommodate BDP
- **Protocol Design**: Affects sliding window protocols

**Q8: Explain how CBWFQ (Class-Based Weighted Fair Queuing) works.**
**Answer**:
**CBWFQ Operation**:
1. **Classification**: Packets classified into classes
2. **Queue Assignment**: Each class gets dedicated queue
3. **Bandwidth Allocation**: Minimum bandwidth guaranteed per class
4. **Scheduling**: Weighted fair scheduling between classes
5. **Excess Bandwidth**: Shared proportionally

**Configuration Example**:
```
Class Voice: 30% bandwidth, priority queue
Class Video: 40% bandwidth, low latency queue
Class Data: 20% bandwidth, default queue
Class Default: 10% bandwidth, best effort
```

**Benefits**:
- Guaranteed bandwidth per class
- Fair sharing of excess bandwidth
- Protection from other classes

**Q9: How does network congestion affect different types of applications?**
**Answer**:
**Application Sensitivity**:

| Application | Bandwidth | Latency | Jitter | Loss |
|-------------|-----------|---------|--------|------|
| Web Browsing | Medium | Medium | Low | Low |
| Email | Low | Low | Low | Low |
| File Transfer | High | Low | Low | Medium |
| Voice (VoIP) | Low | High | High | High |
| Video Streaming | High | Medium | High | Medium |
| Online Gaming | Low | High | High | High |
| Video Conferencing | High | High | High | High |

**Congestion Effects**:
- **Increased Latency**: Queuing delays
- **Packet Loss**: Buffer overflows
- **Jitter**: Variable queuing delays
- **Reduced Throughput**: Retransmissions, backoff

**Q10: Design a QoS policy for a corporate network with voice, video, and data traffic.**
**Answer**:
**QoS Policy Design**:

**1. Traffic Classification**:
```
Voice: RTP (UDP 16384-32767), DSCP EF
Video: H.264, DSCP AF41
Critical Data: ERP, CRM, DSCP AF31
Normal Data: Web, Email, DSCP AF21
Bulk Data: Backup, DSCP AF11
```

**2. Bandwidth Allocation**:
```
Voice: 10% (priority queue, <150ms, <30ms jitter)
Video: 30% (guaranteed, <400ms)
Critical Data: 25% (guaranteed)
Normal Data: 25% (guaranteed)
Bulk Data: 10% (best effort)
```

**3. Queue Configuration**:
```
Priority Queue: Voice (strict priority)
Class-Based Queues: Video, Critical, Normal, Bulk
Default Queue: Unclassified traffic
```

**4. Policing/Shaping**:
```
Voice: Police to 64 kbps per call
Video: Shape to allocated bandwidth
Data: Police excess traffic
```

## Performance Optimization Best Practices

### Network Design
1. **Hierarchical Design**: Core, distribution, access layers
2. **Redundancy**: Multiple paths, equipment redundancy
3. **Scalability**: Plan for growth
4. **Segmentation**: VLANs, subnets for traffic isolation

### Monitoring and Maintenance
1. **Baseline Establishment**: Know normal performance
2. **Proactive Monitoring**: Identify issues before users complain
3. **Capacity Planning**: Plan upgrades before congestion
4. **Regular Maintenance**: Keep equipment updated

### Application Optimization
1. **Protocol Selection**: Choose appropriate protocols
2. **Caching**: Implement strategic caching
3. **Compression**: Reduce data transmission
4. **Load Balancing**: Distribute traffic efficiently

## Key Takeaways

- Network performance involves multiple interrelated metrics
- Bandwidth is theoretical maximum; throughput is actual performance
- Latency has multiple components that must be considered
- QoS is essential for supporting diverse application requirements
- Monitoring and measurement are crucial for optimization
- Capacity planning prevents performance degradation
- Understanding performance metrics is essential for network design
- Optimization requires balancing multiple competing factors