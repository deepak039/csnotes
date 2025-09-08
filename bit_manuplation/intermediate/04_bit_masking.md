# Bit Masking and Subset Generation

## 🎯 Core Concept: Bit Masking

Bit masking uses binary representation to represent subsets, states, or selections efficiently.

```
For set {A, B, C}:
000 = {} (empty set)
001 = {C}
010 = {B}  
011 = {B, C}
100 = {A}
101 = {A, C}
110 = {A, B}
111 = {A, B, C}
```

## 🔍 Basic Bit Masking Operations

### Check if Element is in Subset
```python
def is_element_in_subset(mask, position):
    """Check if element at position is in the subset"""
    return (mask & (1 << position)) != 0

# Example: mask = 5 (101), check position 2
# 5 & (1 << 2) = 101 & 100 = 100 ≠ 0, so element is present
```

### Add Element to Subset
```python
def add_element(mask, position):
    """Add element at position to subset"""
    return mask | (1 << position)

# Example: mask = 5 (101), add position 1
# 5 | (1 << 1) = 101 | 010 = 111 = 7
```

### Remove Element from Subset
```python
def remove_element(mask, position):
    """Remove element at position from subset"""
    return mask & ~(1 << position)

# Example: mask = 7 (111), remove position 1  
# 7 & ~(1 << 1) = 111 & 101 = 101 = 5
```

### Toggle Element in Subset
```python
def toggle_element(mask, position):
    """Toggle element at position in subset"""
    return mask ^ (1 << position)
```

## 🎨 Subset Generation

### Generate All Subsets
```python
def generate_all_subsets(arr):
    """Generate all 2^n subsets using bit masking"""
    n = len(arr)
    all_subsets = []
    
    for mask in range(1 << n):  # 0 to 2^n - 1
        subset = []
        for i in range(n):
            if mask & (1 << i):
                subset.append(arr[i])
        all_subsets.append(subset)
    
    return all_subsets

# Time: O(n * 2^n), Space: O(2^n)
```

### Generate Subsets of Specific Size
```python
def generate_subsets_of_size_k(arr, k):
    """Generate all subsets of size k"""
    n = len(arr)
    subsets = []
    
    for mask in range(1 << n):
        if bin(mask).count('1') == k:  # Check if k bits are set
            subset = []
            for i in range(n):
                if mask & (1 << i):
                    subset.append(arr[i])
            subsets.append(subset)
    
    return subsets
```

### Iterate Through All Subsets of a Mask
```python
def iterate_subsets(mask):
    """Iterate through all subsets of given mask"""
    subsets = []
    submask = mask
    
    while submask > 0:
        subsets.append(submask)
        submask = (submask - 1) & mask
    
    subsets.append(0)  # Empty subset
    return subsets

# Example: mask = 5 (101)
# Subsets: 5 (101), 4 (100), 1 (001), 0 (000)
```

## 🧮 Dynamic Programming with Bitmasks

### Traveling Salesman Problem (TSP)
```python
def tsp_dp(dist):
    """Solve TSP using bitmask DP"""
    n = len(dist)
    # dp[mask][i] = minimum cost to visit all cities in mask, ending at city i
    dp = [[float('inf')] * n for _ in range(1 << n)]
    
    # Base case: start at city 0
    dp[1][0] = 0
    
    for mask in range(1 << n):
        for u in range(n):
            if not (mask & (1 << u)):
                continue
            
            for v in range(n):
                if mask & (1 << v):
                    continue
                
                new_mask = mask | (1 << v)
                dp[new_mask][v] = min(dp[new_mask][v], 
                                    dp[mask][u] + dist[u][v])
    
    # Find minimum cost to return to start
    full_mask = (1 << n) - 1
    result = float('inf')
    for i in range(1, n):
        result = min(result, dp[full_mask][i] + dist[i][0])
    
    return result
```

### Assignment Problem
```python
def min_cost_assignment(cost):
    """Minimum cost to assign n tasks to n people"""
    n = len(cost)
    # dp[mask] = minimum cost to assign tasks in mask
    dp = [float('inf')] * (1 << n)
    dp[0] = 0
    
    for mask in range(1 << n):
        if dp[mask] == float('inf'):
            continue
        
        person = bin(mask).count('1')  # Next person to assign
        if person == n:
            continue
        
        for task in range(n):
            if not (mask & (1 << task)):  # Task not assigned
                new_mask = mask | (1 << task)
                dp[new_mask] = min(dp[new_mask], 
                                 dp[mask] + cost[person][task])
    
    return dp[(1 << n) - 1]
```

### Subset Sum with Bitmask
```python
def subset_sum_bitmask(nums, target):
    """Find if any subset sums to target using bitmask"""
    n = len(nums)
    
    for mask in range(1 << n):
        subset_sum = 0
        for i in range(n):
            if mask & (1 << i):
                subset_sum += nums[i]
        
        if subset_sum == target:
            return True
    
    return False

# More efficient DP approach:
def subset_sum_dp(nums, target):
    """DP approach for subset sum"""
    dp = [False] * (target + 1)
    dp[0] = True
    
    for num in nums:
        for j in range(target, num - 1, -1):
            dp[j] = dp[j] or dp[j - num]
    
    return dp[target]
```

## 🎯 Advanced Bitmask Problems

### Hamiltonian Path
```python
def hamiltonian_path(graph):
    """Check if Hamiltonian path exists"""
    n = len(graph)
    # dp[mask][i] = True if we can visit all nodes in mask ending at i
    dp = [[False] * n for _ in range(1 << n)]
    
    # Base case: start from each node
    for i in range(n):
        dp[1 << i][i] = True
    
    for mask in range(1 << n):
        for u in range(n):
            if not dp[mask][u]:
                continue
            
            for v in range(n):
                if (mask & (1 << v)) or not graph[u][v]:
                    continue
                
                new_mask = mask | (1 << v)
                dp[new_mask][v] = True
    
    # Check if any path visits all nodes
    full_mask = (1 << n) - 1
    return any(dp[full_mask][i] for i in range(n))
```

### Maximum Score Words
```python
def max_score_words(words, letters, score):
    """Maximum score from forming words with given letters"""
    from collections import Counter
    
    def get_score(word_mask):
        letter_count = Counter()
        total_score = 0
        
        for i in range(len(words)):
            if word_mask & (1 << i):
                for char in words[i]:
                    letter_count[char] += 1
                    total_score += score[ord(char) - ord('a')]
        
        # Check if we have enough letters
        available = Counter(letters)
        for char, count in letter_count.items():
            if count > available[char]:
                return 0
        
        return total_score
    
    max_score = 0
    for mask in range(1 << len(words)):
        max_score = max(max_score, get_score(mask))
    
    return max_score
```

### Shortest Superstring
```python
def shortest_superstring(words):
    """Find shortest string containing all words as substrings"""
    n = len(words)
    
    # Precompute overlap between words
    overlap = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            if i != j:
                for k in range(min(len(words[i]), len(words[j])), 0, -1):
                    if words[i][-k:] == words[j][:k]:
                        overlap[i][j] = k
                        break
    
    # DP: dp[mask][i] = minimum length to visit all words in mask, ending at i
    dp = [[float('inf')] * n for _ in range(1 << n)]
    parent = [[-1] * n for _ in range(1 << n)]
    
    # Base case
    for i in range(n):
        dp[1 << i][i] = len(words[i])
    
    for mask in range(1 << n):
        for i in range(n):
            if not (mask & (1 << i)) or dp[mask][i] == float('inf'):
                continue
            
            for j in range(n):
                if mask & (1 << j):
                    continue
                
                new_mask = mask | (1 << j)
                new_length = dp[mask][i] + len(words[j]) - overlap[i][j]
                
                if new_length < dp[new_mask][j]:
                    dp[new_mask][j] = new_length
                    parent[new_mask][j] = i
    
    # Find optimal ending
    full_mask = (1 << n) - 1
    min_length = min(dp[full_mask])
    end = dp[full_mask].index(min_length)
    
    # Reconstruct path
    path = []
    mask = full_mask
    curr = end
    
    while curr != -1:
        path.append(curr)
        next_curr = parent[mask][curr]
        mask ^= (1 << curr)
        curr = next_curr
    
    path.reverse()
    
    # Build result string
    result = words[path[0]]
    for i in range(1, len(path)):
        prev, curr = path[i-1], path[i]
        result += words[curr][overlap[prev][curr]:]
    
    return result
```

## 🔧 Optimization Techniques

### Efficient Subset Iteration
```python
def iterate_k_subsets(n, k):
    """Iterate through all subsets of size k efficiently"""
    # Generate first combination
    mask = (1 << k) - 1
    
    while mask < (1 << n):
        yield mask
        
        # Generate next combination (Gosper's hack)
        c = mask
        c = (c & -c)
        r = mask + c
        mask = (((r ^ mask) >> 2) // c) | r
```

### Parallel Bit Operations
```python
def parallel_subset_operations(masks):
    """Perform operations on multiple subsets in parallel"""
    # Use SIMD-like operations for multiple masks
    result = []
    
    for i in range(0, len(masks), 64):  # Process in chunks
        chunk = masks[i:i+64]
        # Perform parallel operations
        processed = [process_mask(mask) for mask in chunk]
        result.extend(processed)
    
    return result
```

## 📊 Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Generate all subsets | O(n × 2ⁿ) | O(2ⁿ) | Exponential |
| TSP with bitmask | O(n² × 2ⁿ) | O(n × 2ⁿ) | Better than O(n!) |
| Subset iteration | O(2ⁿ) | O(1) | Per subset |
| Assignment problem | O(n × 2ⁿ) | O(2ⁿ) | Optimal for small n |

## 🎯 Common Patterns

### Pattern 1: State Representation
```python
# Use bits to represent which elements are selected/visited
state = 0
state |= (1 << i)  # Select element i
if state & (1 << i):  # Check if element i is selected
```

### Pattern 2: Transition Between States
```python
# From current state, try all possible next states
for next_element in range(n):
    if not (current_state & (1 << next_element)):
        new_state = current_state | (1 << next_element)
        # Process new_state
```

### Pattern 3: Subset Enumeration
```python
# Enumerate all subsets of a given set
for mask in range(1 << n):
    # Process subset represented by mask
    subset = [i for i in range(n) if mask & (1 << i)]
```

## 🚨 Common Pitfalls

1. **Integer Overflow**: Be careful with large n (typically n ≤ 20)
2. **Off-by-one Errors**: Remember 0-indexing for bit positions
3. **Memory Usage**: 2ⁿ space can be huge for large n
4. **Time Complexity**: Exponential algorithms don't scale
5. **Bit Order**: Consistent bit position interpretation

## 📝 Practice Problems

### Easy:
1. **Subsets** (LeetCode 78)
2. **Power Set** generation
3. **Binary representation** problems

### Medium:
1. **Partition to K Equal Sum Subsets** (LeetCode 698)
2. **Fair Distribution of Cookies** (LeetCode 2305)
3. **Maximum Score Words** (LeetCode 1255)

### Hard:
1. **Traveling Salesman Problem**
2. **Shortest Superstring** (LeetCode 943)
3. **Minimum Cost to Connect Two Groups** (LeetCode 1595)

## 🎯 Interview Tips

- **Start small**: Explain with 3-4 elements first
- **Draw diagrams**: Show bit representations visually
- **Mention constraints**: Bitmask DP works for small n (≤ 20)
- **Optimize when possible**: Use subset iteration tricks
- **Consider alternatives**: Sometimes other approaches are better
- **Handle edge cases**: Empty sets, single elements

## 🚀 Advanced Applications

- **Game Theory**: Represent game states
- **Graph Algorithms**: Hamiltonian paths, vertex covers
- **Optimization**: Assignment, scheduling problems
- **Combinatorics**: Generating combinations efficiently
- **Machine Learning**: Feature selection, ensemble methods

Bit masking is a powerful technique that can solve seemingly impossible problems with elegant solutions!