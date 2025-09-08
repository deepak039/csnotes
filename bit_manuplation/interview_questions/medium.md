# Medium Bit Manipulation Interview Questions

## 🎯 Level: Medium (Intermediate Concepts)

These problems require deeper understanding of bit manipulation patterns and combinations of techniques.

---

## 1. Single Number II (LeetCode 137) ⭐⭐

**Problem**: Find the number that appears once, all others appear exactly 3 times.

```python
def single_number_ii(nums):
    """
    Time: O(n), Space: O(1)
    Use two variables to track bit states
    """
    ones = twos = 0
    
    for num in nums:
        # Update twos first (before ones changes)
        twos = twos ^ (ones & num)
        # Update ones
        ones = ones ^ num
        # Remove bits that appear 3 times
        threes = ones & twos
        ones &= ~threes
        twos &= ~threes
    
    return ones

def single_number_ii_counting(nums):
    """Alternative: Count bits at each position"""
    result = 0
    for i in range(32):
        bit_count = sum((num >> i) & 1 for num in nums)
        if bit_count % 3 == 1:
            result |= (1 << i)
    
    # Handle negative numbers in Python
    if result >= 2**31:
        result -= 2**32
    return result
```

**Key Insight**: Use state machine to track bit occurrences: 00 → 01 → 10 → 00

---

## 2. Single Number III (LeetCode 260) ⭐⭐

**Problem**: Find two numbers that appear once, all others appear exactly twice.

```python
def single_number_iii(nums):
    """
    Time: O(n), Space: O(1)
    Use XOR to separate the two unique numbers
    """
    # Step 1: XOR all numbers
    xor_all = 0
    for num in nums:
        xor_all ^= num
    
    # Step 2: Find rightmost set bit (where numbers differ)
    rightmost_bit = xor_all & (-xor_all)
    
    # Step 3: Divide numbers into two groups
    num1 = num2 = 0
    for num in nums:
        if num & rightmost_bit:
            num1 ^= num
        else:
            num2 ^= num
    
    return [num1, num2]
```

**Key Insight**: Use the rightmost differing bit to separate the two unique numbers.

---

## 3. Counting Bits (LeetCode 338) ⭐⭐

**Problem**: Count set bits for all numbers from 0 to n.

```python
def counting_bits(n):
    """
    Time: O(n), Space: O(n)
    DP approach using bit manipulation
    """
    dp = [0] * (n + 1)
    
    for i in range(1, n + 1):
        # dp[i] = dp[i >> 1] + (i & 1)
        # Right shift removes LSB, add 1 if LSB was set
        dp[i] = dp[i >> 1] + (i & 1)
    
    return dp

def counting_bits_kernighan(n):
    """Alternative using Brian Kernighan's algorithm"""
    dp = [0] * (n + 1)
    
    for i in range(1, n + 1):
        # dp[i] = dp[i & (i-1)] + 1
        # i & (i-1) removes rightmost set bit
        dp[i] = dp[i & (i - 1)] + 1
    
    return dp
```

**Key Insight**: Use previously computed results with bit manipulation patterns.

---

## 4. Bitwise AND of Numbers Range (LeetCode 201) ⭐⭐

**Problem**: Find bitwise AND of all numbers in range [left, right].

```python
def range_bitwise_and(left, right):
    """
    Time: O(log n), Space: O(1)
    Find common prefix of left and right
    """
    shift = 0
    
    # Find common prefix by removing different suffixes
    while left != right:
        left >>= 1
        right >>= 1
        shift += 1
    
    # Shift back to get the result
    return left << shift

def range_bitwise_and_alt(left, right):
    """Alternative approach"""
    while left < right:
        # Remove rightmost set bit from right
        right &= right - 1
    return right
```

**Key Insight**: AND of a range equals the common binary prefix of the endpoints.

---

## 5. Maximum XOR of Two Numbers (LeetCode 421) ⭐⭐⭐

**Problem**: Find maximum XOR of any two numbers in the array.

```python
def find_maximum_xor(nums):
    """
    Time: O(32n), Space: O(n)
    Build answer bit by bit from MSB
    """
    max_xor = 0
    mask = 0
    
    for i in range(31, -1, -1):
        mask |= (1 << i)  # Include current bit in mask
        prefixes = {num & mask for num in nums}
        
        temp = max_xor | (1 << i)  # Try to set current bit
        
        # Check if this bit can be set
        for prefix in prefixes:
            if temp ^ prefix in prefixes:
                max_xor = temp
                break
    
    return max_xor

# Using Trie for optimization
class TrieNode:
    def __init__(self):
        self.children = {}

def find_maximum_xor_trie(nums):
    """Using Trie for better understanding"""
    root = TrieNode()
    
    # Build trie
    for num in nums:
        node = root
        for i in range(31, -1, -1):
            bit = (num >> i) & 1
            if bit not in node.children:
                node.children[bit] = TrieNode()
            node = node.children[bit]
    
    max_xor = 0
    
    # Find maximum XOR for each number
    for num in nums:
        node = root
        current_xor = 0
        
        for i in range(31, -1, -1):
            bit = (num >> i) & 1
            # Try to go opposite direction for maximum XOR
            opposite_bit = 1 - bit
            
            if opposite_bit in node.children:
                current_xor |= (1 << i)
                node = node.children[opposite_bit]
            else:
                node = node.children[bit]
        
        max_xor = max(max_xor, current_xor)
    
    return max_xor
```

**Key Insight**: Build the answer bit by bit, trying to maximize each bit position.

---

## 6. Subsets (LeetCode 78) ⭐⭐

**Problem**: Generate all possible subsets of a given set.

```python
def subsets(nums):
    """
    Time: O(n × 2^n), Space: O(2^n)
    Use bit masking to generate all subsets
    """
    n = len(nums)
    result = []
    
    for mask in range(1 << n):  # 2^n possibilities
        subset = []
        for i in range(n):
            if mask & (1 << i):
                subset.append(nums[i])
        result.append(subset)
    
    return result

def subsets_recursive(nums):
    """Recursive approach for comparison"""
    def backtrack(start, path):
        result.append(path[:])
        
        for i in range(start, len(nums)):
            path.append(nums[i])
            backtrack(i + 1, path)
            path.pop()
    
    result = []
    backtrack(0, [])
    return result
```

**Key Insight**: Each bit position represents whether to include an element in the subset.

---

## 7. Gray Code (LeetCode 89) ⭐⭐

**Problem**: Generate n-bit Gray code sequence.

```python
def gray_code(n):
    """
    Time: O(2^n), Space: O(2^n)
    Generate Gray code using bit manipulation
    """
    result = []
    for i in range(1 << n):  # 2^n numbers
        # Gray code formula: i ^ (i >> 1)
        result.append(i ^ (i >> 1))
    return result

def gray_code_recursive(n):
    """Recursive approach"""
    if n == 0:
        return [0]
    
    prev_gray = gray_code_recursive(n - 1)
    result = prev_gray[:]
    
    # Add MSB to previous codes in reverse order
    for i in range(len(prev_gray) - 1, -1, -1):
        result.append(prev_gray[i] | (1 << (n - 1)))
    
    return result
```

**Key Insight**: Gray code can be generated using the formula `i ^ (i >> 1)`.

---

## 8. Repeated DNA Sequences (LeetCode 187) ⭐⭐

**Problem**: Find all 10-letter DNA sequences that occur more than once.

```python
def find_repeated_dna_sequences(s):
    """
    Time: O(n), Space: O(n)
    Use bit manipulation for efficient hashing
    """
    if len(s) < 10:
        return []
    
    # Map nucleotides to 2-bit values
    mapping = {'A': 0, 'C': 1, 'G': 2, 'T': 3}
    
    seen = set()
    repeated = set()
    hash_value = 0
    
    # Process first 10 characters
    for i in range(10):
        hash_value = (hash_value << 2) | mapping[s[i]]
    seen.add(hash_value)
    
    # Rolling hash for remaining characters
    mask = (1 << 20) - 1  # Keep only 20 bits (10 * 2)
    
    for i in range(10, len(s)):
        # Remove leftmost 2 bits and add new 2 bits
        hash_value = ((hash_value << 2) & mask) | mapping[s[i]]
        
        if hash_value in seen:
            repeated.add(s[i-9:i+1])
        else:
            seen.add(hash_value)
    
    return list(repeated)
```

**Key Insight**: Use 2 bits per nucleotide for efficient rolling hash.

---

## 9. Sum of Two Integers (LeetCode 371) ⭐⭐

**Problem**: Calculate sum of two integers without using + or - operators.

```python
def get_sum(a, b):
    """
    Time: O(1), Space: O(1)
    Use bit manipulation to simulate addition
    """
    # Handle Python's arbitrary precision integers
    mask = 0xFFFFFFFF
    
    while b != 0:
        # Calculate carry
        carry = (a & b) << 1
        # Calculate sum without carry
        a = (a ^ b) & mask
        b = carry & mask
    
    # Handle negative result
    return a if a <= 0x7FFFFFFF else ~(a ^ mask)

def get_sum_recursive(a, b):
    """Recursive approach"""
    if b == 0:
        return a
    
    # Sum without carry + carry
    return get_sum_recursive(a ^ b, (a & b) << 1)
```

**Key Insight**: Addition = XOR (sum without carry) + AND shifted left (carry).

---

## 10. UTF-8 Validation (LeetCode 393) ⭐⭐

**Problem**: Check if array of integers represents valid UTF-8 encoding.

```python
def valid_utf8(data):
    """
    Time: O(n), Space: O(1)
    Check UTF-8 encoding rules using bit manipulation
    """
    def get_byte_count(byte):
        """Get number of bytes in UTF-8 character"""
        if (byte >> 7) == 0:          # 0xxxxxxx
            return 1
        elif (byte >> 5) == 0b110:    # 110xxxxx
            return 2
        elif (byte >> 4) == 0b1110:   # 1110xxxx
            return 3
        elif (byte >> 3) == 0b11110:  # 11110xxx
            return 4
        else:
            return 0  # Invalid
    
    def is_continuation(byte):
        """Check if byte is continuation byte (10xxxxxx)"""
        return (byte >> 6) == 0b10
    
    i = 0
    while i < len(data):
        byte_count = get_byte_count(data[i] & 0xFF)
        
        if byte_count == 0:
            return False
        
        # Check continuation bytes
        for j in range(1, byte_count):
            if i + j >= len(data) or not is_continuation(data[i + j] & 0xFF):
                return False
        
        i += byte_count
    
    return True
```

**Key Insight**: Use bit patterns to identify UTF-8 character boundaries.

---

## 🎯 Advanced Medium Problems

### 11. Minimum Flips to Make a OR b Equal c (LeetCode 1318)

```python
def min_flips(a, b, c):
    """
    Time: O(log max(a,b,c)), Space: O(1)
    """
    flips = 0
    
    while a or b or c:
        bit_a, bit_b, bit_c = a & 1, b & 1, c & 1
        
        if bit_c == 0:
            # Both a and b should be 0
            flips += bit_a + bit_b
        else:
            # At least one of a or b should be 1
            if bit_a == 0 and bit_b == 0:
                flips += 1
        
        a >>= 1
        b >>= 1
        c >>= 1
    
    return flips
```

### 12. Binary Watch (LeetCode 401)

```python
def read_binary_watch(num):
    """
    Time: O(1), Space: O(1)
    Generate all valid times with exactly num LEDs
    """
    times = []
    
    for h in range(12):
        for m in range(60):
            if bin(h).count('1') + bin(m).count('1') == num:
                times.append(f"{h}:{m:02d}")
    
    return times
```

---

## 📊 Problem Patterns Summary

| Pattern | Problems | Key Technique |
|---------|----------|---------------|
| State Machine | Single Number II | Track bit states with multiple variables |
| Bit Grouping | Single Number III | Use differing bit to separate groups |
| DP with Bits | Counting Bits | Use previous results with bit operations |
| Range Operations | Bitwise AND Range | Find common prefix |
| Greedy Bit Building | Maximum XOR | Build answer bit by bit |
| Subset Generation | Subsets | Use bit mask to represent selections |
| Encoding/Hashing | DNA Sequences | Map characters to bits for efficiency |
| Arithmetic Simulation | Sum of Integers | Use XOR and AND for addition |

---

## 🎯 Problem-Solving Strategies

### 1. Identify the Pattern
- **Single elements**: Use XOR properties
- **Range operations**: Look for common prefixes/suffixes
- **Counting**: Use DP with bit manipulation
- **Maximum/Minimum**: Build answer greedily

### 2. Choose the Right Technique
- **State machines**: For complex counting (3x, 4x occurrences)
- **Bit masking**: For subset generation
- **Trie structures**: For XOR maximization problems
- **Rolling hash**: For string/sequence problems

### 3. Optimize Step by Step
- Start with brute force understanding
- Apply bit manipulation optimizations
- Consider space-time tradeoffs
- Handle edge cases (negative numbers, overflow)

---

## 🚨 Common Pitfalls in Medium Problems

1. **State Machine Complexity**: Don't overcomplicate the state transitions
2. **Integer Overflow**: Be careful with left shifts and large numbers
3. **Negative Numbers**: Handle two's complement representation correctly
4. **Off-by-one Errors**: Double-check bit position calculations
5. **Edge Cases**: Empty arrays, single elements, boundary values

---

## 📝 Interview Tips for Medium Problems

### Preparation:
1. **Master easy problems first** - Build strong foundation
2. **Understand patterns** - Recognize when to use each technique
3. **Practice state machines** - Single Number II is a classic
4. **Learn Trie + XOR** - Common in advanced problems

### During Interview:
1. **Explain your approach** - Why bit manipulation is suitable
2. **Start with examples** - Trace through small cases
3. **Build incrementally** - Don't jump to complex solutions
4. **Test edge cases** - Zero, negative numbers, single elements
5. **Optimize gradually** - Show progression from brute force

### Time Management:
- **Medium problems**: 20-30 minutes
- **Spend 5 minutes** understanding and planning
- **15-20 minutes** coding and testing
- **5 minutes** for edge cases and optimization

---

## 🚀 Next Steps

After mastering these medium problems:
1. **Move to hard problems** - Complex DP with bitmasks
2. **Study advanced data structures** - Trie, Segment Trees with bits
3. **Practice contest problems** - Codeforces, AtCoder bit manipulation
4. **Learn system-level applications** - Bit manipulation in databases, graphics

Remember: Medium bit manipulation problems test your ability to combine multiple concepts and recognize complex patterns!