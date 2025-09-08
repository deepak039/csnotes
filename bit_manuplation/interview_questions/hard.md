# Hard Bit Manipulation Interview Questions

## 🎯 Level: Hard (Advanced Concepts)

These problems require deep understanding of bit manipulation, complex algorithms, and often combine multiple advanced techniques.

---

## 1. Maximum XOR With an Element From Array (LeetCode 1707) ⭐⭐⭐

**Problem**: Answer queries for maximum XOR with elements ≤ limit.

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.min_val = float('inf')

def maximize_xor(nums, queries):
    """
    Time: O(n*32 + q*32), Space: O(n*32)
    Use Trie with minimum value tracking
    """
    root = TrieNode()
    
    def insert(num):
        node = root
        node.min_val = min(node.min_val, num)
        
        for i in range(31, -1, -1):
            bit = (num >> i) & 1
            if bit not in node.children:
                node.children[bit] = TrieNode()
            node = node.children[bit]
            node.min_val = min(node.min_val, num)
    
    def query_max_xor(x, limit):
        if root.min_val > limit:
            return -1
        
        node = root
        max_xor = 0
        
        for i in range(31, -1, -1):
            bit = (x >> i) & 1
            opposite = 1 - bit
            
            # Try opposite bit if possible and within limit
            if (opposite in node.children and 
                node.children[opposite].min_val <= limit):
                max_xor |= (1 << i)
                node = node.children[opposite]
            elif bit in node.children:
                node = node.children[bit]
            else:
                return -1
        
        return max_xor
    
    # Build trie
    for num in nums:
        insert(num)
    
    # Process queries
    result = []
    for x, limit in queries:
        result.append(query_max_xor(x, limit))
    
    return result

# Alternative: Offline processing with sorting
def maximize_xor_offline(nums, queries):
    """Offline processing approach"""
    # Add indices to queries and sort by limit
    indexed_queries = [(limit, x, i) for i, (x, limit) in enumerate(queries)]
    indexed_queries.sort()
    
    nums.sort()
    result = [0] * len(queries)
    
    root = TrieNode()
    num_idx = 0
    
    for limit, x, query_idx in indexed_queries:
        # Add all numbers <= limit to trie
        while num_idx < len(nums) and nums[num_idx] <= limit:
            insert_to_trie(root, nums[num_idx])
            num_idx += 1
        
        if num_idx == 0:
            result[query_idx] = -1
        else:
            result[query_idx] = find_max_xor(root, x)
    
    return result
```

**Key Insight**: Use Trie with minimum value tracking or offline processing with sorting.

---

## 2. Shortest Superstring (LeetCode 943) ⭐⭐⭐

**Problem**: Find shortest string containing all given strings as substrings.

```python
def shortest_superstring(words):
    """
    Time: O(n^2 * 2^n), Space: O(n * 2^n)
    Use bitmask DP with overlap precomputation
    """
    n = len(words)
    
    # Precompute overlap between words
    overlap = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            if i != j:
                max_overlap = min(len(words[i]), len(words[j]))
                for k in range(max_overlap, 0, -1):
                    if words[i].endswith(words[j][:k]):
                        overlap[i][j] = k
                        break
    
    # DP: dp[mask][i] = (min_length, parent)
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
```

**Key Insight**: Use bitmask DP with precomputed overlaps to find optimal ordering.

---

## 3. Minimum XOR Sum of Two Arrays (LeetCode 1879) ⭐⭐⭐

**Problem**: Find minimum XOR sum when pairing elements from two arrays.

```python
def minimum_xor_sum(nums1, nums2):
    """
    Time: O(n * 2^n), Space: O(2^n)
    Use bitmask DP for assignment problem
    """
    n = len(nums1)
    
    # dp[mask] = minimum XOR sum using elements in mask from nums2
    dp = [float('inf')] * (1 << n)
    dp[0] = 0
    
    for mask in range(1 << n):
        if dp[mask] == float('inf'):
            continue
        
        # Find next position to assign (number of set bits)
        pos = bin(mask).count('1')
        if pos == n:
            continue
        
        # Try assigning nums1[pos] to each unused element in nums2
        for i in range(n):
            if not (mask & (1 << i)):  # nums2[i] not used
                new_mask = mask | (1 << i)
                dp[new_mask] = min(dp[new_mask], 
                                 dp[mask] + (nums1[pos] ^ nums2[i]))
    
    return dp[(1 << n) - 1]

# Alternative: Recursive with memoization
def minimum_xor_sum_recursive(nums1, nums2):
    """Recursive approach with memoization"""
    from functools import lru_cache
    
    @lru_cache(None)
    def dp(pos, mask):
        if pos == len(nums1):
            return 0
        
        min_sum = float('inf')
        for i in range(len(nums2)):
            if not (mask & (1 << i)):
                min_sum = min(min_sum, 
                            (nums1[pos] ^ nums2[i]) + dp(pos + 1, mask | (1 << i)))
        
        return min_sum
    
    return dp(0, 0)
```

**Key Insight**: Model as assignment problem using bitmask DP.

---

## 4. Stone Game IX (LeetCode 1908) ⭐⭐⭐

**Problem**: Determine if Alice can win the stone game with modulo 3 constraints.

```python
def stone_game_ix(stones):
    """
    Time: O(n), Space: O(1)
    Use modulo arithmetic and game theory
    """
    count = [0, 0, 0]  # count[i] = number of stones with value % 3 == i
    
    for stone in stones:
        count[stone % 3] += 1
    
    def can_win(count, start):
        """Check if current player can win starting with 'start' (1 or 2)"""
        if count[start] == 0:
            return False
        
        count[start] -= 1
        current_sum = start
        turn = 1  # 1 for Alice, -1 for Bob
        
        while True:
            if turn == 1:  # Alice's turn
                if current_sum % 3 == 1:
                    if count[1] > 0:
                        count[1] -= 1
                        current_sum += 1
                    elif count[2] > 0:
                        count[2] -= 1
                        current_sum += 2
                    else:
                        return False  # Alice loses
                else:  # current_sum % 3 == 2
                    if count[2] > 0:
                        count[2] -= 1
                        current_sum += 2
                    elif count[1] > 0:
                        count[1] -= 1
                        current_sum += 1
                    else:
                        return False  # Alice loses
            else:  # Bob's turn
                if current_sum % 3 == 1:
                    if count[1] > 0:
                        count[1] -= 1
                        current_sum += 1
                    elif count[2] > 0:
                        count[2] -= 1
                        current_sum += 2
                    else:
                        return True  # Bob loses, Alice wins
                else:  # current_sum % 3 == 2
                    if count[2] > 0:
                        count[2] -= 1
                        current_sum += 2
                    elif count[1] > 0:
                        count[1] -= 1
                        current_sum += 1
                    else:
                        return True  # Bob loses, Alice wins
            
            turn *= -1
    
    # Try starting with 1 or 2
    return (can_win(count[:], 1) or can_win(count[:], 2))

def stone_game_ix_optimized(stones):
    """Optimized version using mathematical analysis"""
    count = [0, 0, 0]
    for stone in stones:
        count[stone % 3] += 1
    
    # Alice needs to start with 1 or 2, and the sequence must be valid
    # Key insight: zeros can be used to skip turns
    
    def check(first):
        if count[first] == 0:
            return False
        
        c = count[:]
        c[first] -= 1
        
        # After first move, we need alternating pattern
        other = 3 - first
        
        # Alice needs at least one move, Bob needs to be blocked
        if c[other] == 0 and c[first] == 0:
            return False
        
        # Consider the effect of zeros (turn skipping)
        zeros = c[0]
        
        # Simplified analysis based on parity and available moves
        if zeros % 2 == 0:
            return c[first] > 0 or c[other] > 2
        else:
            return c[first] > 2 or c[other] > 0
    
    return check(1) or check(2)
```

**Key Insight**: Use modulo arithmetic and analyze game states with parity considerations.

---

## 5. Count Number of Maximum Bitwise-OR Subsets (LeetCode 2044) ⭐⭐⭐

**Problem**: Count subsets with maximum possible bitwise OR.

```python
def count_max_or_subsets(nums):
    """
    Time: O(n * 2^n), Space: O(2^n)
    Generate all subsets and find maximum OR
    """
    n = len(nums)
    max_or = 0
    
    # Find maximum possible OR (OR of all elements)
    for num in nums:
        max_or |= num
    
    count = 0
    
    # Check all subsets
    for mask in range(1, 1 << n):  # Skip empty subset
        current_or = 0
        for i in range(n):
            if mask & (1 << i):
                current_or |= nums[i]
        
        if current_or == max_or:
            count += 1
    
    return count

def count_max_or_subsets_dp(nums):
    """
    Time: O(n * max_or), Space: O(max_or)
    DP approach counting subsets by OR value
    """
    max_or = 0
    for num in nums:
        max_or |= num
    
    # dp[or_val] = number of subsets with OR value = or_val
    dp = {0: 1}  # Empty subset has OR = 0
    
    for num in nums:
        new_dp = dp.copy()
        for or_val, count in dp.items():
            new_or = or_val | num
            new_dp[new_or] = new_dp.get(new_or, 0) + count
        dp = new_dp
    
    return dp.get(max_or, 0)

def count_max_or_subsets_recursive(nums):
    """Recursive backtracking approach"""
    max_or = 0
    for num in nums:
        max_or |= num
    
    def backtrack(index, current_or):
        if index == len(nums):
            return 1 if current_or == max_or else 0
        
        # Don't include current element
        count = backtrack(index + 1, current_or)
        # Include current element
        count += backtrack(index + 1, current_or | nums[index])
        
        return count
    
    return backtrack(0, 0)
```

**Key Insight**: Maximum OR is the OR of all elements; count subsets achieving this value.

---

## 6. Minimum Number of Taps to Open to Water a Garden (LeetCode 1326) ⭐⭐⭐

**Problem**: Find minimum taps needed to water entire garden using bit manipulation optimization.

```python
def min_taps(n, ranges):
    """
    Time: O(n^2), Space: O(n)
    Convert to interval covering problem with DP optimization
    """
    # Convert taps to intervals
    intervals = []
    for i, r in enumerate(ranges):
        if r > 0:
            left = max(0, i - r)
            right = min(n, i + r)
            intervals.append((left, right))
    
    # Sort intervals by start position
    intervals.sort()
    
    # DP approach: dp[i] = minimum taps to cover [0, i]
    dp = [float('inf')] * (n + 1)
    dp[0] = 0
    
    for start, end in intervals:
        if dp[start] == float('inf'):
            continue
        
        # Update all positions this interval can cover
        for pos in range(start, min(end + 1, n + 1)):
            dp[pos] = min(dp[pos], dp[start] + 1)
    
    return dp[n] if dp[n] != float('inf') else -1

def min_taps_greedy(n, ranges):
    """
    Time: O(n), Space: O(n)
    Greedy approach with interval merging
    """
    # For each position, find the farthest we can reach
    max_reach = [0] * (n + 1)
    
    for i, r in enumerate(ranges):
        if r == 0:
            continue
        
        left = max(0, i - r)
        right = min(n, i + r)
        max_reach[left] = max(max_reach[left], right)
    
    taps = 0
    current_end = 0
    farthest = 0
    
    for i in range(n):
        farthest = max(farthest, max_reach[i])
        
        if i == current_end:
            if farthest <= i:
                return -1
            taps += 1
            current_end = farthest
    
    return taps
```

**Key Insight**: Convert to interval covering problem and use greedy or DP approach.

---

## 7. Parallel Courses III (LeetCode 2050) ⭐⭐⭐

**Problem**: Find minimum time to complete all courses with prerequisites and durations.

```python
def minimum_time(n, relations, time):
    """
    Time: O(V + E), Space: O(V + E)
    Topological sort with time calculation
    """
    from collections import defaultdict, deque
    
    # Build graph and in-degree count
    graph = defaultdict(list)
    in_degree = [0] * (n + 1)
    
    for prev, next_course in relations:
        graph[prev].append(next_course)
        in_degree[next_course] += 1
    
    # Initialize queue with courses having no prerequisites
    queue = deque()
    completion_time = [0] * (n + 1)
    
    for i in range(1, n + 1):
        if in_degree[i] == 0:
            queue.append(i)
            completion_time[i] = time[i - 1]
    
    while queue:
        course = queue.popleft()
        
        for next_course in graph[course]:
            # Update completion time for next course
            completion_time[next_course] = max(
                completion_time[next_course],
                completion_time[course] + time[next_course - 1]
            )
            
            in_degree[next_course] -= 1
            if in_degree[next_course] == 0:
                queue.append(next_course)
    
    return max(completion_time[1:])

# Alternative: Using bitmask for small n (when n <= 20)
def minimum_time_bitmask(n, relations, time):
    """Bitmask DP approach for small n"""
    if n > 20:  # Too large for bitmask
        return minimum_time(n, relations, time)
    
    # Build prerequisite masks
    prereq = [0] * n
    for prev, next_course in relations:
        prereq[next_course - 1] |= (1 << (prev - 1))
    
    # dp[mask] = minimum time to complete courses in mask
    dp = [float('inf')] * (1 << n)
    dp[0] = 0
    
    for mask in range(1 << n):
        if dp[mask] == float('inf'):
            continue
        
        for i in range(n):
            if mask & (1 << i):  # Course i already taken
                continue
            
            # Check if prerequisites are satisfied
            if (mask & prereq[i]) == prereq[i]:
                new_mask = mask | (1 << i)
                dp[new_mask] = min(dp[new_mask], dp[mask] + time[i])
    
    return dp[(1 << n) - 1]
```

**Key Insight**: Use topological sort for large n, bitmask DP for small n.

---

## 🎯 Advanced Problem Patterns

### Pattern 1: Trie + Bit Manipulation
- Maximum XOR problems
- Prefix matching with constraints
- Efficient bit-level searches

### Pattern 2: Bitmask DP
- Assignment problems
- Traveling salesman variants
- Subset optimization problems

### Pattern 3: Game Theory + Bits
- Stone games with modular arithmetic
- Nim-like games with bit operations
- Optimal strategy with bit states

### Pattern 4: Complex State Machines
- Multi-level bit counting
- State transitions with multiple variables
- Bit-based finite automata

---

## 📊 Complexity Analysis for Hard Problems

| Problem Type | Time | Space | Key Technique |
|--------------|------|-------|---------------|
| Trie + XOR | O(n × 32) | O(n × 32) | Bit-level trie traversal |
| Bitmask DP | O(n × 2ⁿ) | O(2ⁿ) | Dynamic programming on subsets |
| Game Theory | O(n) | O(1) | Mathematical analysis |
| Assignment | O(n × 2ⁿ) | O(2ⁿ) | Optimal matching |
| Interval Problems | O(n²) | O(n) | Greedy or DP optimization |

---

## 🚨 Advanced Pitfalls

1. **Memory Explosion**: Bitmask DP with n > 20 is usually infeasible
2. **Integer Precision**: Large XOR operations may need careful handling
3. **State Space**: Complex state machines can have subtle bugs
4. **Optimization Tradeoffs**: Sometimes simpler approaches are better
5. **Edge Case Complexity**: Hard problems often have many edge cases

---

## 🎯 Interview Strategy for Hard Problems

### Preparation Phase:
1. **Master medium problems first** - Build strong foundation
2. **Study classic algorithms** - TSP, assignment problem, game theory
3. **Practice pattern recognition** - Identify when to use each technique
4. **Understand complexity limits** - Know when bitmask DP is feasible

### Problem-Solving Approach:
1. **Understand constraints** - Is n small enough for bitmask DP?
2. **Identify the core problem** - Assignment, optimization, game theory?
3. **Start with brute force** - Get the logic right first
4. **Apply optimizations** - Use appropriate data structures
5. **Handle edge cases** - Empty inputs, single elements, boundary conditions

### Communication Tips:
1. **Explain the intuition** - Why this approach works
2. **Discuss alternatives** - Show you know multiple techniques
3. **Analyze complexity** - Time and space tradeoffs
4. **Test thoroughly** - Walk through examples step by step

---

## 🚀 Beyond Interview Preparation

Hard bit manipulation problems often appear in:
- **Competitive Programming** - Codeforces, TopCoder, AtCoder
- **System Design** - Database indexing, caching strategies
- **Graphics Programming** - Pixel manipulation, compression
- **Cryptography** - Bit-level encryption algorithms
- **Machine Learning** - Feature hashing, dimensionality reduction

---

## 📝 Practice Recommendations

1. **Start with prerequisites** - Master easy and medium problems
2. **Focus on patterns** - Don't just memorize solutions
3. **Time yourself** - Hard problems should take 45-60 minutes
4. **Study editorial solutions** - Learn different approaches
5. **Practice regularly** - Consistency is key for hard problems

Remember: Hard bit manipulation problems test your ability to combine multiple advanced concepts creatively. Focus on understanding the underlying patterns rather than memorizing specific solutions!