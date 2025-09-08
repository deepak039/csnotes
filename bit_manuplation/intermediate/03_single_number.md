# Single Number Problems (XOR-based)

## 🎯 Core Concept: XOR Properties

XOR has unique properties that make it perfect for "single number" problems:
```python
a ^ a = 0        # Self-cancellation
a ^ 0 = a        # Identity
a ^ b = b ^ a    # Commutative  
(a ^ b) ^ c = a ^ (b ^ c)  # Associative
```

## 🔍 Problem Variations

### 1. Single Number I - All Others Appear Twice

**Problem**: Find the number that appears once, all others appear exactly twice.

```python
def single_number(nums):
    """
    Time: O(n), Space: O(1)
    XOR all numbers - duplicates cancel out
    """
    result = 0
    for num in nums:
        result ^= num
    return result

# Example: [2,2,1] → 2^2^1 = 0^1 = 1
# Example: [4,1,2,1,2] → 4^1^2^1^2 = 4^0^0 = 4
```

### 2. Single Number II - All Others Appear Thrice

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

# State machine:
# 00 → 01 → 10 → 00 (for each bit position)
# ones tracks bits appearing 1 or 3 times
# twos tracks bits appearing 2 or 3 times
```

**Alternative Approach - Bit Counting**:
```python
def single_number_ii_counting(nums):
    """Count bits at each position"""
    result = 0
    for i in range(32):
        bit_count = 0
        for num in nums:
            bit_count += (num >> i) & 1
        
        if bit_count % 3 == 1:
            result |= (1 << i)
    
    # Handle negative numbers in Python
    if result >= 2**31:
        result -= 2**32
    
    return result
```

### 3. Single Number III - Two Singles, Others Twice

**Problem**: Find two numbers that appear once, all others appear exactly twice.

```python
def single_number_iii(nums):
    """
    Time: O(n), Space: O(1)
    Use XOR to separate the two unique numbers
    """
    # Step 1: XOR all numbers to get xor of two unique numbers
    xor_all = 0
    for num in nums:
        xor_all ^= num
    
    # Step 2: Find rightmost set bit (where the two numbers differ)
    rightmost_bit = xor_all & (-xor_all)
    
    # Step 3: Divide numbers into two groups based on this bit
    num1 = num2 = 0
    for num in nums:
        if num & rightmost_bit:
            num1 ^= num
        else:
            num2 ^= num
    
    return [num1, num2]

# Example: [1,2,1,3,2,5]
# xor_all = 1^2^1^3^2^5 = 3^5 = 6 (110)
# rightmost_bit = 6 & -6 = 2 (010)
# Group 1 (bit 1 set): [2,3,2] → 3
# Group 2 (bit 1 not set): [1,1,5] → 5
```

## 🧮 Advanced XOR Problems

### 4. Missing Number
```python
def missing_number(nums):
    """Find missing number in range [0,n]"""
    n = len(nums)
    result = n  # Start with n
    
    for i, num in enumerate(nums):
        result ^= i ^ num
    
    return result

# Alternative: XOR all numbers from 0 to n with array elements
def missing_number_alt(nums):
    result = 0
    for i in range(len(nums) + 1):
        result ^= i
    for num in nums:
        result ^= num
    return result
```

### 5. Find the Duplicate Number
```python
def find_duplicate(nums):
    """Find duplicate in array of n+1 integers (1 to n)"""
    # Using XOR (if exactly one duplicate)
    n = len(nums) - 1
    result = 0
    
    # XOR all array elements
    for num in nums:
        result ^= num
    
    # XOR with numbers 1 to n
    for i in range(1, n + 1):
        result ^= i
    
    return result

# Note: This works only if there's exactly one duplicate
# For general case, use Floyd's cycle detection
```

### 6. Single Number in Sorted Array
```python
def single_non_duplicate(nums):
    """Find single number in sorted array where others appear twice"""
    # Binary search approach
    left, right = 0, len(nums) - 1
    
    while left < right:
        mid = (left + right) // 2
        
        # Ensure mid is even for pair checking
        if mid % 2 == 1:
            mid -= 1
        
        # Check if pair is intact
        if nums[mid] == nums[mid + 1]:
            left = mid + 2  # Single is in right half
        else:
            right = mid     # Single is in left half
    
    return nums[left]
```

## 🎯 Complex XOR Applications

### 7. Maximum XOR of Two Numbers
```python
def find_maximum_xor(nums):
    """Find maximum XOR of any two numbers in array"""
    max_xor = 0
    mask = 0
    
    # Build answer bit by bit from MSB
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
```

### 8. XOR Queries of Subarray
```python
def xor_queries(arr, queries):
    """Answer XOR queries on subarrays efficiently"""
    # Build prefix XOR array
    prefix_xor = [0]
    for num in arr:
        prefix_xor.append(prefix_xor[-1] ^ num)
    
    result = []
    for left, right in queries:
        # XOR of subarray [left, right] = prefix[right+1] ^ prefix[left]
        result.append(prefix_xor[right + 1] ^ prefix_xor[left])
    
    return result
```

### 9. Decode XORed Array
```python
def decode(encoded, first):
    """Decode array where encoded[i] = arr[i] ^ arr[i+1]"""
    decoded = [first]
    
    for i in range(len(encoded)):
        decoded.append(decoded[-1] ^ encoded[i])
    
    return decoded

# If first element is unknown:
def decode_without_first(encoded):
    """When we need to find the first element"""
    n = len(encoded) + 1
    
    # XOR all numbers from 1 to n
    total_xor = 0
    for i in range(1, n + 1):
        total_xor ^= i
    
    # XOR encoded elements at odd indices
    encoded_xor = 0
    for i in range(1, len(encoded), 2):
        encoded_xor ^= encoded[i]
    
    first = total_xor ^ encoded_xor
    return decode(encoded, first)
```

## 🔧 XOR Optimization Techniques

### Bit Manipulation with XOR
```python
def swap_without_temp(a, b):
    """Swap two numbers without temporary variable"""
    a = a ^ b
    b = a ^ b  # b = (a^b) ^ b = a
    a = a ^ b  # a = (a^b) ^ a = b
    return a, b

def check_opposite_signs(a, b):
    """Check if two numbers have opposite signs"""
    return (a ^ b) < 0

def toggle_case(char):
    """Toggle case of alphabetic character"""
    return chr(ord(char) ^ 32)  # 32 = 0x20
```

### XOR Linked List (Space Optimization)
```python
class XORNode:
    def __init__(self, data):
        self.data = data
        self.npx = 0  # XOR of next and previous addresses

class XORLinkedList:
    def __init__(self):
        self.head = None
    
    def insert(self, data):
        new_node = XORNode(data)
        new_node.npx = id(self.head)
        
        if self.head:
            self.head.npx ^= id(new_node)
        
        self.head = new_node
```

## 📊 Problem Patterns Summary

| Problem Type | Key Insight | Time | Space |
|--------------|-------------|------|-------|
| Single Number I | XOR cancellation | O(n) | O(1) |
| Single Number II | Bit state machine | O(n) | O(1) |
| Single Number III | Bit grouping | O(n) | O(1) |
| Missing Number | XOR with expected | O(n) | O(1) |
| Maximum XOR | Bit-by-bit construction | O(n) | O(n) |
| XOR Queries | Prefix XOR | O(n+q) | O(n) |

## 🎯 Interview Problems by Difficulty

### Easy:
1. **Single Number** (LeetCode 136)
2. **Missing Number** (LeetCode 268)  
3. **Number Complement** (LeetCode 476)

### Medium:
1. **Single Number II** (LeetCode 137)
2. **Single Number III** (LeetCode 260)
3. **Find the Duplicate Number** (LeetCode 287)
4. **XOR Operation in Array** (LeetCode 1486)

### Hard:
1. **Maximum XOR of Two Numbers** (LeetCode 421)
2. **Maximum XOR With an Element From Array** (LeetCode 1707)
3. **Minimum XOR Sum of Two Arrays** (LeetCode 1879)

## 🧠 Problem-Solving Strategy

### Step-by-Step Approach:
1. **Identify the pattern**: How many times do numbers repeat?
2. **Choose XOR variant**: Simple XOR, bit counting, or grouping?
3. **Handle edge cases**: Empty arrays, single elements
4. **Optimize if needed**: Can we do better than O(n) space?

### Common Mistakes:
- Forgetting XOR properties (especially self-cancellation)
- Not handling negative numbers in bit counting
- Incorrect bit manipulation in state machines
- Missing edge cases in binary search variants

## 📝 Practice Template

```python
def solve_single_number_problem(nums):
    """Template for single number problems"""
    
    # Method 1: Simple XOR (for pairs)
    if all_others_appear_twice:
        result = 0
        for num in nums:
            result ^= num
        return result
    
    # Method 2: Bit counting (for triples)
    if all_others_appear_thrice:
        ones = twos = 0
        for num in nums:
            twos ^= ones & num
            ones ^= num
            threes = ones & twos
            ones &= ~threes
            twos &= ~threes
        return ones
    
    # Method 3: Grouping (for two singles)
    if two_singles_others_pairs:
        xor_all = 0
        for num in nums:
            xor_all ^= num
        
        rightmost_bit = xor_all & (-xor_all)
        num1 = num2 = 0
        
        for num in nums:
            if num & rightmost_bit:
                num1 ^= num
            else:
                num2 ^= num
        
        return [num1, num2]
```

## 🚀 Advanced Tips

1. **XOR is reversible**: If `a ^ b = c`, then `a ^ c = b` and `b ^ c = a`
2. **XOR preserves information**: No data is lost in XOR operations
3. **Bit manipulation**: XOR is fundamental to many bit tricks
4. **Cryptography**: XOR is used in encryption algorithms
5. **Error detection**: XOR checksums detect single-bit errors

Remember: XOR problems often have elegant O(1) space solutions that seem impossible at first glance!