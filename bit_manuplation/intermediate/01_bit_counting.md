# Bit Counting Algorithms

## 🎯 Problem: Count Set Bits (Hamming Weight)

Count the number of 1s in the binary representation of a number.

## 🔄 Algorithm Approaches

### 1. Naive Approach - Check Each Bit
```python
def count_bits_naive(n):
    """Time: O(log n), Space: O(1)"""
    count = 0
    while n:
        count += n & 1
        n >>= 1
    return count

# Example: 13 (1101) → 3 set bits
```

### 2. Brian Kernighan's Algorithm ⭐
```python
def count_bits_kernighan(n):
    """Time: O(k), Space: O(1) where k = number of set bits"""
    count = 0
    while n:
        n &= n - 1  # Remove rightmost set bit
        count += 1
    return count

# Why it works:
# n = 1100, n-1 = 1011
# n & (n-1) = 1100 & 1011 = 1000 (removed rightmost 1)
```

### 3. Lookup Table Approach
```python
# Precompute for 8-bit numbers
BIT_COUNT = [bin(i).count('1') for i in range(256)]

def count_bits_lookup(n):
    """Time: O(1) for 32-bit, Space: O(256)"""
    return (BIT_COUNT[n & 0xFF] + 
            BIT_COUNT[(n >> 8) & 0xFF] + 
            BIT_COUNT[(n >> 16) & 0xFF] + 
            BIT_COUNT[(n >> 24) & 0xFF])
```

### 4. Parallel Counting (SWAR)
```python
def count_bits_parallel(n):
    """SWAR (SIMD Within A Register) - O(log log n)"""
    # Count bits in parallel
    n = n - ((n >> 1) & 0x55555555)
    n = (n & 0x33333333) + ((n >> 2) & 0x33333333)
    n = (n + (n >> 4)) & 0x0F0F0F0F
    n = n + (n >> 8)
    n = n + (n >> 16)
    return n & 0x3F

# Magic numbers explanation:
# 0x55555555 = 01010101... (alternating bits)
# 0x33333333 = 00110011... (pairs of bits)
# 0x0F0F0F0F = 00001111... (nibbles)
```

### 5. Built-in Methods
```python
def count_bits_builtin(n):
    """Using language built-ins"""
    return bin(n).count('1')  # Python
    # return Integer.bitCount(n);  // Java
    # return __builtin_popcount(n);  // C++
```

## 📊 Performance Comparison

| Method | Time | Space | Best For |
|--------|------|-------|----------|
| Naive | O(log n) | O(1) | Learning |
| Kernighan | O(k) | O(1) | Sparse bits |
| Lookup | O(1) | O(256) | Frequent calls |
| Parallel | O(log log n) | O(1) | Performance |
| Built-in | O(1) | O(1) | Production |

## 🎯 Related Problems

### Problem 1: Count Bits for Range [0, n]
```python
def count_bits_range(n):
    """Count bits for all numbers from 0 to n"""
    result = [0] * (n + 1)
    
    # DP approach: result[i] = result[i >> 1] + (i & 1)
    for i in range(1, n + 1):
        result[i] = result[i >> 1] + (i & 1)
    
    return result

# Alternative: result[i] = result[i & (i-1)] + 1
```

### Problem 2: Hamming Distance
```python
def hamming_distance(x, y):
    """Count different bits between two numbers"""
    return count_bits_kernighan(x ^ y)

# XOR gives 1 where bits differ, count those 1s
```

### Problem 3: Total Hamming Distance
```python
def total_hamming_distance(nums):
    """Sum of hamming distances between all pairs"""
    total = 0
    n = len(nums)
    
    # For each bit position
    for i in range(32):
        ones = sum((num >> i) & 1 for num in nums)
        zeros = n - ones
        total += ones * zeros  # Each 1 pairs with each 0
    
    return total
```

### Problem 4: Binary Watch
```python
def read_binary_watch(num):
    """Find all times with exactly num LEDs on"""
    times = []
    
    for h in range(12):
        for m in range(60):
            if count_bits_kernighan(h) + count_bits_kernighan(m) == num:
                times.append(f"{h}:{m:02d}")
    
    return times
```

## 🧮 Advanced Counting Techniques

### Count Trailing Zeros
```python
def count_trailing_zeros(n):
    """Count zeros from right until first 1"""
    if n == 0: return 32  # or bit width
    
    count = 0
    while (n & 1) == 0:
        count += 1
        n >>= 1
    return count

# Alternative: Use De Bruijn sequence for O(1)
def count_trailing_zeros_fast(n):
    if n == 0: return 32
    return (n & -n).bit_length() - 1
```

### Count Leading Zeros
```python
def count_leading_zeros(n):
    """Count zeros from left until first 1"""
    if n == 0: return 32
    
    count = 0
    # Binary search approach
    if n <= 0x0000FFFF: count += 16; n <<= 16
    if n <= 0x00FFFFFF: count += 8;  n <<= 8
    if n <= 0x0FFFFFFF: count += 4;  n <<= 4
    if n <= 0x3FFFFFFF: count += 2;  n <<= 2
    if n <= 0x7FFFFFFF: count += 1
    
    return count
```

### Parity (Even/Odd number of 1s)
```python
def parity_naive(n):
    """Check if number of 1s is odd"""
    return count_bits_kernighan(n) & 1

def parity_fast(n):
    """O(log log n) parity check"""
    n ^= n >> 16
    n ^= n >> 8
    n ^= n >> 4
    n ^= n >> 2
    n ^= n >> 1
    return n & 1
```

## 🎯 Interview Problems

### Easy: Number of 1 Bits
```python
def hamming_weight(n):
    """LeetCode 191: Number of 1 Bits"""
    count = 0
    while n:
        n &= n - 1
        count += 1
    return count
```

### Medium: Counting Bits
```python
def counting_bits(n):
    """LeetCode 338: Counting Bits"""
    dp = [0] * (n + 1)
    for i in range(1, n + 1):
        dp[i] = dp[i >> 1] + (i & 1)
    return dp
```

### Medium: Binary Watch
```python
def read_binary_watch(num):
    """LeetCode 401: Binary Watch"""
    result = []
    for h in range(12):
        for m in range(60):
            if bin(h).count('1') + bin(m).count('1') == num:
                result.append(f"{h}:{m:02d}")
    return result
```

### Hard: Maximum XOR of Two Numbers
```python
def find_maximum_xor(nums):
    """LeetCode 421: Uses bit counting concepts"""
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

## 🔍 Optimization Techniques

### 1. Memoization for Repeated Calls
```python
from functools import lru_cache

@lru_cache(maxsize=None)
def count_bits_memo(n):
    if n == 0: return 0
    return count_bits_memo(n & (n-1)) + 1
```

### 2. Bit Manipulation Shortcuts
```python
# Check if exactly one bit is set
def is_single_bit(n):
    return n > 0 and count_bits_kernighan(n) == 1
    # Or: return n > 0 and (n & (n-1)) == 0

# Check if at most one bit is set
def at_most_one_bit(n):
    return (n & (n-1)) == 0
```

### 3. SIMD Instructions (Assembly/C++)
```cpp
// Using GCC built-ins
int count_bits_simd(uint32_t n) {
    return __builtin_popcount(n);
}

// Using Intel intrinsics
#include <immintrin.h>
int count_bits_intel(uint32_t n) {
    return _mm_popcnt_u32(n);
}
```

## 📝 Practice Problems

### Easy Level:
1. **Power of Two**: Check if number is power of 2
2. **Number Complement**: Find complement of number
3. **Hamming Distance**: Distance between two integers

### Medium Level:
1. **Counting Bits**: Count bits for range [0,n]
2. **Single Number III**: Find two unique numbers
3. **Binary Watch**: Generate valid times

### Hard Level:
1. **Maximum XOR**: Find maximum XOR in array
2. **Minimum Flips**: Make a OR b equal to c
3. **Stone Game IX**: Complex bit counting strategy

## 🎯 Key Takeaways

1. **Brian Kernighan's algorithm** is most commonly used in interviews
2. **DP approach** for counting bits in ranges is very efficient
3. **Bit manipulation** often provides O(1) or O(log n) solutions
4. **XOR properties** are crucial for many counting problems
5. **Built-in functions** are acceptable in most interviews but know the underlying algorithm

## 🚀 Interview Tips

- Always mention time/space complexity
- Explain the bit manipulation trick you're using
- Consider edge cases (n=0, negative numbers)
- Know multiple approaches and their trade-offs
- Practice mental binary arithmetic for small numbers