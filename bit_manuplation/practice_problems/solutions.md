# Detailed Solutions with Explanations

## 🎯 Complete Solutions for Key Problems

This section provides detailed solutions with multiple approaches, complexity analysis, and interview tips.

---

## 🟢 Easy Problem Solutions

### 1. Number of 1 Bits (LeetCode 191)

**Problem**: Count the number of set bits in an integer.

```python
def hamming_weight(n):
    """
    Approach 1: Brian Kernighan's Algorithm (Optimal)
    Time: O(k) where k = number of set bits
    Space: O(1)
    """
    count = 0
    while n:
        n &= n - 1  # Remove rightmost set bit
        count += 1
    return count

def hamming_weight_naive(n):
    """
    Approach 2: Check each bit
    Time: O(32) = O(1)
    Space: O(1)
    """
    count = 0
    while n:
        count += n & 1
        n >>= 1
    return count

def hamming_weight_builtin(n):
    """
    Approach 3: Built-in function
    Time: O(1)
    Space: O(1)
    """
    return bin(n).count('1')

def hamming_weight_lookup(n):
    """
    Approach 4: Lookup table (for frequent calls)
    Time: O(1)
    Space: O(256) for lookup table
    """
    # Precompute lookup table
    lookup = [bin(i).count('1') for i in range(256)]
    
    return (lookup[n & 0xFF] + 
            lookup[(n >> 8) & 0xFF] + 
            lookup[(n >> 16) & 0xFF] + 
            lookup[(n >> 24) & 0xFF])

# Interview Discussion Points:
# 1. Why is Brian Kernighan's algorithm optimal?
# 2. When would you use lookup table approach?
# 3. How does this relate to population count instruction in CPUs?
```

### 2. Single Number (LeetCode 136)

**Problem**: Find the number that appears once, all others appear twice.

```python
def single_number(nums):
    """
    Approach 1: XOR (Optimal)
    Time: O(n), Space: O(1)
    
    Key insight: a ^ a = 0, a ^ 0 = a
    All duplicates cancel out, leaving the unique number
    """
    result = 0
    for num in nums:
        result ^= num
    return result

def single_number_set(nums):
    """
    Approach 2: Using Set
    Time: O(n), Space: O(n)
    """
    seen = set()
    for num in nums:
        if num in seen:
            seen.remove(num)
        else:
            seen.add(num)
    return seen.pop()

def single_number_math(nums):
    """
    Approach 3: Mathematical approach
    Time: O(n), Space: O(n)
    
    2 * sum(unique) - sum(all) = unique_number
    """
    return 2 * sum(set(nums)) - sum(nums)

# Test cases and edge cases:
def test_single_number():
    assert single_number([2, 2, 1]) == 1
    assert single_number([4, 1, 2, 1, 2]) == 4
    assert single_number([1]) == 1
    print("All tests passed!")

# Interview Tips:
# 1. Always mention XOR approach first
# 2. Explain why XOR works mathematically
# 3. Discuss space complexity advantage
# 4. Mention that order doesn't matter due to XOR properties
```

### 3. Power of Two (LeetCode 231)

**Problem**: Check if a number is a power of 2.

```python
def is_power_of_two(n):
    """
    Approach 1: Bit manipulation trick (Optimal)
    Time: O(1), Space: O(1)
    
    Power of 2 has exactly one bit set
    n & (n-1) removes the rightmost set bit
    """
    return n > 0 and (n & (n - 1)) == 0

def is_power_of_two_count_bits(n):
    """
    Approach 2: Count set bits
    Time: O(log n), Space: O(1)
    """
    return n > 0 and bin(n).count('1') == 1

def is_power_of_two_division(n):
    """
    Approach 3: Repeated division
    Time: O(log n), Space: O(1)
    """
    if n <= 0:
        return False
    
    while n % 2 == 0:
        n //= 2
    
    return n == 1

def is_power_of_two_math(n):
    """
    Approach 4: Mathematical approach
    Time: O(1), Space: O(1)
    """
    import math
    return n > 0 and math.log2(n).is_integer()

def is_power_of_two_rightmost_bit(n):
    """
    Approach 5: Isolate rightmost bit
    Time: O(1), Space: O(1)
    
    For power of 2, rightmost set bit equals the number itself
    """
    return n > 0 and (n & -n) == n

# Comprehensive test cases:
def test_power_of_two():
    # Powers of 2
    assert is_power_of_two(1) == True   # 2^0
    assert is_power_of_two(2) == True   # 2^1
    assert is_power_of_two(4) == True   # 2^2
    assert is_power_of_two(16) == True  # 2^4
    
    # Not powers of 2
    assert is_power_of_two(3) == False
    assert is_power_of_two(5) == False
    assert is_power_of_two(6) == False
    
    # Edge cases
    assert is_power_of_two(0) == False
    assert is_power_of_two(-1) == False
    assert is_power_of_two(-16) == False
    
    print("All power of two tests passed!")

# Interview Discussion:
# 1. Explain why n & (n-1) == 0 works
# 2. Draw binary representations for examples
# 3. Discuss edge case of n = 0
# 4. Mention that negative numbers can't be powers of 2
```

---

## 🟡 Medium Problem Solutions

### 4. Single Number II (LeetCode 137)

**Problem**: Find the number that appears once, all others appear exactly 3 times.

```python
def single_number_ii(nums):
    """
    Approach 1: State Machine (Optimal)
    Time: O(n), Space: O(1)
    
    Use two variables to track bit states:
    00 -> 01 -> 10 -> 00 (for each bit position)
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
    """
    Approach 2: Bit counting
    Time: O(32n) = O(n), Space: O(1)
    
    Count bits at each position, take modulo 3
    """
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

def single_number_ii_hashmap(nums):
    """
    Approach 3: Hash map (for understanding)
    Time: O(n), Space: O(n)
    """
    from collections import Counter
    count = Counter(nums)
    
    for num, freq in count.items():
        if freq == 1:
            return num

# Detailed explanation of state machine:
def explain_state_machine():
    """
    State transitions for each bit:
    
    Current State | Input Bit | Next State | ones | twos
    -------------|-----------|------------|------|------
    00           | 0         | 00         | 0    | 0
    00           | 1         | 01         | 1    | 0
    01           | 0         | 01         | 1    | 0
    01           | 1         | 10         | 0    | 1
    10           | 0         | 10         | 0    | 1
    10           | 1         | 00         | 0    | 0
    
    ones tracks bits appearing 1 or 3 times
    twos tracks bits appearing 2 or 3 times
    When both are 1, we've seen the bit 3 times -> reset both
    """
    pass

# Test with detailed tracing:
def test_single_number_ii():
    nums = [2, 2, 3, 2]
    print(f"Input: {nums}")
    print(f"Expected: 3")
    print(f"Result: {single_number_ii(nums)}")
    
    # Trace through state machine
    ones = twos = 0
    for i, num in enumerate(nums):
        print(f"\nStep {i+1}: Processing {num} (binary: {bin(num)})")
        print(f"Before: ones={bin(ones)}, twos={bin(twos)}")
        
        twos = twos ^ (ones & num)
        ones = ones ^ num
        threes = ones & twos
        ones &= ~threes
        twos &= ~threes
        
        print(f"After: ones={bin(ones)}, twos={bin(twos)}")

# Interview Tips:
# 1. Start with bit counting approach for clarity
# 2. Explain state machine logic step by step
# 3. Handle negative numbers correctly
# 4. Discuss why this generalizes to "appears k times" problems
```

### 5. Maximum XOR of Two Numbers (LeetCode 421)

**Problem**: Find maximum XOR of any two numbers in the array.

```python
def find_maximum_xor(nums):
    """
    Approach 1: Bit-by-bit construction (Optimal)
    Time: O(32n), Space: O(n)
    
    Build the answer bit by bit from MSB to LSB
    """
    max_xor = 0
    mask = 0
    
    for i in range(31, -1, -1):  # From MSB to LSB
        mask |= (1 << i)  # Include current bit in mask
        prefixes = {num & mask for num in nums}
        
        temp = max_xor | (1 << i)  # Try to set current bit
        
        # Check if this bit can be set
        for prefix in prefixes:
            if temp ^ prefix in prefixes:
                max_xor = temp
                break
    
    return max_xor

class TrieNode:
    def __init__(self):
        self.children = {}

def find_maximum_xor_trie(nums):
    """
    Approach 2: Trie-based solution
    Time: O(32n), Space: O(32n)
    
    More intuitive but uses more space
    """
    if not nums:
        return 0
    
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
            # Try opposite bit for maximum XOR
            opposite = 1 - bit
            
            if opposite in node.children:
                current_xor |= (1 << i)
                node = node.children[opposite]
            else:
                node = node.children[bit]
        
        max_xor = max(max_xor, current_xor)
    
    return max_xor

def find_maximum_xor_brute_force(nums):
    """
    Approach 3: Brute force (for comparison)
    Time: O(n^2), Space: O(1)
    """
    max_xor = 0
    for i in range(len(nums)):
        for j in range(i + 1, len(nums)):
            max_xor = max(max_xor, nums[i] ^ nums[j])
    return max_xor

# Detailed explanation of bit-by-bit approach:
def explain_bit_construction():
    """
    Example: nums = [3, 10, 5, 25, 2, 8]
    
    Binary representations:
    3  = 00011
    10 = 01010
    5  = 00101
    25 = 11001
    2  = 00010
    8  = 01000
    
    Build answer from left to right:
    
    Bit 4 (MSB): Try to set it to 1
    - Prefixes with mask 10000: {0, 16}
    - temp = 16, check if 16^0=16 and 16^16=0 exist in prefixes
    - 16 exists, 0 exists -> can set bit 4
    - max_xor = 16
    
    Bit 3: Try to set it to 1  
    - Prefixes with mask 11000: {0, 8, 16, 24}
    - temp = 24, check pairs...
    - Can set bit 3 -> max_xor = 24
    
    Continue until all bits processed...
    """
    pass

# Test with step-by-step tracing:
def test_maximum_xor():
    nums = [3, 10, 5, 25, 2, 8]
    print(f"Input: {nums}")
    print(f"Binary representations:")
    for num in nums:
        print(f"{num:2d} = {bin(num)[2:].zfill(5)}")
    
    result = find_maximum_xor(nums)
    print(f"\nMaximum XOR: {result}")
    print(f"Binary: {bin(result)}")
    
    # Verify with brute force
    brute_result = find_maximum_xor_brute_force(nums)
    assert result == brute_result
    print("Verification passed!")

# Interview Discussion:
# 1. Explain why we build from MSB to LSB
# 2. Discuss the key insight: if a^b=c, then a^c=b and b^c=a
# 3. Compare time/space tradeoffs of different approaches
# 4. Mention applications in competitive programming
```

---

## 🔴 Hard Problem Solutions

### 6. Shortest Superstring (LeetCode 943)

**Problem**: Find shortest string containing all given strings as substrings.

```python
def shortest_superstring(words):
    """
    Approach: Bitmask DP + TSP
    Time: O(n^2 * 2^n), Space: O(n * 2^n)
    
    Model as Traveling Salesman Problem:
    - Each word is a city
    - Distance = length added when going from word i to word j
    - Find shortest path visiting all cities
    """
    n = len(words)
    
    # Precompute overlap between words
    overlap = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            if i != j:
                max_len = min(len(words[i]), len(words[j]))
                for k in range(max_len, 0, -1):
                    if words[i].endswith(words[j][:k]):
                        overlap[i][j] = k
                        break
    
    # DP state: dp[mask][i] = (min_length, parent)
    # mask = set of words used, i = last word
    dp = {}
    
    # Base case: single words
    for i in range(n):
        dp[(1 << i), i] = (len(words[i]), -1)
    
    # Fill DP table
    for mask in range(1, 1 << n):
        for i in range(n):
            if not (mask & (1 << i)):
                continue
            
            prev_mask = mask ^ (1 << i)
            if prev_mask == 0:
                continue
            
            for j in range(n):
                if not (prev_mask & (1 << j)):
                    continue
                
                if (prev_mask, j) in dp:
                    new_length = dp[(prev_mask, j)][0] + len(words[i]) - overlap[j][i]
                    
                    if (mask, i) not in dp or new_length < dp[(mask, i)][0]:
                        dp[(mask, i)] = (new_length, j)
    
    # Find optimal solution
    full_mask = (1 << n) - 1
    min_length = float('inf')
    last_word = -1
    
    for i in range(n):
        if (full_mask, i) in dp and dp[(full_mask, i)][0] < min_length:
            min_length = dp[(full_mask, i)][0]
            last_word = i
    
    # Reconstruct path
    path = []
    mask = full_mask
    curr = last_word
    
    while curr != -1:
        path.append(curr)
        if (mask, curr) in dp:
            next_curr = dp[(mask, curr)][1]
            mask ^= (1 << curr)
            curr = next_curr
        else:
            break
    
    path.reverse()
    
    # Build result string
    result = words[path[0]]
    for i in range(1, len(path)):
        prev_word = path[i-1]
        curr_word = path[i]
        result += words[curr_word][overlap[prev_word][curr_word]:]
    
    return result

def shortest_superstring_greedy(words):
    """
    Approach: Greedy approximation
    Time: O(n^3), Space: O(n^2)
    
    Not optimal but much faster for large inputs
    """
    def get_overlap(s1, s2):
        max_overlap = min(len(s1), len(s2))
        for i in range(max_overlap, 0, -1):
            if s1.endswith(s2[:i]):
                return i
        return 0
    
    def merge_strings(s1, s2, overlap):
        return s1 + s2[overlap:]
    
    remaining = words[:]
    
    while len(remaining) > 1:
        max_overlap = -1
        best_i = best_j = -1
        
        # Find pair with maximum overlap
        for i in range(len(remaining)):
            for j in range(len(remaining)):
                if i != j:
                    overlap = get_overlap(remaining[i], remaining[j])
                    if overlap > max_overlap:
                        max_overlap = overlap
                        best_i, best_j = i, j
        
        # Merge best pair
        if max_overlap > 0:
            merged = merge_strings(remaining[best_i], remaining[best_j], max_overlap)
            remaining = [merged] + [remaining[k] for k in range(len(remaining)) 
                                  if k != best_i and k != best_j]
        else:
            # No overlap found, just concatenate
            merged = remaining[0] + remaining[1]
            remaining = [merged] + remaining[2:]
    
    return remaining[0]

# Test with detailed explanation:
def test_shortest_superstring():
    words = ["catg", "ctaagt", "gcta", "ttca", "atgcatc"]
    print(f"Input words: {words}")
    
    # Show overlaps
    n = len(words)
    print("\nOverlap matrix:")
    for i in range(n):
        for j in range(n):
            if i != j:
                max_len = min(len(words[i]), len(words[j]))
                overlap = 0
                for k in range(max_len, 0, -1):
                    if words[i].endswith(words[j][:k]):
                        overlap = k
                        break
                print(f"overlap({words[i]}, {words[j]}) = {overlap}")
    
    result = shortest_superstring(words)
    print(f"\nShortest superstring: {result}")
    print(f"Length: {len(result)}")
    
    # Verify all words are substrings
    for word in words:
        assert word in result
        print(f"✓ '{word}' found in result")

# Interview Tips:
# 1. Explain the TSP connection clearly
# 2. Discuss why this is NP-hard
# 3. Mention approximation algorithms for large inputs
# 4. Show how to reconstruct the solution path
```

---

## 🎯 Problem-Solving Templates

### Template 1: XOR Problems
```python
def solve_xor_problem(nums):
    """
    Template for XOR-based problems
    Key insights:
    - a ^ a = 0 (self-cancellation)
    - a ^ 0 = a (identity)
    - XOR is commutative and associative
    """
    result = 0
    for num in nums:
        result ^= num
    return result

# Variations:
# - Single number: direct XOR
# - Two singles: use differing bit to separate groups
# - Missing number: XOR with expected sequence
```

### Template 2: Bit Counting
```python
def count_bits_template(n):
    """
    Template for bit counting problems
    """
    # Method 1: Brian Kernighan's algorithm
    count = 0
    while n:
        n &= n - 1  # Remove rightmost set bit
        count += 1
    return count

    # Method 2: DP approach for range
    dp = [0] * (n + 1)
    for i in range(1, n + 1):
        dp[i] = dp[i >> 1] + (i & 1)
    return dp
```

### Template 3: Subset Generation
```python
def generate_subsets_template(arr):
    """
    Template for subset generation using bitmasks
    """
    n = len(arr)
    result = []
    
    for mask in range(1 << n):  # 2^n subsets
        subset = []
        for i in range(n):
            if mask & (1 << i):
                subset.append(arr[i])
        result.append(subset)
    
    return result
```

### Template 4: Bitmask DP
```python
def bitmask_dp_template(items, constraints):
    """
    Template for bitmask DP problems
    """
    n = len(items)
    # dp[mask] = optimal value for subset represented by mask
    dp = [float('inf')] * (1 << n)
    dp[0] = 0  # Base case
    
    for mask in range(1 << n):
        if dp[mask] == float('inf'):
            continue
        
        # Try adding each unused item
        for i in range(n):
            if not (mask & (1 << i)):  # Item i not used
                new_mask = mask | (1 << i)
                new_value = dp[mask] + cost(items[i], mask)
                dp[new_mask] = min(dp[new_mask], new_value)
    
    return dp[(1 << n) - 1]  # All items used
```

---

## 📊 Complexity Analysis Summary

| Problem Type | Time Complexity | Space Complexity | Key Technique |
|--------------|----------------|------------------|---------------|
| Basic Bit Ops | O(1) to O(log n) | O(1) | Direct manipulation |
| XOR Problems | O(n) | O(1) | Self-cancellation |
| Bit Counting | O(k) to O(n) | O(1) to O(n) | Brian Kernighan's |
| Subset Generation | O(n × 2ⁿ) | O(2ⁿ) | Bitmask enumeration |
| Maximum XOR | O(32n) | O(32n) | Trie or bit construction |
| Bitmask DP | O(n × 2ⁿ) | O(2ⁿ) | Dynamic programming |

---

## 🚀 Interview Success Tips

### Before the Interview:
1. **Practice mental binary arithmetic** - Convert small numbers quickly
2. **Memorize key bit tricks** - `n & (n-1)`, `n & -n`, XOR properties
3. **Understand complexity** - When bit manipulation provides optimal solutions
4. **Know multiple approaches** - Show problem-solving flexibility

### During the Interview:
1. **Start with examples** - Draw binary representations
2. **Explain your reasoning** - Why bit manipulation is suitable
3. **Code incrementally** - Build solution step by step
4. **Test with edge cases** - Zero, negative numbers, single elements
5. **Discuss optimizations** - Time/space tradeoffs

### Common Mistakes to Avoid:
1. **Operator precedence** - Use parentheses liberally
2. **Off-by-one errors** - Remember 0-indexing for bit positions
3. **Integer overflow** - Be careful with left shifts
4. **Negative number handling** - Understand two's complement
5. **Premature optimization** - Get correctness first, then optimize

Remember: Bit manipulation problems often have elegant solutions that seem impossible at first glance. Focus on understanding the underlying patterns and properties!