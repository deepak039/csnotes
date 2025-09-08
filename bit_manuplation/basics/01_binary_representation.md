# Binary Representation Fundamentals

## 🔢 Number Systems

### Decimal to Binary Conversion
```
Decimal 13 → Binary 1101
13 ÷ 2 = 6 remainder 1
6 ÷ 2 = 3 remainder 0  
3 ÷ 2 = 1 remainder 1
1 ÷ 2 = 0 remainder 1
Read remainders bottom to top: 1101
```

### Binary to Decimal Conversion
```
Binary 1101 → Decimal
1×2³ + 1×2² + 0×2¹ + 1×2⁰ = 8 + 4 + 0 + 1 = 13
```

## 📊 Bit Positions and Values

```
Bit Position:  7  6  5  4  3  2  1  0
Bit Value:   128 64 32 16  8  4  2  1
Binary:        1  0  1  1  0  1  0  1  = 181
```

## 🔍 Signed vs Unsigned Integers

### Unsigned (8-bit)
- Range: 0 to 255
- All bits represent magnitude

### Signed - Two's Complement (8-bit)
- Range: -128 to 127
- MSB is sign bit (0=positive, 1=negative)
- Negative numbers: flip all bits and add 1

```python
# Example: -5 in 8-bit two's complement
5 in binary:     00000101
Flip all bits:   11111010
Add 1:           11111011  = -5
```

## 💡 Key Concepts

### Bit Indexing
- **0-indexed from right**: Bit 0 is rightmost (LSB)
- **MSB**: Most Significant Bit (leftmost)
- **LSB**: Least Significant Bit (rightmost)

### Common Bit Patterns
```
Powers of 2:
1 = 0001    2 = 0010    4 = 0100    8 = 1000

All 1s (n bits): 2ⁿ - 1
4 bits: 1111 = 15
8 bits: 11111111 = 255
```

## 🧮 Mental Math Tricks

### Quick Powers of 2
```
2⁰ = 1      2⁴ = 16     2⁸ = 256
2¹ = 2      2⁵ = 32     2⁹ = 512
2² = 4      2⁶ = 64     2¹⁰ = 1024
2³ = 8      2⁷ = 128    2¹¹ = 2048
```

### Binary Addition
```
  1011  (11)
+ 1101  (13)
------
 11000  (24)

Rules: 0+0=0, 0+1=1, 1+0=1, 1+1=10 (carry 1)
```

## 🎯 Interview Questions

### Q1: Convert decimal to binary without built-in functions
```python
def decimal_to_binary(n):
    if n == 0: return "0"
    result = ""
    while n > 0:
        result = str(n % 2) + result
        n //= 2
    return result
```

### Q2: Count number of 1s in binary representation
```python
def count_ones(n):
    count = 0
    while n:
        count += n & 1
        n >>= 1
    return count
```

### Q3: Check if number is power of 2
```python
def is_power_of_two(n):
    return n > 0 and (n & (n - 1)) == 0
```

## 📝 Practice Problems
1. Convert 42 to binary
2. What's the decimal value of 10110101?
3. Find the 4th bit (0-indexed) of 157
4. How many bits needed to represent 1000?

**Answers:**
1. 101010
2. 181
3. Bit 4 of 157 (10011101) is 1
4. 10 bits (2⁹ = 512 < 1000 < 1024 = 2¹⁰)