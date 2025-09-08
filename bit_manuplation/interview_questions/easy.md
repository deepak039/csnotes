# Easy Bit Manipulation Interview Questions

## 🎯 Level: Easy (Beginner Friendly)

These problems test basic understanding of bit operations and common tricks.

---

## 1. Number of 1 Bits (LeetCode 191) ⭐

**Problem**: Count the number of set bits in an integer.

```python
def hamming_weight(n):
    """
    Time: O(k) where k = number of set bits
    Space: O(1)
    """
    count = 0
    while n:
        n &= n - 1  # Brian Kernighan's algorithm
        count += 1
    return count

# Alternative approaches:
def hamming_weight_naive(n):
    count = 0
    while n:
        count += n & 1
        n >>= 1
    return count

def hamming_weight_builtin(n):
    return bin(n).count('1')
```

**Key Insight**: `n & (n-1)` removes the rightmost set bit.

---

## 2. Power of Two (LeetCode 231) ⭐

**Problem**: Check if a number is a power of 2.

```python
def is_power_of_two(n):
    """
    Time: O(1), Space: O(1)
    Power of 2 has exactly one bit set
    """
    return n > 0 and (n & (n - 1)) == 0

# Alternative:
def is_power_of_two_alt(n):
    return n > 0 and (n & -n) == n
```

**Key Insight**: Powers of 2 have exactly one bit set in binary.

---

## 3. Single Number (LeetCode 136) ⭐

**Problem**: Find the number that appears once, all others appear twice.

```python
def single_number(nums):
    """
    Time: O(n), Space: O(1)
    XOR cancels out duplicates
    """
    result = 0
    for num in nums:
        result ^= num
    return result
```

**Key Insight**: `a ^ a = 0` and `a ^ 0 = a`

---

## 4. Missing Number (LeetCode 268) ⭐

**Problem**: Find the missing number in array containing n distinct numbers from 0 to n.

```python
def missing_number(nums):
    """Method 1: XOR approach"""
    n = len(nums)
    result = n
    
    for i, num in enumerate(nums):
        result ^= i ^ num
    
    return result

def missing_number_sum(nums):
    """Method 2: Sum approach"""
    n = len(nums)
    expected_sum = n * (n + 1) // 2
    actual_sum = sum(nums)
    return expected_sum - actual_sum

def missing_number_bit(nums):
    """Method 3: Bit manipulation"""
    result = 0
    for i in range(len(nums) + 1):
        result ^= i
    for num in nums:
        result ^= num
    return result
```

---

## 5. Reverse Bits (LeetCode 190)

**Problem**: Reverse bits of a 32-bit unsigned integer.

```python
def reverse_bits(n):
    """
    Time: O(32) = O(1), Space: O(1)
    """
    result = 0
    for i in range(32):
        result = (result << 1) | (n & 1)
        n >>= 1
    return result

def reverse_bits_optimized(n):
    """Optimized version using bit manipulation tricks"""
    # Swap adjacent bits
    n = ((n & 0xAAAAAAAA) >> 1) | ((n & 0x55555555) << 1)
    # Swap adjacent pairs
    n = ((n & 0xCCCCCCCC) >> 2) | ((n & 0x33333333) << 2)
    # Swap adjacent nibbles
    n = ((n & 0xF0F0F0F0) >> 4) | ((n & 0x0F0F0F0F) << 4)
    # Swap adjacent bytes
    n = ((n & 0xFF00FF00) >> 8) | ((n & 0x00FF00FF) << 8)
    # Swap adjacent 16-bit blocks
    n = (n >> 16) | (n << 16)
    return n
```

---

## 6. Number Complement (LeetCode 476)

**Problem**: Find the complement of a positive integer.

```python
def find_complement(num):
    """
    Time: O(log n), Space: O(1)
    """
    # Find the bit length
    bit_length = num.bit_length()
    
    # Create mask with all 1s for the bit length
    mask = (1 << bit_length) - 1
    
    # XOR with mask to flip all bits
    return num ^ mask

def find_complement_alt(num):
    """Alternative approach"""
    mask = 1
    temp = num
    
    # Create mask with same number of bits as num
    while temp:
        mask <<= 1
        temp >>= 1
    
    return (mask - 1) ^ num
```

---

## 7. Hamming Distance (LeetCode 461)

**Problem**: Find the Hamming distance between two integers (number of different bits).

```python
def hamming_distance(x, y):
    """
    Time: O(log n), Space: O(1)
    """
    xor = x ^ y
    count = 0
    
    while xor:
        count += 1
        xor &= xor - 1  # Remove rightmost set bit
    
    return count

def hamming_distance_builtin(x, y):
    """Using built-in function"""
    return bin(x ^ y).count('1')
```

---

## 8. Binary Number with Alternating Bits (LeetCode 693)

**Problem**: Check if a binary number has alternating bits.

```python
def has_alternating_bits(n):
    """
    Time: O(1), Space: O(1)
    """
    # XOR with right-shifted version
    xor = n ^ (n >> 1)
    
    # If alternating, XOR should have all 1s
    # Check if XOR is of form 111...1
    return (xor & (xor + 1)) == 0

def has_alternating_bits_simple(n):
    """Simple approach"""
    prev_bit = n & 1
    n >>= 1
    
    while n:
        curr_bit = n & 1
        if curr_bit == prev_bit:
            return False
        prev_bit = curr_bit
        n >>= 1
    
    return True
```

---

## 9. Add Binary (LeetCode 67)

**Problem**: Add two binary strings and return their sum as a binary string.

```python
def add_binary(a, b):
    """
    Time: O(max(len(a), len(b))), Space: O(max(len(a), len(b)))
    """
    result = []
    carry = 0
    i, j = len(a) - 1, len(b) - 1
    
    while i >= 0 or j >= 0 or carry:
        total = carry
        
        if i >= 0:
            total += int(a[i])
            i -= 1
        
        if j >= 0:
            total += int(b[j])
            j -= 1
        
        result.append(str(total % 2))
        carry = total // 2
    
    return ''.join(reversed(result))

def add_binary_builtin(a, b):
    """Using built-in functions (not recommended for interviews)"""
    return bin(int(a, 2) + int(b, 2))[2:]
```

---

## 10. Convert to Base -2 (LeetCode 1017)

**Problem**: Convert integer to base -2 representation.

```python
def base_neg2(n):
    """
    Time: O(log n), Space: O(log n)
    """
    if n == 0:
        return "0"
    
    result = []
    
    while n:
        if n % 2:
            result.append('1')
            n = (n - 1) // (-2)
        else:
            result.append('0')
            n //= (-2)
    
    return ''.join(reversed(result))
```

---

## 🎯 Common Patterns in Easy Problems

### Pattern 1: Basic Bit Operations
```python
# Check if bit is set
def is_bit_set(num, pos):
    return (num & (1 << pos)) != 0

# Set a bit
def set_bit(num, pos):
    return num | (1 << pos)

# Clear a bit
def clear_bit(num, pos):
    return num & ~(1 << pos)

# Toggle a bit
def toggle_bit(num, pos):
    return num ^ (1 << pos)
```

### Pattern 2: XOR Properties
```python
# Self-cancellation: a ^ a = 0
# Identity: a ^ 0 = a
# Commutative: a ^ b = b ^ a
# Find unique element in duplicates
def find_unique(nums):
    result = 0
    for num in nums:
        result ^= num
    return result
```

### Pattern 3: Power of 2 Detection
```python
# Power of 2 has exactly one bit set
def is_power_of_two(n):
    return n > 0 and (n & (n - 1)) == 0

# Get next power of 2
def next_power_of_two(n):
    if n <= 1: return 1
    return 1 << (n - 1).bit_length()
```

---

## 📝 Quick Reference for Easy Problems

| Problem | Key Technique | Time | Space |
|---------|---------------|------|-------|
| Count 1s | `n & (n-1)` removes rightmost 1 | O(k) | O(1) |
| Power of 2 | `n & (n-1) == 0` | O(1) | O(1) |
| Single Number | XOR cancellation | O(n) | O(1) |
| Missing Number | XOR or sum formula | O(n) | O(1) |
| Reverse Bits | Shift and build result | O(32) | O(1) |
| Complement | XOR with mask | O(log n) | O(1) |
| Hamming Distance | Count 1s in XOR | O(log n) | O(1) |
| Alternating Bits | XOR with shifted version | O(1) | O(1) |

---

## 🚀 Interview Tips for Easy Problems

### Before Coding:
1. **Understand the problem** - What exactly is being asked?
2. **Think of edge cases** - Zero, negative numbers, empty input
3. **Choose the right approach** - Bit manipulation vs. other methods
4. **Explain your approach** - Why bit manipulation is suitable

### While Coding:
1. **Start with brute force** if bit manipulation isn't obvious
2. **Use meaningful variable names** - `mask`, `bit_pos`, etc.
3. **Add comments** for complex bit operations
4. **Test with small examples** - Trace through manually

### Common Mistakes:
1. **Operator precedence** - Use parentheses: `(n & 1) == 0`
2. **Off-by-one errors** - Remember 0-indexing for bit positions
3. **Integer overflow** - Be careful with left shifts
4. **Negative numbers** - Consider two's complement representation

### Optimization Opportunities:
1. **Built-in functions** - Know them but implement manually first
2. **Bit tricks** - Learn common patterns like `n & (n-1)`
3. **Early termination** - Stop when no more bits to process
4. **Constant time operations** - Prefer O(1) over O(log n) when possible

---

## 📚 Practice Strategy

1. **Master the basics** - Understand binary representation thoroughly
2. **Learn key tricks** - `n & (n-1)`, `n & -n`, XOR properties
3. **Practice mental math** - Convert small numbers to binary quickly
4. **Solve systematically** - Don't jump to advanced problems too early
5. **Time yourself** - Easy problems should be solved in 10-15 minutes

Remember: Easy bit manipulation problems are about recognizing patterns and applying basic bit operations correctly!