# Basic Bit Operations

## 🎯 Essential Operations

### 1. Check if Bit is Set
```python
def is_bit_set(num, pos):
    """Check if bit at position pos is set (1)"""
    return (num & (1 << pos)) != 0

# Example: Check if 3rd bit of 13 is set
# 13 = 1101, 3rd bit (from right, 0-indexed) is 1
print(is_bit_set(13, 3))  # True
```

### 2. Set a Bit
```python
def set_bit(num, pos):
    """Set bit at position pos to 1"""
    return num | (1 << pos)

# Example: Set 1st bit of 10
# 10 = 1010 → 1011 = 11
print(set_bit(10, 0))  # 11
```

### 3. Clear a Bit
```python
def clear_bit(num, pos):
    """Set bit at position pos to 0"""
    return num & ~(1 << pos)

# Example: Clear 3rd bit of 15
# 15 = 1111 → 0111 = 7
print(clear_bit(15, 3))  # 7
```

### 4. Toggle a Bit
```python
def toggle_bit(num, pos):
    """Flip bit at position pos"""
    return num ^ (1 << pos)

# Example: Toggle 2nd bit of 10
# 10 = 1010 → 1110 = 14
print(toggle_bit(10, 2))  # 14
```

### 5. Update a Bit
```python
def update_bit(num, pos, bit_value):
    """Set bit at pos to bit_value (0 or 1)"""
    mask = ~(1 << pos)  # Clear the bit
    return (num & mask) | (bit_value << pos)

# Example: Set 2nd bit of 10 to 0
print(update_bit(10, 1, 0))  # 8 (1010 → 1000)
```

## 🔍 Bit Analysis Operations

### 6. Get Rightmost Set Bit
```python
def rightmost_set_bit(num):
    """Get the rightmost set bit"""
    return num & (-num)

# Example: 12 = 1100, rightmost set bit is at position 2
print(rightmost_set_bit(12))  # 4 (which is 2^2)
```

### 7. Clear Rightmost Set Bit
```python
def clear_rightmost_set_bit(num):
    """Clear the rightmost set bit"""
    return num & (num - 1)

# Example: 12 = 1100 → 1000 = 8
print(clear_rightmost_set_bit(12))  # 8
```

### 8. Check if Power of 2
```python
def is_power_of_two(num):
    """Check if number is power of 2"""
    return num > 0 and (num & (num - 1)) == 0

# Powers of 2 have exactly one bit set
print(is_power_of_two(16))  # True (10000)
print(is_power_of_two(18))  # False (10010)
```

### 9. Count Set Bits (Hamming Weight)
```python
def count_set_bits(num):
    """Count number of 1s in binary representation"""
    count = 0
    while num:
        count += 1
        num &= num - 1  # Clear rightmost set bit
    return count

# Alternative: Built-in method
def count_set_bits_builtin(num):
    return bin(num).count('1')

print(count_set_bits(13))  # 3 (1101 has three 1s)
```

### 10. Get Bit at Position
```python
def get_bit(num, pos):
    """Get the bit value at position pos"""
    return (num >> pos) & 1

print(get_bit(13, 2))  # 1 (13 = 1101, bit 2 is 1)
```

## 🎨 Advanced Basic Operations

### 11. Isolate Rightmost 0 Bit
```python
def rightmost_zero_bit(num):
    """Get position of rightmost 0 bit"""
    return ~num & (num + 1)

print(rightmost_zero_bit(13))  # 2 (13 = 1101, rightmost 0 is at pos 1)
```

### 12. Set All Bits in Range
```python
def set_bits_in_range(num, start, end):
    """Set all bits from start to end (inclusive)"""
    mask = ((1 << (end - start + 1)) - 1) << start
    return num | mask

# Set bits 1 to 3 in number 8 (1000)
print(set_bits_in_range(8, 1, 3))  # 14 (1110)
```

### 13. Clear All Bits in Range
```python
def clear_bits_in_range(num, start, end):
    """Clear all bits from start to end (inclusive)"""
    mask = ((1 << (end - start + 1)) - 1) << start
    return num & ~mask

# Clear bits 1 to 2 in number 15 (1111)
print(clear_bits_in_range(15, 1, 2))  # 9 (1001)
```

### 14. Extract Bits in Range
```python
def extract_bits(num, start, length):
    """Extract 'length' bits starting from 'start' position"""
    mask = (1 << length) - 1
    return (num >> start) & mask

# Extract 3 bits starting from position 1 in 13 (1101)
print(extract_bits(13, 1, 3))  # 6 (110 in binary)
```

## 🧮 Practical Applications

### 15. Check if Numbers Have Opposite Signs
```python
def opposite_signs(a, b):
    """Check if two numbers have opposite signs"""
    return (a ^ b) < 0

print(opposite_signs(5, -3))   # True
print(opposite_signs(5, 3))    # False
```

### 16. Find Missing Number (1 to n)
```python
def find_missing_number(nums, n):
    """Find missing number in array of 1 to n"""
    xor_all = 0
    xor_nums = 0
    
    for i in range(1, n + 1):
        xor_all ^= i
    
    for num in nums:
        xor_nums ^= num
    
    return xor_all ^ xor_nums

# Example: [1,2,4,5] missing 3
print(find_missing_number([1,2,4,5], 5))  # 3
```

### 17. Reverse Bits
```python
def reverse_bits(num, bit_length=32):
    """Reverse bits of a number"""
    result = 0
    for i in range(bit_length):
        if num & (1 << i):
            result |= 1 << (bit_length - 1 - i)
    return result

# Reverse bits of 5 (101) in 8-bit: 10100000 = 160
print(reverse_bits(5, 8))  # 160
```

## 📊 Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Set/Clear/Toggle bit | O(1) | O(1) | Direct bit manipulation |
| Count set bits | O(k) | O(1) | k = number of set bits |
| Check power of 2 | O(1) | O(1) | Single operation |
| Reverse bits | O(n) | O(1) | n = bit length |

## 🎯 Interview Tips

### Common Mistakes to Avoid:
1. **Off-by-one errors**: Remember 0-indexing
2. **Operator precedence**: Use parentheses
3. **Sign extension**: Be careful with negative numbers
4. **Integer overflow**: Consider bit width limits

### Key Insights:
- `n & (n-1)` clears rightmost set bit
- `n & (-n)` isolates rightmost set bit  
- `n ^ n = 0` and `n ^ 0 = n`
- Powers of 2 have exactly one bit set

## 📝 Practice Problems

1. **Easy**: Check if 7th bit of 200 is set
2. **Easy**: Set the 4th bit of 25
3. **Medium**: Count trailing zeros in binary representation
4. **Medium**: Find position of rightmost set bit

**Solutions:**
```python
# 1. Check 7th bit of 200
print(is_bit_set(200, 7))  # True (200 = 11001000)

# 2. Set 4th bit of 25  
print(set_bit(25, 4))      # 41 (25 = 11001 → 111001)

# 3. Count trailing zeros
def count_trailing_zeros(n):
    if n == 0: return 32  # or bit width
    count = 0
    while (n & 1) == 0:
        count += 1
        n >>= 1
    return count

# 4. Position of rightmost set bit
def rightmost_set_bit_pos(n):
    return (n & -n).bit_length() - 1
```