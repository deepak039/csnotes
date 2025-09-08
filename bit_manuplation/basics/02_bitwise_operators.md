# Bitwise Operators

## 🔧 Core Operators

### AND (&)
- Result is 1 only when both bits are 1
- **Use cases**: Masking, checking specific bits

```
  1010  (10)
& 1100  (12)
------
  1000  (8)

Truth table:
0 & 0 = 0    1 & 0 = 0
0 & 1 = 0    1 & 1 = 1
```

### OR (|)
- Result is 1 when at least one bit is 1
- **Use cases**: Setting bits, combining flags

```
  1010  (10)
| 1100  (12)
------
  1110  (14)

Truth table:
0 | 0 = 0    1 | 0 = 1
0 | 1 = 1    1 | 1 = 1
```

### XOR (^)
- Result is 1 when bits are different
- **Use cases**: Toggling, finding differences, encryption

```
  1010  (10)
^ 1100  (12)
------
  0110  (6)

Truth table:
0 ^ 0 = 0    1 ^ 0 = 1
0 ^ 1 = 1    1 ^ 1 = 0
```

### NOT (~)
- Flips all bits (1s complement)
- **Use cases**: Creating masks, negation

```
~1010 = 0101 (in 4-bit representation)
Note: In most systems, ~10 = -11 due to two's complement
```

## ↔️ Shift Operators

### Left Shift (<<)
- Shifts bits left, fills with 0s
- **Effect**: Multiplies by 2ⁿ (where n is shift amount)

```
5 << 2:
  0101  (5)
<<   2
------
  10100 (20)  // 5 × 2² = 20
```

### Right Shift (>>)
- Shifts bits right
- **Arithmetic shift**: Preserves sign (fills with sign bit)
- **Logical shift**: Fills with 0s

```
20 >> 2:
  10100 (20)
>>    2
------
  00101 (5)   // 20 ÷ 2² = 5
```

## 🎯 Operator Properties

### XOR Properties (Most Important!)
```python
a ^ a = 0        # Self-cancellation
a ^ 0 = a        # Identity
a ^ b = b ^ a    # Commutative
(a ^ b) ^ c = a ^ (b ^ c)  # Associative
```

### AND Properties
```python
a & a = a        # Idempotent
a & 0 = 0        # Zero property
a & 1 = a        # Identity (for single bit)
a & (2ⁿ - 1) = a % 2ⁿ  # Modulo operation
```

### OR Properties
```python
a | a = a        # Idempotent
a | 0 = a        # Identity
a | (2ⁿ - 1) = 2ⁿ - 1  # All bits set
```

## 💡 Common Patterns

### Check if bit is set
```python
def is_bit_set(num, pos):
    return (num & (1 << pos)) != 0
```

### Set a bit
```python
def set_bit(num, pos):
    return num | (1 << pos)
```

### Clear a bit
```python
def clear_bit(num, pos):
    return num & ~(1 << pos)
```

### Toggle a bit
```python
def toggle_bit(num, pos):
    return num ^ (1 << pos)
```

### Get rightmost set bit
```python
def rightmost_set_bit(num):
    return num & (-num)
```

## 🧮 Quick Calculations

### Multiplication/Division by Powers of 2
```python
n * 2ᵏ = n << k    # Multiply by 2^k
n / 2ᵏ = n >> k    # Divide by 2^k (for positive numbers)

# Examples:
15 * 4 = 15 << 2 = 60
32 / 8 = 32 >> 3 = 4
```

### Modulo with Powers of 2
```python
n % 2ᵏ = n & (2ᵏ - 1)

# Examples:
17 % 8 = 17 & 7 = 1
25 % 16 = 25 & 15 = 9
```

## 🎯 Interview Examples

### Q1: Swap two numbers without temporary variable
```python
def swap(a, b):
    a = a ^ b
    b = a ^ b  # b = (a^b) ^ b = a
    a = a ^ b  # a = (a^b) ^ a = b
    return a, b
```

### Q2: Check if number has alternating bits
```python
def has_alternating_bits(n):
    xor = n ^ (n >> 1)
    return (xor & (xor + 1)) == 0
```

### Q3: Find the only non-duplicate number
```python
def single_number(nums):
    result = 0
    for num in nums:
        result ^= num
    return result
```

## 📊 Operator Precedence (High to Low)
1. `~` (NOT)
2. `<<`, `>>` (Shifts)
3. `&` (AND)
4. `^` (XOR)
5. `|` (OR)

## 🚨 Common Pitfalls
- **Operator precedence**: Use parentheses! `a & b == 0` vs `(a & b) == 0`
- **Signed shifts**: Right shift of negative numbers is implementation-dependent
- **Overflow**: Left shifting can cause overflow
- **Bit width**: Results depend on integer size (32-bit vs 64-bit)

## 📝 Practice Problems
1. What is `13 & 7`?
2. Calculate `5 ^ 3 ^ 5`
3. How to check if a number is even using bitwise operators?
4. Set the 3rd bit of 10 (0-indexed)

**Answers:**
1. 5 (1101 & 0111 = 0101)
2. 3 (XOR is associative, 5^5=0, 0^3=3)
3. `(n & 1) == 0`
4. `10 | (1 << 3) = 10 | 8 = 18`