# Power of Two Problems

## 🎯 Core Concept: Powers of 2

Powers of 2 have exactly one bit set in their binary representation:
```
1 = 2⁰ = 0001
2 = 2¹ = 0010  
4 = 2² = 0100
8 = 2³ = 1000
16 = 2⁴ = 10000
```

## 🔍 Check if Number is Power of 2

### Method 1: Classic Bit Trick ⭐
```python
def is_power_of_two(n):
    """Most elegant solution"""
    return n > 0 and (n & (n - 1)) == 0

# Why it works:
# Power of 2: n = 1000, n-1 = 0111
# n & (n-1) = 1000 & 0111 = 0000
# Non-power: n = 1010, n-1 = 1001  
# n & (n-1) = 1010 & 1001 = 1000 ≠ 0
```

### Method 2: Count Set Bits
```python
def is_power_of_two_count(n):
    """Count bits approach"""
    return n > 0 and bin(n).count('1') == 1
```

### Method 3: Isolate Rightmost Bit
```python
def is_power_of_two_isolate(n):
    """Using bit isolation"""
    return n > 0 and (n & -n) == n

# n & -n isolates rightmost set bit
# For power of 2, this equals the number itself
```

### Method 4: Mathematical Approach
```python
import math

def is_power_of_two_math(n):
    """Using logarithms"""
    if n <= 0: return False
    return math.log2(n).is_integer()
```

## 🔢 Find Powers of 2

### Generate Powers of 2
```python
def generate_powers_of_two(max_power):
    """Generate first max_power powers of 2"""
    return [1 << i for i in range(max_power)]

# Alternative: [2**i for i in range(max_power)]
```

### Next Power of 2
```python
def next_power_of_two(n):
    """Find smallest power of 2 >= n"""
    if n <= 1: return 1
    
    # Method 1: Bit manipulation
    n -= 1
    n |= n >> 1
    n |= n >> 2  
    n |= n >> 4
    n |= n >> 8
    n |= n >> 16
    return n + 1

def next_power_of_two_simple(n):
    """Simpler approach"""
    if n <= 1: return 1
    return 1 << (n - 1).bit_length()
```

### Previous Power of 2
```python
def prev_power_of_two(n):
    """Find largest power of 2 <= n"""
    if n < 1: return 0
    return 1 << (n.bit_length() - 1)

# Alternative: n & -n for rightmost power of 2
```

## 🎯 Advanced Power of 2 Problems

### Problem 1: Power of Four
```python
def is_power_of_four(n):
    """Check if number is power of 4"""
    # Power of 4 is power of 2 with bit at even position
    return (n > 0 and 
            (n & (n - 1)) == 0 and  # Power of 2
            (n & 0x55555555) != 0)  # Bit at even position

# 0x55555555 = 01010101... (bits at even positions)
```

### Problem 2: Power of Three
```python
def is_power_of_three(n):
    """Check if number is power of 3 (no bit manipulation)"""
    # Largest power of 3 in 32-bit: 3^19 = 1162261467
    return n > 0 and 1162261467 % n == 0

def is_power_of_three_iterative(n):
    """Iterative approach"""
    if n < 1: return False
    while n % 3 == 0:
        n //= 3
    return n == 1
```

### Problem 3: Reachable Numbers
```python
def reachable_numbers(blocked, source, target):
    """Can we reach target from source avoiding blocked cells?"""
    # Uses power of 2 for BFS optimization
    from collections import deque
    
    if source == target: return True
    
    blocked_set = set(map(tuple, blocked))
    if len(blocked_set) < 2: return True
    
    # Maximum area that can be blocked: roughly n*(n-1)/2
    # where n is number of blocked cells
    max_blocked_area = len(blocked_set) * (len(blocked_set) - 1) // 2
    
    def bfs(start, end):
        queue = deque([start])
        visited = {tuple(start)}
        
        while queue and len(visited) <= max_blocked_area:
            x, y = queue.popleft()
            
            for dx, dy in [(0,1), (1,0), (0,-1), (-1,0)]:
                nx, ny = x + dx, y + dy
                
                if (0 <= nx < 10**6 and 0 <= ny < 10**6 and
                    (nx, ny) not in blocked_set and
                    (nx, ny) not in visited):
                    
                    if [nx, ny] == end:
                        return True
                    
                    visited.add((nx, ny))
                    queue.append([nx, ny])
        
        return len(visited) > max_blocked_area
    
    return bfs(source, target) and bfs(target, source)
```

## 🧮 Mathematical Properties

### Sum of Powers of 2
```python
def sum_powers_of_two(n):
    """Sum of first n powers of 2: 2^0 + 2^1 + ... + 2^(n-1)"""
    return (1 << n) - 1  # 2^n - 1

# Example: 1 + 2 + 4 + 8 = 15 = 2^4 - 1
```

### Largest Power of 2 Divisor
```python
def largest_power_of_two_divisor(n):
    """Find largest power of 2 that divides n"""
    return n & -n

# Example: 12 = 1100, largest divisor is 4 = 0100
```

### Count Powers of 2 in Range
```python
def count_powers_of_two_in_range(left, right):
    """Count powers of 2 in range [left, right]"""
    count = 0
    power = 1
    
    while power <= right:
        if power >= left:
            count += 1
        power <<= 1  # power *= 2
    
    return count
```

## 🎯 Interview Problems

### Easy: Power of Two (LeetCode 231)
```python
def isPowerOfTwo(n):
    return n > 0 and (n & (n - 1)) == 0
```

### Easy: Power of Four (LeetCode 342)
```python
def isPowerOfFour(n):
    return (n > 0 and 
            (n & (n - 1)) == 0 and 
            (n & 0x55555555) != 0)
```

### Medium: Single Number II
```python
def singleNumber(nums):
    """Find number appearing once, others appear 3 times"""
    ones = twos = 0
    
    for num in nums:
        ones = (ones ^ num) & ~twos
        twos = (twos ^ num) & ~ones
    
    return ones
```

### Medium: Maximum XOR of Two Numbers
```python
def findMaximumXOR(nums):
    """Find maximum XOR of any two numbers"""
    max_xor = 0
    mask = 0
    
    for i in range(31, -1, -1):
        mask |= (1 << i)
        prefixes = {num & mask for num in nums}
        
        temp = max_xor | (1 << i)
        for prefix in prefixes:
            if temp ^ prefix in prefixes:
                max_xor = temp
                break
    
    return max_xor
```

### Hard: Minimum Number of Taps
```python
def minTaps(n, ranges):
    """Minimum taps to water garden using bit manipulation optimization"""
    # Convert to interval covering problem
    intervals = []
    for i, r in enumerate(ranges):
        if r > 0:
            intervals.append([max(0, i - r), min(n, i + r)])
    
    intervals.sort()
    
    taps = 0
    i = 0
    start = end = 0
    
    while end < n:
        while i < len(intervals) and intervals[i][0] <= start:
            end = max(end, intervals[i][1])
            i += 1
        
        if start == end:
            return -1
        
        taps += 1
        start = end
    
    return taps
```

## 🔧 Optimization Techniques

### Fast Power Calculation
```python
def fast_power(base, exp):
    """Calculate base^exp using bit manipulation"""
    result = 1
    while exp > 0:
        if exp & 1:  # If exp is odd
            result *= base
        base *= base
        exp >>= 1
    return result
```

### Bit Manipulation for Set Operations
```python
def power_set_bits(arr):
    """Generate all subsets using bit manipulation"""
    n = len(arr)
    subsets = []
    
    for mask in range(1 << n):  # 2^n subsets
        subset = []
        for i in range(n):
            if mask & (1 << i):
                subset.append(arr[i])
        subsets.append(subset)
    
    return subsets
```

## 📊 Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Check power of 2 | O(1) | O(1) | Single bit operation |
| Next power of 2 | O(1) | O(1) | Fixed number of operations |
| Generate powers | O(n) | O(n) | n powers to generate |
| Power set | O(n × 2ⁿ) | O(2ⁿ) | Exponential in nature |

## 🎯 Common Patterns

### Pattern 1: Single Bit Set
```python
# Check if exactly one bit is set
def has_single_bit(n):
    return n > 0 and (n & (n - 1)) == 0
```

### Pattern 2: Bit Position Manipulation
```python
# Set bit at power of 2 position
def set_power_bit(n, power):
    return n | (1 << power)

# Clear bit at power of 2 position  
def clear_power_bit(n, power):
    return n & ~(1 << power)
```

### Pattern 3: Range Queries
```python
# Count set bits in range using powers of 2
def count_bits_range(left, right):
    count = 0
    for i in range(32):
        if (1 << i) >= left and (1 << i) <= right:
            count += 1
    return count
```

## 🚨 Edge Cases to Consider

1. **Zero**: Not a power of 2 (by convention)
2. **Negative numbers**: Cannot be powers of 2
3. **One**: 2⁰ = 1 is a power of 2
4. **Large numbers**: Consider integer overflow
5. **Floating point**: Powers of 2 in decimal representation

## 📝 Practice Problems

### Easy:
1. Check if number is power of 2
2. Find next power of 2
3. Count powers of 2 up to n

### Medium:
1. Power of four detection
2. Single number variations
3. Maximum XOR problems

### Hard:
1. Minimum flips to make OR equal
2. Stone game with powers of 2
3. Trie-based XOR problems

## 🎯 Interview Tips

- **Memorize** the classic `n & (n-1) == 0` trick
- **Understand** why powers of 2 have single bits set
- **Practice** bit manipulation with small examples
- **Consider** edge cases like 0 and negative numbers
- **Know** the relationship between powers of 2 and bit positions
- **Remember** that bit shifting is multiplication/division by powers of 2