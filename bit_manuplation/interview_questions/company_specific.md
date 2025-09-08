# Company-Specific Bit Manipulation Questions

## 🏢 FAANG and Top Tech Companies

This section covers bit manipulation questions frequently asked at major tech companies, organized by company and difficulty level.

---

## 🔵 Google / Alphabet

### Easy-Medium:
1. **Number of 1 Bits** - Basic bit counting
2. **Power of Two** - Single bit detection
3. **Single Number** - XOR properties

### Medium-Hard:
4. **Maximum XOR of Two Numbers in Array** - Trie + bit manipulation
5. **Bitwise AND of Numbers Range** - Common prefix finding
6. **UTF-8 Validation** - Bit pattern recognition

```python
# Google Favorite: Maximum XOR with Trie
class TrieNode:
    def __init__(self):
        self.children = {}

def find_maximum_xor_google_style(nums):
    """
    Google often asks for Trie-based solution
    Time: O(32n), Space: O(32n)
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
            # Try to go opposite direction
            opposite = 1 - bit
            
            if opposite in node.children:
                current_xor |= (1 << i)
                node = node.children[opposite]
            else:
                node = node.children[bit]
        
        max_xor = max(max_xor, current_xor)
    
    return max_xor

# Google Follow-up: What if we have updates?
class MaxXORDataStructure:
    """Dynamic maximum XOR with insertions"""
    def __init__(self):
        self.root = TrieNode()
        self.nums = set()
    
    def insert(self, num):
        if num in self.nums:
            return
        
        self.nums.add(num)
        node = self.root
        for i in range(31, -1, -1):
            bit = (num >> i) & 1
            if bit not in node.children:
                node.children[bit] = TrieNode()
            node = node.children[bit]
    
    def get_max_xor(self):
        if len(self.nums) < 2:
            return 0
        return find_maximum_xor_google_style(list(self.nums))
```

---

## 🔴 Meta (Facebook)

### Frequently Asked:
1. **Single Number II** - State machine approach
2. **Gray Code** - Bit manipulation patterns
3. **Subsets** - Bit masking for combinations

### Meta-Specific Patterns:
4. **Social Network Connections** - Using bits to represent relationships
5. **Feature Flags** - Bit manipulation for A/B testing

```python
# Meta Style: Social Network Problem
def mutual_friends_bitmask(user_connections, user1, user2):
    """
    Use bitmask to represent friend connections
    Time: O(1), Space: O(1) for bit operations
    """
    # Each user's connections represented as bitmask
    friends1 = user_connections[user1]
    friends2 = user_connections[user2]
    
    # Mutual friends = intersection of friend sets
    mutual = friends1 & friends2
    
    # Count mutual friends
    return bin(mutual).count('1')

def suggest_friends(user_connections, user_id, min_mutual=2):
    """Suggest friends based on mutual connections"""
    user_friends = user_connections[user_id]
    suggestions = []
    
    for other_user, other_friends in enumerate(user_connections):
        if other_user == user_id or (user_friends & (1 << other_user)):
            continue  # Skip self and existing friends
        
        mutual_count = bin(user_friends & other_friends).count('1')
        if mutual_count >= min_mutual:
            suggestions.append((other_user, mutual_count))
    
    return sorted(suggestions, key=lambda x: x[1], reverse=True)

# Meta Feature Flags System
class FeatureFlags:
    """Bit-based feature flag system"""
    def __init__(self):
        self.flags = 0
    
    def enable_feature(self, feature_id):
        self.flags |= (1 << feature_id)
    
    def disable_feature(self, feature_id):
        self.flags &= ~(1 << feature_id)
    
    def is_enabled(self, feature_id):
        return (self.flags & (1 << feature_id)) != 0
    
    def get_enabled_features(self):
        features = []
        temp = self.flags
        bit_pos = 0
        while temp:
            if temp & 1:
                features.append(bit_pos)
            temp >>= 1
            bit_pos += 1
        return features
```

---

## 🟠 Amazon

### Common Themes:
1. **Bit manipulation for optimization** - Space and time efficiency
2. **System design applications** - Database indexing, caching
3. **Large-scale data processing** - Bit vectors for filtering

### Amazon Favorites:
1. **Missing Number** - Multiple approaches
2. **Counting Bits** - DP optimization
3. **Minimum Flips** - Bit difference problems

```python
# Amazon Style: Large Scale Data Processing
def bloom_filter_operations(items, filter_size=1000000):
    """
    Simplified Bloom filter using bit manipulation
    Used in Amazon's caching systems
    """
    bit_array = 0  # In practice, use actual bit array
    
    def hash_functions(item):
        """Multiple hash functions for Bloom filter"""
        import hashlib
        h1 = int(hashlib.md5(str(item).encode()).hexdigest(), 16) % filter_size
        h2 = int(hashlib.sha1(str(item).encode()).hexdigest(), 16) % filter_size
        h3 = int(hashlib.sha256(str(item).encode()).hexdigest(), 16) % filter_size
        return [h1, h2, h3]
    
    def add_item(item):
        nonlocal bit_array
        for pos in hash_functions(item):
            bit_array |= (1 << pos)
    
    def might_contain(item):
        for pos in hash_functions(item):
            if not (bit_array & (1 << pos)):
                return False
        return True
    
    # Add all items
    for item in items:
        add_item(item)
    
    return might_contain

# Amazon Inventory System
def inventory_bit_operations(products, operations):
    """
    Use bits to track product availability across warehouses
    Each bit represents availability in a warehouse
    """
    inventory = {}  # product_id -> bitmask of warehouses
    
    for product_id in products:
        inventory[product_id] = 0
    
    for op_type, product_id, warehouse_id in operations:
        if op_type == "stock":
            inventory[product_id] |= (1 << warehouse_id)
        elif op_type == "remove":
            inventory[product_id] &= ~(1 << warehouse_id)
    
    def find_available_warehouses(product_id):
        mask = inventory[product_id]
        warehouses = []
        warehouse_id = 0
        while mask:
            if mask & 1:
                warehouses.append(warehouse_id)
            mask >>= 1
            warehouse_id += 1
        return warehouses
    
    def count_total_availability():
        total = 0
        for mask in inventory.values():
            total += bin(mask).count('1')
        return total
    
    return find_available_warehouses, count_total_availability
```

---

## 🍎 Apple

### Focus Areas:
1. **Hardware-level optimizations** - Bit manipulation for performance
2. **Graphics and media processing** - Pixel manipulation
3. **Security applications** - Cryptographic bit operations

### Apple-Style Problems:
1. **Reverse Bits** - Hardware register manipulation
2. **Bit manipulation in graphics** - Color channel operations
3. **Efficient data structures** - Compressed representations

```python
# Apple Style: Graphics Processing
def rgb_bit_manipulation(pixels):
    """
    Manipulate RGB values using bit operations
    Common in Apple's graphics frameworks
    """
    def extract_rgb(pixel):
        """Extract R, G, B from 24-bit pixel"""
        r = (pixel >> 16) & 0xFF
        g = (pixel >> 8) & 0xFF
        b = pixel & 0xFF
        return r, g, b
    
    def combine_rgb(r, g, b):
        """Combine R, G, B into 24-bit pixel"""
        return (r << 16) | (g << 8) | b
    
    def adjust_brightness(pixel, factor):
        """Adjust brightness using bit operations"""
        r, g, b = extract_rgb(pixel)
        r = min(255, int(r * factor))
        g = min(255, int(g * factor))
        b = min(255, int(b * factor))
        return combine_rgb(r, g, b)
    
    def apply_filter(pixels, filter_type):
        """Apply various filters using bit manipulation"""
        result = []
        
        for pixel in pixels:
            if filter_type == "grayscale":
                r, g, b = extract_rgb(pixel)
                gray = int(0.299 * r + 0.587 * g + 0.114 * b)
                result.append(combine_rgb(gray, gray, gray))
            elif filter_type == "red_channel":
                r, _, _ = extract_rgb(pixel)
                result.append(combine_rgb(r, 0, 0))
            elif filter_type == "invert":
                r, g, b = extract_rgb(pixel)
                result.append(combine_rgb(255 - r, 255 - g, 255 - b))
        
        return result
    
    return apply_filter(pixels, "grayscale")

# Apple Hardware Optimization
def bit_manipulation_optimization():
    """Demonstrate hardware-level optimizations Apple might use"""
    
    def fast_multiply_by_power_of_2(n, power):
        """Multiply by 2^power using left shift"""
        return n << power
    
    def fast_divide_by_power_of_2(n, power):
        """Divide by 2^power using right shift"""
        return n >> power
    
    def check_even_odd_fast(n):
        """Check if number is even/odd using bit operation"""
        return "even" if (n & 1) == 0 else "odd"
    
    def swap_without_temp(a, b):
        """Swap without temporary variable"""
        a ^= b
        b ^= a
        a ^= b
        return a, b
    
    return {
        'multiply': fast_multiply_by_power_of_2,
        'divide': fast_divide_by_power_of_2,
        'parity': check_even_odd_fast,
        'swap': swap_without_temp
    }
```

---

## 🟢 Microsoft

### Common Patterns:
1. **System-level programming** - Bit flags and masks
2. **Database optimizations** - Bit vectors for indexing
3. **Algorithm efficiency** - Bit manipulation for performance

### Microsoft Favorites:
1. **Hamming Distance** - Error detection/correction
2. **Binary representation problems** - System programming
3. **Bit manipulation in data structures** - Efficient implementations

```python
# Microsoft Style: System Programming
class BitVector:
    """
    Efficient bit vector implementation
    Used in Microsoft's database systems
    """
    def __init__(self, size):
        self.size = size
        self.bits = [0] * ((size + 31) // 32)  # 32-bit integers
    
    def set_bit(self, pos):
        if 0 <= pos < self.size:
            word_index = pos // 32
            bit_index = pos % 32
            self.bits[word_index] |= (1 << bit_index)
    
    def clear_bit(self, pos):
        if 0 <= pos < self.size:
            word_index = pos // 32
            bit_index = pos % 32
            self.bits[word_index] &= ~(1 << bit_index)
    
    def get_bit(self, pos):
        if 0 <= pos < self.size:
            word_index = pos // 32
            bit_index = pos % 32
            return (self.bits[word_index] & (1 << bit_index)) != 0
        return False
    
    def count_set_bits(self):
        """Count total set bits using Brian Kernighan's algorithm"""
        count = 0
        for word in self.bits:
            temp = word
            while temp:
                temp &= temp - 1
                count += 1
        return count
    
    def bitwise_and(self, other):
        """Bitwise AND with another BitVector"""
        result = BitVector(self.size)
        for i in range(len(self.bits)):
            result.bits[i] = self.bits[i] & other.bits[i]
        return result
    
    def bitwise_or(self, other):
        """Bitwise OR with another BitVector"""
        result = BitVector(self.size)
        for i in range(len(self.bits)):
            result.bits[i] = self.bits[i] | other.bits[i]
        return result

# Microsoft Database Index Simulation
def database_bit_index(records, query_conditions):
    """
    Use bit manipulation for database indexing
    Similar to Microsoft SQL Server optimizations
    """
    # Create bit vectors for each condition
    condition_vectors = {}
    
    for condition in query_conditions:
        condition_vectors[condition] = BitVector(len(records))
    
    # Populate bit vectors based on record properties
    for i, record in enumerate(records):
        for condition in query_conditions:
            if evaluate_condition(record, condition):
                condition_vectors[condition].set_bit(i)
    
    def query_with_and(conditions):
        """Find records matching ALL conditions"""
        if not conditions:
            return []
        
        result_vector = condition_vectors[conditions[0]]
        for condition in conditions[1:]:
            result_vector = result_vector.bitwise_and(condition_vectors[condition])
        
        # Extract matching record indices
        matches = []
        for i in range(len(records)):
            if result_vector.get_bit(i):
                matches.append(i)
        
        return matches
    
    def query_with_or(conditions):
        """Find records matching ANY condition"""
        if not conditions:
            return []
        
        result_vector = condition_vectors[conditions[0]]
        for condition in conditions[1:]:
            result_vector = result_vector.bitwise_or(condition_vectors[condition])
        
        matches = []
        for i in range(len(records)):
            if result_vector.get_bit(i):
                matches.append(i)
        
        return matches
    
    return query_with_and, query_with_or

def evaluate_condition(record, condition):
    """Dummy function to evaluate if record meets condition"""
    # Implementation depends on specific condition format
    return True  # Placeholder
```

---

## 🟡 Netflix

### Streaming-Specific Applications:
1. **Content recommendation** - Bit vectors for user preferences
2. **Video processing** - Bit manipulation for compression
3. **A/B testing** - Feature flags using bits

```python
# Netflix Style: Content Recommendation System
class ContentRecommendationBits:
    """
    Use bit manipulation for efficient content recommendations
    """
    def __init__(self, num_users, num_content):
        self.num_users = num_users
        self.num_content = num_content
        # Each user's preferences as bit vector
        self.user_preferences = [0] * num_users
        # Each content's viewer base as bit vector
        self.content_viewers = [0] * num_content
    
    def user_watched_content(self, user_id, content_id):
        """Mark that user watched content"""
        self.user_preferences[user_id] |= (1 << content_id)
        self.content_viewers[content_id] |= (1 << user_id)
    
    def find_similar_users(self, user_id, min_common=3):
        """Find users with similar preferences"""
        user_prefs = self.user_preferences[user_id]
        similar_users = []
        
        for other_user in range(self.num_users):
            if other_user == user_id:
                continue
            
            # Count common preferences using bit manipulation
            common = user_prefs & self.user_preferences[other_user]
            common_count = bin(common).count('1')
            
            if common_count >= min_common:
                similar_users.append((other_user, common_count))
        
        return sorted(similar_users, key=lambda x: x[1], reverse=True)
    
    def recommend_content(self, user_id, max_recommendations=10):
        """Recommend content based on similar users"""
        user_prefs = self.user_preferences[user_id]
        similar_users = self.find_similar_users(user_id)
        
        recommendations = {}
        
        for similar_user_id, similarity in similar_users[:5]:  # Top 5 similar users
            similar_prefs = self.user_preferences[similar_user_id]
            # Find content similar user likes but current user hasn't watched
            new_content = similar_prefs & (~user_prefs)
            
            # Count recommendations for each content
            content_id = 0
            while new_content:
                if new_content & 1:
                    recommendations[content_id] = recommendations.get(content_id, 0) + similarity
                new_content >>= 1
                content_id += 1
        
        # Sort by recommendation score
        sorted_recs = sorted(recommendations.items(), key=lambda x: x[1], reverse=True)
        return [content_id for content_id, _ in sorted_recs[:max_recommendations]]

# Netflix A/B Testing with Bit Flags
class ABTestingBitFlags:
    """Efficient A/B testing using bit manipulation"""
    
    def __init__(self):
        self.user_flags = {}  # user_id -> bit flags
        self.test_definitions = {}  # test_name -> bit_position
        self.next_bit_position = 0
    
    def create_test(self, test_name):
        """Create new A/B test"""
        if test_name not in self.test_definitions:
            self.test_definitions[test_name] = self.next_bit_position
            self.next_bit_position += 1
    
    def assign_user_to_test(self, user_id, test_name, variant_a=True):
        """Assign user to test variant"""
        if user_id not in self.user_flags:
            self.user_flags[user_id] = 0
        
        bit_pos = self.test_definitions[test_name]
        
        if variant_a:
            self.user_flags[user_id] |= (1 << bit_pos)
        else:
            self.user_flags[user_id] &= ~(1 << bit_pos)
    
    def is_user_in_variant_a(self, user_id, test_name):
        """Check if user is in variant A"""
        if user_id not in self.user_flags:
            return False
        
        bit_pos = self.test_definitions[test_name]
        return (self.user_flags[user_id] & (1 << bit_pos)) != 0
    
    def get_user_tests(self, user_id):
        """Get all tests user is participating in"""
        if user_id not in self.user_flags:
            return {}
        
        user_flag = self.user_flags[user_id]
        tests = {}
        
        for test_name, bit_pos in self.test_definitions.items():
            tests[test_name] = (user_flag & (1 << bit_pos)) != 0
        
        return tests
```

---

## 🎯 Company-Specific Interview Tips

### Google:
- **Focus on scalability** - How does your solution scale?
- **Discuss alternatives** - Multiple approaches to same problem
- **System design integration** - How bit manipulation fits in larger systems

### Meta:
- **User-centric thinking** - How does this help users?
- **Performance optimization** - Why bit manipulation over other approaches?
- **Real-world applications** - Connect to social media scenarios

### Amazon:
- **Customer obsession** - How does efficiency benefit customers?
- **Large-scale thinking** - Solutions for millions of users
- **Cost optimization** - Space and time efficiency

### Apple:
- **Hardware awareness** - Understanding of underlying hardware
- **Performance critical** - Every bit of optimization matters
- **User experience** - How performance affects user experience

### Microsoft:
- **Enterprise solutions** - Scalable, maintainable code
- **System integration** - How it fits with existing systems
- **Backwards compatibility** - Consideration for legacy systems

---

## 📊 Difficulty Distribution by Company

| Company | Easy | Medium | Hard | Focus Area |
|---------|------|--------|------|------------|
| Google | 20% | 50% | 30% | Algorithms & Scale |
| Meta | 25% | 45% | 30% | User Systems |
| Amazon | 30% | 50% | 20% | Efficiency & Scale |
| Apple | 15% | 40% | 45% | Hardware & Performance |
| Microsoft | 25% | 50% | 25% | System Programming |
| Netflix | 20% | 60% | 20% | Data Processing |

---

## 🚀 Preparation Strategy by Company

1. **Research company-specific patterns** - Each company has preferred approaches
2. **Practice relevant applications** - Connect bit manipulation to company's domain
3. **Understand business context** - Why would the company need this optimization?
4. **Prepare follow-up questions** - How to extend or modify the solution
5. **Study company engineering blogs** - Learn about their technical challenges

Remember: Companies often ask bit manipulation questions not just to test algorithmic skills, but to see how you think about efficiency, scalability, and real-world applications!