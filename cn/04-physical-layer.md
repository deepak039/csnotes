# 4. Physical Layer

## Overview

The Physical Layer is the lowest layer in both OSI and TCP/IP models. It deals with the actual transmission of raw bits over a physical medium. This layer defines the electrical, mechanical, and procedural specifications for activating, maintaining, and deactivating physical connections.

## Key Functions

1. **Bit Transmission**: Converting digital data to physical signals
2. **Physical Topology**: Defining how devices are physically connected
3. **Transmission Media**: Specifying the medium for data transmission
4. **Signal Encoding**: Converting bits to electrical/optical/radio signals
5. **Synchronization**: Ensuring sender and receiver are synchronized

## Transmission Media

### 1. Guided Media (Wired)

#### Twisted Pair Cable
**Structure**: Two insulated copper wires twisted together

**Types**:
- **UTP (Unshielded Twisted Pair)**
  - Categories: Cat5, Cat5e, Cat6, Cat6a, Cat7
  - Distance: Up to 100 meters
  - Speed: 10 Mbps to 10 Gbps
  - Cost: Low
  - Use: Ethernet networks

- **STP (Shielded Twisted Pair)**
  - Additional shielding reduces interference
  - More expensive than UTP
  - Better performance in noisy environments

**Cat5e vs Cat6 Comparison**:
| Feature | Cat5e | Cat6 |
|---------|-------|------|
| Bandwidth | 100 MHz | 250 MHz |
| Speed | 1 Gbps | 10 Gbps |
| Distance | 100m | 55m (10 Gbps) |
| Crosstalk | Higher | Lower |

#### Coaxial Cable
**Structure**: Central conductor, insulation, metallic shield, outer jacket

**Types**:
- **Thin Coax (10Base2)**: 50-ohm impedance
- **Thick Coax (10Base5)**: 50-ohm impedance
- **Cable TV**: 75-ohm impedance

**Characteristics**:
- Better shielding than twisted pair
- Higher bandwidth than twisted pair
- More expensive than twisted pair
- Used in cable TV, older Ethernet

#### Fiber Optic Cable
**Structure**: Core, cladding, buffer, jacket

**Types**:
- **Single-mode Fiber (SMF)**
  - Core diameter: 8-10 micrometers
  - Distance: Up to 100+ km
  - Speed: Very high (100 Gbps+)
  - Cost: Higher
  - Use: Long-distance, backbone networks

- **Multi-mode Fiber (MMF)**
  - Core diameter: 50-62.5 micrometers
  - Distance: Up to 2 km
  - Speed: High (10-100 Gbps)
  - Cost: Lower than SMF
  - Use: Campus networks, data centers

**Advantages**:
- Immune to electromagnetic interference
- High bandwidth
- Long distance transmission
- Secure (difficult to tap)

**Disadvantages**:
- Expensive
- Fragile
- Requires special connectors

### 2. Unguided Media (Wireless)

#### Radio Waves
- **Frequency**: 3 kHz to 300 GHz
- **Propagation**: Omnidirectional
- **Use**: AM/FM radio, TV, cellular
- **Characteristics**: Can penetrate walls, affected by weather

#### Microwaves
- **Frequency**: 300 MHz to 300 GHz
- **Propagation**: Line-of-sight
- **Use**: Satellite communication, point-to-point links
- **Characteristics**: High frequency, directional

#### Infrared
- **Frequency**: 300 GHz to 400 THz
- **Propagation**: Line-of-sight
- **Use**: Remote controls, short-range communication
- **Characteristics**: Cannot penetrate walls

## Signal Encoding

### Digital-to-Digital Encoding

#### 1. Non-Return-to-Zero (NRZ)
- **NRZ-L**: Low = 0, High = 1
- **NRZ-I**: Inversion for 1, no change for 0
- **Problem**: Synchronization issues with long runs

#### 2. Return-to-Zero (RZ)
- Signal returns to zero in middle of bit period
- Better synchronization
- Requires more bandwidth

#### 3. Manchester Encoding
- **Used in**: Ethernet (10Base-T)
- **Encoding**: 
  - 0 = High-to-Low transition
  - 1 = Low-to-High transition
- **Advantage**: Self-synchronizing
- **Disadvantage**: Requires double bandwidth

#### 4. Differential Manchester
- **Used in**: Token Ring
- **Encoding**:
  - 0 = Transition at beginning
  - 1 = No transition at beginning
- Always has transition in middle for synchronization

### Digital-to-Analog Encoding

#### 1. Amplitude Shift Keying (ASK)
- Varies amplitude of carrier signal
- Simple but susceptible to noise

#### 2. Frequency Shift Keying (FSK)
- Varies frequency of carrier signal
- More robust than ASK

#### 3. Phase Shift Keying (PSK)
- Varies phase of carrier signal
- Most robust against noise

#### 4. Quadrature Amplitude Modulation (QAM)
- Combines amplitude and phase modulation
- High data rates
- Used in modems, WiFi

## Multiplexing Techniques

### 1. Frequency Division Multiplexing (FDM)
- Different signals use different frequency bands
- **Example**: Radio stations, cable TV
- **Guard bands** prevent interference
- Analog multiplexing technique

### 2. Time Division Multiplexing (TDM)
- Different signals use different time slots
- **Synchronous TDM**: Fixed time slots
- **Statistical TDM**: Dynamic allocation
- Digital multiplexing technique

### 3. Wavelength Division Multiplexing (WDM)
- Different signals use different light wavelengths
- **DWDM**: Dense WDM with many wavelengths
- Used in fiber optic networks
- Massive bandwidth multiplication

### 4. Code Division Multiplexing (CDM)
- Different signals use different codes
- **CDMA**: Used in cellular networks
- All signals share same frequency and time

## Network Devices at Physical Layer

### 1. Repeater
- **Function**: Amplifies and regenerates signals
- **OSI Layer**: Physical Layer
- **Use**: Extend cable length
- **Limitation**: Cannot filter or process data

### 2. Hub
- **Function**: Multi-port repeater
- **OSI Layer**: Physical Layer
- **Characteristics**:
  - Single collision domain
  - Half-duplex communication
  - Broadcasts to all ports
- **Obsolete**: Replaced by switches

### 3. Media Converters
- **Function**: Convert between different media types
- **Examples**: 
  - Fiber to Ethernet
  - Coax to Twisted Pair
- **Use**: Connecting different network segments

## Transmission Impairments

### 1. Attenuation
- **Definition**: Signal strength decreases with distance
- **Solution**: Amplifiers, repeaters
- **Measurement**: Decibels (dB)

### 2. Distortion
- **Definition**: Signal shape changes during transmission
- **Causes**: Different frequency components travel at different speeds
- **Solution**: Equalization

### 3. Noise
- **Types**:
  - **Thermal Noise**: Random electron movement
  - **Intermodulation Noise**: Multiple signals interfering
  - **Crosstalk**: Signal from adjacent wires
  - **Impulse Noise**: Sudden spikes (lightning, switching)

**Signal-to-Noise Ratio (SNR)**:
```
SNR (dB) = 10 log₁₀(Signal Power / Noise Power)
```

## Performance Metrics

### 1. Bandwidth
- **Definition**: Range of frequencies a medium can carry
- **Units**: Hertz (Hz)
- **Digital Context**: Maximum data rate

### 2. Throughput
- **Definition**: Actual data rate achieved
- **Always ≤ Bandwidth**
- **Affected by**: Protocol overhead, errors, congestion

### 3. Latency (Delay)
- **Propagation Delay**: Time for signal to travel
- **Transmission Delay**: Time to put bits on wire
- **Processing Delay**: Time to process packet
- **Queuing Delay**: Time waiting in buffers

### 4. Jitter
- **Definition**: Variation in packet delay
- **Impact**: Affects real-time applications
- **Solution**: Buffering, QoS

## Interview Questions

### Basic Level

**Q1: What is the function of Physical Layer?**
**Answer**: 
The Physical Layer is responsible for:
- Transmitting raw bits over physical medium
- Defining electrical and mechanical specifications
- Signal encoding and modulation
- Physical topology and connections
- Synchronization between sender and receiver

**Q2: What are the types of transmission media?**
**Answer**:
**Guided Media (Wired)**:
- Twisted Pair (UTP/STP)
- Coaxial Cable
- Fiber Optic Cable

**Unguided Media (Wireless)**:
- Radio Waves
- Microwaves
- Infrared

**Q3: What is the difference between single-mode and multi-mode fiber?**
**Answer**:
| Feature | Single-mode | Multi-mode |
|---------|-------------|------------|
| Core Diameter | 8-10 μm | 50-62.5 μm |
| Light Source | Laser | LED/Laser |
| Distance | 100+ km | Up to 2 km |
| Cost | Higher | Lower |
| Bandwidth | Very High | High |

### Intermediate Level

**Q4: Explain Manchester encoding and why it's used.**
**Answer**:
**Manchester Encoding**:
- Each bit period divided into two halves
- 0 = High-to-Low transition
- 1 = Low-to-High transition

**Advantages**:
- Self-synchronizing (always has transition)
- No DC component
- Error detection capability

**Disadvantage**: Requires double bandwidth

**Q5: What is attenuation and how is it measured?**
**Answer**:
**Attenuation**: Loss of signal strength over distance

**Measurement**: Decibels (dB)
```
Attenuation (dB) = 10 log₁₀(P₁/P₂)
```
Where P₁ = input power, P₂ = output power

**Solutions**:
- Repeaters (regenerate signal)
- Amplifiers (boost signal)
- Use appropriate cable category

**Q6: What is the difference between FDM and TDM?**
**Answer**:
| Feature | FDM | TDM |
|---------|-----|-----|
| Division Method | Frequency | Time |
| Signal Type | Analog | Digital |
| Guard Bands | Required | Not required |
| Bandwidth Usage | Continuous | Intermittent |
| Examples | Radio, Cable TV | T1, E1 lines |

### Advanced Level

**Q7: Calculate the maximum data rate for a channel with bandwidth 4000 Hz and SNR of 30 dB.**
**Answer**:
Using Shannon's Theorem:
```
C = B × log₂(1 + SNR)
```

First convert SNR from dB:
```
SNR = 10^(30/10) = 1000
```

Then calculate capacity:
```
C = 4000 × log₂(1 + 1000)
C = 4000 × log₂(1001)
C = 4000 × 9.97
C ≈ 39,880 bps
```

**Q8: Explain the concept of eye diagram in digital communication.**
**Answer**:
**Eye Diagram**: Oscilloscope display showing signal quality
- **Formation**: Overlay multiple bit periods
- **Analysis**:
  - **Eye Opening**: Larger = better signal quality
  - **Eye Height**: Noise margin
  - **Eye Width**: Timing margin
  - **Jitter**: Horizontal eye closure
  - **Noise**: Vertical eye closure

**Q9: What is the difference between baseband and broadband transmission?**
**Answer**:
| Feature | Baseband | Broadband |
|---------|----------|-----------|
| Signal Type | Digital | Analog |
| Frequency | Single frequency | Multiple frequencies |
| Direction | Bidirectional | Unidirectional |
| Distance | Limited | Extended |
| Examples | Ethernet | Cable TV |
| Multiplexing | TDM | FDM |

## Key Takeaways

- Physical layer handles actual bit transmission
- Choice of transmission media affects performance and cost
- Signal encoding is crucial for reliable communication
- Multiplexing enables efficient bandwidth utilization
- Understanding impairments helps in network design
- Performance metrics guide network optimization