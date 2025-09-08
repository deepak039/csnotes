# Common Bit Manipulation Tricks

## 🎯 Essential Tricks Every Developer Should Know

### 1. Check if Number is Even/Odd
```python
# Instead of n % 2
def is_even(n):
    return (n & 1) == 0

def is_odd(n):
    return (n & 1) == 1

# Why it works: LSB is 0 for even, 1 for odd
```

### 2. Multiply/Divide by Powers of 2
```python
# Multiply by 2^k
def multiply_by_power_of_2(n, k):
    return n << k

# Divide by 2^k (for positive numbers)
def divide_by_power_of_2(n, k):
    return n >> k

# Examples:
# 5 * 8 = 5 << 3 = 40
# 32 / 4 = 32 >> 2 = 8
```

### 3. Swap Two Numbers Without Temp Variable
```python
def swap_xor(a, b):
    a = a ^ b
    b = a ^ b  # b = (a^b) ^ b = a
    a = a ^ b  # a = (a^b) ^ a = b
    return a, b

# Alternative: Arithmetic (can overflow)
def swap_arithmetic(a, b):
    a = a + b
    b = a - b  # b = (a+b) - b = a
    a = a - b  # a = (a+b) - a = b
    return a, b
```

### 4. Find Absolute Value
```python
def abs_value(n):
    # Get sign mask (all 1s if negative, all 0s if positive)
    mask = n >> 31  # Assumes 32-bit integers
    return (n + mask) ^ mask

# For negative: mask = -1, result = (n-1) ^ -1 = ~(n-1) = -n
# For positive: mask = 0, result = n ^ 0 = n
```

### 5. Check if Two Numbers Have Same Sign
```python
def same_sign(a, b):
    return (a ^ b) >= 0

# XOR of same signs gives positive, different signs gives negative
```

### 6. Turn Off Rightmost Set Bit
```python
def turn_off_rightmost_set_bit(n):
    return n & (n - 1)

# Example: 12 (1100) → 8 (1000)
# Works because n-1 flips all bits after rightmost set bit
```

### 7. Isolate Rightmost Set Bit
```python
def isolate_rightmost_set_bit(n):
    return n & (-n)

# Example: 12 (1100) → 4 (0100)
# -n in two's complement flips bits and adds 1
```

### 8. Check if Power of 2
```python
def is_power_of_2(n):
    return n > 0 and (n & (n - 1)) == 0

# Power of 2 has exactly one bit set
# n & (n-1) removes that bit, result should be 0
```

### 9. Count Set Bits (Brian Kernighan's Algorithm)
```python
def count_set_bits(n):
    count = 0
    while n:
        n &= n - 1  # Remove rightmost set bit
        count += 1
    return count

# Time: O(number of set bits) instead of O(log n)
```

### 10. Find Next Power of 2
```python
def next_power_of_2(n):
    n -= 1
    n |= n >> 1
    n |= n >> 2
    n |= n >> 4
    n |= n >> 8
    n |= n >> 16
    return n + 1

# Fills all bits after MSB, then adds 1
```

## 🧮 Mathematical Tricks

### 11. Fast Modulo for Powers of 2
```python
def fast_modulo(n, power_of_2):
    return n & (power_of_2 - 1)

# n % 8 = n & 7
# n % 16 = n & 15
# Works because 2^k - 1 has k trailing 1s
```

### 12. Average Without Overflow
```python
def average_no_overflow(a, b):
    return (a & b) + ((a ^ b) >> 1)

# Avoids (a + b) / 2 which might overflow
# Uses: a + b = 2*(a & b) + (a ^ b)
```

### 13. Min/Max Without Branching
```python
def min_no_branch(a, b):
    return b ^ ((a ^ b) & -(a < b))

def max_no_branch(a, b):
    return a ^ ((a ^ b) & -(a < b))

# Uses the fact that -(True) = -1 (all 1s), -(False) = 0
```

### 14. Sign of Integer
```python
def sign(n):
    return (n >> 31) | ((-n) >> 31)

# Returns -1 for negative, 0 for zero, 1 for positive
```

## 🎨 Advanced Tricks

### 15. Reverse Bits in O(log n)
```python
def reverse_bits_fast(n):
    # For 32-bit integer
    n = ((n & 0xAAAAAAAA) >> 1) | ((n & 0x55555555) << 1)
    n = ((n & 0xCCCCCCCC) >> 2) | ((n & 0x33333333) << 2)
    n = ((n & 0xF0F0F0F0) >> 4) | ((n & 0x0F0F0F0F) << 4)
    n = ((n & 0xFF00FF00) >> 8) | ((n & 0x00FF00FF) << 8)
    n = (n >> 16) | (n << 16)
    return n
```

### 16. Count Leading Zeros
```python
def count_leading_zeros(n):
    if n == 0: return 32
    count = 0
    if n <= 0x0000FFFF: count += 16; n <<= 16
    if n <= 0x00FFFFFF: count += 8;  n <<= 8
    if n <= 0x0FFFFFFF: count += 4;  n <<= 4
    if n <= 0x3FFFFFFF: count += 2;  n <<= 2
    if n <= 0x7FFFFFFF: count += 1
    return count
```

### 17. Gray Code Generation
```python
def binary_to_gray(n):
    return n ^ (n >> 1)

def gray_to_binary(n):
    mask = n
    while mask:
        mask >>= 1
        n ^= mask
    return n
```

### 18. Bit Interleaving (Morton Code)
```python
def interleave_bits(x, y):
    # Interleave bits of x and y
    result = 0
    for i in range(16):  # For 16-bit numbers
        result |= (x & (1 << i)) << i
        result |= (y & (1 << i)) << (i + 1)
    return result
```

## 🎯 Problem-Solving Patterns

### Pattern 1: XOR for Finding Unique Elements
```python
# Find single number in array where all others appear twice
def single_number(nums):
    result = 0
    for num in nums:
        result ^= num
    return result
```

### Pattern 2: Bit Masking for Subsets
```python
# Generate all subsets using bit masking
def generate_subsets(arr):
    n = len(arr)
    subsets = []
    for mask in range(1 << n):  # 2^n possibilities
        subset = []
        for i in range(n):
            if mask & (1 << i):
                subset.append(arr[i])
        subsets.append(subset)
    return subsets
```

### Pattern 3: Bit Manipulation for Optimization
```python
# Check if string has all unique characters
def has_unique_chars(s):
    checker = 0
    for char in s:
        val = ord(char) - ord('a')
        if checker & (1 << val):
            return False
        checker |= (1 << val)
    return True
```

## 📊 Performance Comparison

| Operation | Standard | Bit Manipulation | Speedup |
|-----------|----------|------------------|---------|
| Check even | `n % 2 == 0` | `(n & 1) == 0` | ~2x |
| Multiply by 8 | `n * 8` | `n << 3` | ~3x |
| Divide by 4 | `n / 4` | `n >> 2` | ~2x |
| Power of 2 check | Loop/Math | `n & (n-1) == 0` | ~10x |

## 🚨 Common Pitfalls

1. **Signed vs Unsigned**: Right shift behavior differs
2. **Integer Size**: Tricks assume specific bit width
3. **Overflow**: Left shifts can cause overflow
4. **Readability**: Bit tricks can reduce code clarity
5. **Portability**: Some tricks are platform-dependent

## 🎯 Interview Applications

### When to Use Bit Manipulation:
- **Space optimization**: Representing multiple boolean flags
- **Performance critical**: Low-level optimizations
- **Mathematical problems**: Powers of 2, counting, etc.
- **Cryptography**: XOR operations, bit shuffling
- **Graphics**: Color manipulation, pixel operations

### Red Flags (When NOT to Use):
- **Premature optimization**: Readability matters more
- **Complex logic**: Bit tricks can obscure intent
- **Floating point**: Bit manipulation doesn't apply
- **Large numbers**: Beyond integer limits

## 📝 Quick Reference Card

```python
# Essential one-liners
n & 1                    # Check if odd
n & (n-1)               # Clear rightmost set bit
n & (-n)                # Isolate rightmost set bit
n | (1 << i)            # Set bit i
n & ~(1 << i)           # Clear bit i
n ^ (1 << i)            # Toggle bit i
(n >> i) & 1            # Get bit i
n << k                  # Multiply by 2^k
n >> k                  # Divide by 2^k
n & (n-1) == 0          # Check if power of 2
a ^ b ^ a               # Equals b (XOR swap)
```

## 📚 Practice Problems

1. **Bit Flipping**: Flip bits to convert A to B
2. **Missing Number**: Find missing number using XOR
3. **Power of Four**: Check if number is power of 4
4. **Bit Insertion**: Insert M into N at positions i-j

These tricks form the foundation for solving complex bit manipulation problems efficiently!