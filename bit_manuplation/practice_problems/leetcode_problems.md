# LeetCode Bit Manipulation Problems

## 🎯 Curated Problem List by Difficulty

This comprehensive list covers all essential bit manipulation problems on LeetCode, organized by difficulty and topic.

---

## 🟢 Easy Problems (Foundation Building)

### Core Bit Operations
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 191 | [Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/) | Easy | Brian Kernighan's Algorithm | O(k) | O(1) |
| 231 | [Power of Two](https://leetcode.com/problems/power-of-two/) | Easy | Single Bit Detection | O(1) | O(1) |
| 342 | [Power of Four](https://leetcode.com/problems/power-of-four/) | Easy | Bit Pattern Recognition | O(1) | O(1) |
| 136 | [Single Number](https://leetcode.com/problems/single-number/) | Easy | XOR Properties | O(n) | O(1) |
| 268 | [Missing Number](https://leetcode.com/problems/missing-number/) | Easy | XOR/Sum Formula | O(n) | O(1) |
| 190 | [Reverse Bits](https://leetcode.com/problems/reverse-bits/) | Easy | Bit Manipulation | O(32) | O(1) |
| 476 | [Number Complement](https://leetcode.com/problems/number-complement/) | Easy | Bit Flipping | O(log n) | O(1) |
| 461 | [Hamming Distance](https://leetcode.com/problems/hamming-distance/) | Easy | XOR + Bit Counting | O(log n) | O(1) |

### Binary String Operations
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 67 | [Add Binary](https://leetcode.com/problems/add-binary/) | Easy | Binary Arithmetic | O(max(m,n)) | O(max(m,n)) |
| 693 | [Binary Number with Alternating Bits](https://leetcode.com/problems/binary-number-with-alternating-bits/) | Easy | Bit Pattern Check | O(1) | O(1) |
| 1009 | [Complement of Base 10 Integer](https://leetcode.com/problems/complement-of-base-10-integer/) | Easy | Bit Complement | O(log n) | O(1) |
| 1342 | [Number of Steps to Reduce a Number to Zero](https://leetcode.com/problems/number-of-steps-to-reduce-a-number-to-zero/) | Easy | Bit Operations | O(log n) | O(1) |

---

## 🟡 Medium Problems (Pattern Recognition)

### XOR and Single Number Variants
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 137 | [Single Number II](https://leetcode.com/problems/single-number-ii/) | Medium | State Machine | O(n) | O(1) |
| 260 | [Single Number III](https://leetcode.com/problems/single-number-iii/) | Medium | Bit Grouping | O(n) | O(1) |
| 389 | [Find the Difference](https://leetcode.com/problems/find-the-difference/) | Medium | XOR Application | O(n) | O(1) |
| 1318 | [Minimum Flips to Make a OR b Equal c](https://leetcode.com/problems/minimum-flips-to-make-a-or-b-equal-c/) | Medium | Bit Analysis | O(log max(a,b,c)) | O(1) |

### Bit Counting and DP
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 338 | [Counting Bits](https://leetcode.com/problems/counting-bits/) | Medium | DP with Bits | O(n) | O(n) |
| 201 | [Bitwise AND of Numbers Range](https://leetcode.com/problems/bitwise-and-of-numbers-range/) | Medium | Common Prefix | O(log n) | O(1) |
| 371 | [Sum of Two Integers](https://leetcode.com/problems/sum-of-two-integers/) | Medium | Bit Arithmetic | O(1) | O(1) |
| 477 | [Total Hamming Distance](https://leetcode.com/problems/total-hamming-distance/) | Medium | Bit Position Analysis | O(32n) | O(1) |

### Subset Generation and Masking
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 78 | [Subsets](https://leetcode.com/problems/subsets/) | Medium | Bit Masking | O(n × 2^n) | O(2^n) |
| 90 | [Subsets II](https://leetcode.com/problems/subsets-ii/) | Medium | Bit Masking + Dedup | O(n × 2^n) | O(2^n) |
| 1239 | [Maximum Length of a Concatenated String with Unique Characters](https://leetcode.com/problems/maximum-length-of-a-concatenated-string-with-unique-characters/) | Medium | Bitmask DP | O(2^n) | O(2^n) |
| 1255 | [Maximum Score Words Formed by Letters](https://leetcode.com/problems/maximum-score-words-formed-by-letters/) | Medium | Subset Enumeration | O(2^n × m) | O(1) |

### Advanced Bit Manipulation
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 89 | [Gray Code](https://leetcode.com/problems/gray-code/) | Medium | Gray Code Generation | O(2^n) | O(2^n) |
| 187 | [Repeated DNA Sequences](https://leetcode.com/problems/repeated-dna-sequences/) | Medium | Rolling Hash | O(n) | O(n) |
| 393 | [UTF-8 Validation](https://leetcode.com/problems/utf-8-validation/) | Medium | Bit Pattern Matching | O(n) | O(1) |
| 401 | [Binary Watch](https://leetcode.com/problems/binary-watch/) | Medium | Bit Counting | O(1) | O(1) |

---

## 🔴 Hard Problems (Advanced Techniques)

### Maximum XOR Problems
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 421 | [Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/) | Hard | Trie + XOR | O(32n) | O(32n) |
| 1707 | [Maximum XOR With an Element From Array](https://leetcode.com/problems/maximum-xor-with-an-element-from-array/) | Hard | Trie + Constraints | O(32n + 32q) | O(32n) |
| 1938 | [Maximum Genetic Difference Query](https://leetcode.com/problems/maximum-genetic-difference-query/) | Hard | Trie + DFS | O(32n + 32q) | O(32n) |

### Bitmask Dynamic Programming
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 943 | [Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/) | Hard | Bitmask DP + TSP | O(n^2 × 2^n) | O(n × 2^n) |
| 1879 | [Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) | Hard | Assignment Problem | O(n × 2^n) | O(2^n) |
| 1595 | [Minimum Cost to Connect Two Groups of Points](https://leetcode.com/problems/minimum-cost-to-connect-two-groups-of-points/) | Hard | Bitmask DP | O(m × n × 2^n) | O(n × 2^n) |
| 1986 | [Minimum Number of Work Sessions to Finish the Tasks](https://leetcode.com/problems/minimum-number-of-work-sessions-to-finish-the-tasks/) | Hard | Subset DP | O(3^n) | O(2^n) |

### Complex Bit Operations
| # | Problem | Difficulty | Key Concept | Time | Space |
|---|---------|------------|-------------|------|-------|
| 1397 | [Find All Good Strings](https://leetcode.com/problems/find-all-good-strings/) | Hard | KMP + DP | O(n × m × k) | O(n × m × k) |
| 1542 | [Find Longest Awesome Substring](https://leetcode.com/problems/find-longest-awesome-substring/) | Hard | Bitmask + Prefix | O(n) | O(2^10) |
| 1915 | [Number of Wonderful Substrings](https://leetcode.com/problems/number-of-wonderful-substrings/) | Hard | Bitmask Prefix Sum | O(n) | O(2^10) |

---

## 🎯 Problem Categories and Patterns

### 1. Basic Bit Operations (Easy)
**Focus**: Understanding fundamental bit manipulation
- **Key Problems**: 191, 231, 136, 268
- **Practice Goal**: Master basic bit tricks
- **Time Investment**: 1-2 weeks

### 2. XOR Applications (Easy-Medium)
**Focus**: Leveraging XOR properties for problem solving
- **Key Problems**: 136, 137, 260, 389
- **Practice Goal**: Recognize XOR patterns
- **Time Investment**: 1 week

### 3. Bit Counting and Analysis (Medium)
**Focus**: Efficient bit counting and range operations
- **Key Problems**: 338, 201, 477, 401
- **Practice Goal**: Optimize counting algorithms
- **Time Investment**: 1-2 weeks

### 4. Subset Generation (Medium)
**Focus**: Using bitmasks to represent and generate subsets
- **Key Problems**: 78, 90, 1239, 1255
- **Practice Goal**: Master subset enumeration
- **Time Investment**: 2 weeks

### 5. Advanced XOR with Trie (Hard)
**Focus**: Combining Trie data structure with XOR operations
- **Key Problems**: 421, 1707, 1938
- **Practice Goal**: Build and query XOR tries
- **Time Investment**: 2-3 weeks

### 6. Bitmask Dynamic Programming (Hard)
**Focus**: Using bitmasks to represent states in DP
- **Key Problems**: 943, 1879, 1595
- **Practice Goal**: Solve NP-hard problems optimally for small inputs
- **Time Investment**: 3-4 weeks

---

## 📚 Learning Path Recommendations

### Beginner Path (4-6 weeks)
```
Week 1-2: Basic Operations
- Problems: 191, 231, 342, 136, 268, 190, 476, 461
- Focus: Understand binary representation and basic tricks

Week 3-4: XOR and Simple Applications  
- Problems: 137, 260, 389, 67, 693, 1009
- Focus: Master XOR properties and applications

Week 5-6: Bit Counting and Medium Problems
- Problems: 338, 201, 371, 477, 78, 89
- Focus: Efficient algorithms and pattern recognition
```

### Intermediate Path (6-8 weeks)
```
Week 1-2: Review Basics + Advanced Easy
- All easy problems + 338, 201, 371

Week 3-4: Subset Generation and Masking
- Problems: 78, 90, 1239, 1255, 401, 187

Week 5-6: Complex Bit Manipulation
- Problems: 89, 393, 477, 1318, 1542

Week 7-8: Introduction to Hard Problems
- Problems: 421, 943 (simplified versions)
```

### Advanced Path (8-12 weeks)
```
Week 1-4: Master All Easy and Medium
- Complete all easy and medium problems
- Focus on multiple solution approaches

Week 5-8: Hard Problems - XOR with Trie
- Problems: 421, 1707, 1938
- Build strong foundation in Trie + XOR

Week 9-12: Bitmask DP and Complex Problems
- Problems: 943, 1879, 1595, 1986
- Master advanced DP techniques
```

---

## 🎯 Practice Strategy

### Daily Practice Schedule
```
Beginner: 1-2 problems per day
Intermediate: 2-3 problems per day  
Advanced: 1 hard problem every 2-3 days + review
```

### Weekly Goals
```
Week 1: Master bit representation and basic operations
Week 2: Understand XOR properties thoroughly
Week 3: Learn bit counting algorithms
Week 4: Practice subset generation
Week 5: Tackle medium complexity problems
Week 6+: Focus on hard problems and optimization
```

### Problem Selection Strategy
1. **Start with fundamentals** - Don't skip easy problems
2. **Group by pattern** - Solve similar problems together
3. **Time yourself** - Easy (10-15 min), Medium (20-30 min), Hard (45-60 min)
4. **Review solutions** - Learn multiple approaches
5. **Practice regularly** - Consistency over intensity

---

## 📊 Difficulty Progression

```
Easy Problems (20-30 problems):
├── Basic bit operations (8-10 problems)
├── XOR applications (5-7 problems)
├── Binary string operations (4-6 problems)
└── Simple bit counting (3-5 problems)

Medium Problems (25-35 problems):
├── Advanced XOR (6-8 problems)
├── Bit counting and DP (6-8 problems)
├── Subset generation (6-8 problems)
└── Complex bit manipulation (7-11 problems)

Hard Problems (15-25 problems):
├── Maximum XOR with Trie (4-6 problems)
├── Bitmask DP (6-8 problems)
└── Complex algorithms (5-11 problems)
```

---

## 🚀 Success Metrics

### Beginner Level Success:
- ✅ Solve 80%+ of easy problems without hints
- ✅ Understand binary representation intuitively
- ✅ Recognize when to use XOR
- ✅ Implement basic bit operations from memory

### Intermediate Level Success:
- ✅ Solve 70%+ of medium problems independently
- ✅ Design bitmask solutions for subset problems
- ✅ Optimize bit counting algorithms
- ✅ Explain time/space complexity accurately

### Advanced Level Success:
- ✅ Solve 60%+ of hard problems with minimal hints
- ✅ Implement Trie + XOR solutions efficiently
- ✅ Design bitmask DP for complex problems
- ✅ Optimize solutions for competitive programming

---

## 💡 Pro Tips for LeetCode Practice

1. **Read constraints carefully** - They often hint at the approach
2. **Start with brute force** - Then optimize using bit manipulation
3. **Draw examples** - Visualize bit patterns for small inputs
4. **Test edge cases** - Zero, negative numbers, single elements
5. **Learn from discussions** - Study different solution approaches
6. **Time complexity matters** - Bit manipulation often provides optimal solutions
7. **Practice mental math** - Quick binary conversions help in interviews
8. **Understand the why** - Don't just memorize bit tricks

Remember: Consistent practice with gradual difficulty increase is key to mastering bit manipulation problems on LeetCode!