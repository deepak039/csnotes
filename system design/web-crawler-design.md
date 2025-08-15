# Design Web Crawler - Interview Guide

## Interview Flow

### 1. Requirements Clarification (5 minutes)

**Interviewer**: "Design a web crawler that can crawl the entire web"

**Candidate Questions to Ask**:
- What's the scale? How many web pages should we crawl?
- What's the crawling frequency? Real-time or batch?
- What data should we extract and store?
- Do we need to respect robots.txt?
- Should we handle different content types (HTML, PDF, images)?
- Do we need duplicate detection?
- Any specific domains to focus on or avoid?
- What about rate limiting and politeness?

**Assumed Requirements**:
- Crawl 1 billion web pages
- Fresh content within 1 week
- Extract text, links, metadata
- Respect robots.txt and rate limits
- Handle HTML primarily
- Duplicate detection needed
- Distributed system

---

### 2. Capacity Estimation (5 minutes)

**Scale Calculations**:
```
Total pages: 1 billion
Crawl frequency: Weekly
Pages per day: 1B / 7 = ~143M pages/day
Pages per second: 143M / (24 * 3600) = ~1,650 pages/sec

Average page size: 100KB
Storage per day: 143M * 100KB = ~14TB/day
Storage per year: 14TB * 365 = ~5PB/year

Bandwidth: 1,650 * 100KB = ~165MB/s
```

**Infrastructure Needs**:
```
Crawling servers: 100+ (assuming 20 pages/sec per server)
Storage: Distributed file system (HDFS, S3)
Queue capacity: 10M+ URLs in queue
Database: Metadata for billions of URLs
```

---

### 3. High-Level Design (10 minutes)

```
[Seed URLs] → [URL Queue] → [Crawler Workers] → [Content Processor]
                   ↑              ↓                      ↓
              [URL Frontier] ← [Link Extractor] → [Storage System]
                   ↑                                     ↓
              [Duplicate Filter] ← [Content Analyzer] ←──┘
```

**Core Components**:
1. **URL Frontier**: Manages URLs to be crawled
2. **Crawler Workers**: Fetch web pages
3. **Content Processor**: Parse and extract data
4. **Link Extractor**: Find new URLs
5. **Duplicate Detector**: Avoid re-crawling
6. **Storage System**: Store crawled content
7. **Scheduler**: Manage crawling frequency

---

### 4. Detailed Design (15 minutes)

#### 4.1 URL Frontier Design

**Interviewer**: "How do you manage billions of URLs to crawl?"

**Multi-Queue Architecture**:
```
URL Frontier
├── Priority Queues (by domain importance)
│   ├── High Priority Queue (news sites, popular domains)
│   ├── Medium Priority Queue (regular websites)
│   └── Low Priority Queue (less important sites)
├── Politeness Queues (per domain)
│   ├── google.com queue
│   ├── facebook.com queue
│   └── ... (one queue per domain)
└── Fresh Queue (recently discovered URLs)
```

**Implementation**:
```python
class URLFrontier:
    def __init__(self):
        self.priority_queues = {
            'high': Queue(),
            'medium': Queue(), 
            'low': Queue()
        }
        self.domain_queues = {}  # domain -> queue
        self.last_crawl_time = {}  # domain -> timestamp
        self.crawl_delay = {}  # domain -> delay_seconds
    
    def add_url(self, url, priority='medium'):
        domain = extract_domain(url)
        
        # Add to priority queue
        self.priority_queues[priority].put(url)
        
        # Add to domain-specific queue for politeness
        if domain not in self.domain_queues:
            self.domain_queues[domain] = Queue()
        self.domain_queues[domain].put(url)
    
    def get_next_url(self):
        # Respect politeness - don't crawl same domain too frequently
        for domain, queue in self.domain_queues.items():
            if not queue.empty():
                last_crawl = self.last_crawl_time.get(domain, 0)
                delay = self.crawl_delay.get(domain, 1)  # default 1 sec
                
                if time.time() - last_crawl >= delay:
                    url = queue.get()
                    self.last_crawl_time[domain] = time.time()
                    return url
        
        return None
```

#### 4.2 Crawler Worker Design

```python
class CrawlerWorker:
    def __init__(self, worker_id):
        self.worker_id = worker_id
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'WebCrawler/1.0 (+http://example.com/bot)'
        })
    
    def crawl_page(self, url):
        try:
            # Check robots.txt
            if not self.is_allowed_by_robots(url):
                return None
            
            # Fetch page
            response = self.session.get(url, timeout=10)
            
            if response.status_code == 200:
                return {
                    'url': url,
                    'content': response.text,
                    'headers': dict(response.headers),
                    'status_code': response.status_code,
                    'crawled_at': datetime.now()
                }
        except Exception as e:
            self.log_error(url, str(e))
            return None
    
    def is_allowed_by_robots(self, url):
        # Check robots.txt cache
        domain = extract_domain(url)
        robots_txt = self.get_robots_txt(domain)
        return robots_txt.can_fetch('*', url)
```

#### 4.3 Content Processing Pipeline

```python
class ContentProcessor:
    def process(self, crawled_page):
        if not crawled_page:
            return
        
        # Extract links
        links = self.extract_links(crawled_page['content'])
        
        # Extract text content
        text_content = self.extract_text(crawled_page['content'])
        
        # Generate content hash for duplicate detection
        content_hash = self.generate_hash(text_content)
        
        # Check for duplicates
        if self.is_duplicate(content_hash):
            return
        
        # Store processed content
        processed_content = {
            'url': crawled_page['url'],
            'title': self.extract_title(crawled_page['content']),
            'text': text_content,
            'links': links,
            'content_hash': content_hash,
            'crawled_at': crawled_page['crawled_at']
        }
        
        # Store in database/file system
        self.store_content(processed_content)
        
        # Add new links to frontier
        self.add_links_to_frontier(links)
    
    def extract_links(self, html_content):
        soup = BeautifulSoup(html_content, 'html.parser')
        links = []
        for link in soup.find_all('a', href=True):
            absolute_url = urljoin(base_url, link['href'])
            if self.is_valid_url(absolute_url):
                links.append(absolute_url)
        return links
```

---

### 5. Deep Dive Questions & Solutions (15 minutes)

#### 5.1 Duplicate Detection

**Interviewer**: "How do you detect duplicate content efficiently?"

**Multi-level Duplicate Detection**:

```python
class DuplicateDetector:
    def __init__(self):
        self.url_bloom_filter = BloomFilter(capacity=1000000000, error_rate=0.1)
        self.content_hash_db = {}  # Redis/Database
        self.simhash_index = {}    # For near-duplicate detection
    
    def is_duplicate_url(self, url):
        # Level 1: Bloom filter (fast, some false positives)
        if url not in self.url_bloom_filter:
            self.url_bloom_filter.add(url)
            return False
        
        # Level 2: Exact URL check in database
        return self.exact_url_exists(url)
    
    def is_duplicate_content(self, content):
        # Level 1: Exact content hash
        content_hash = hashlib.md5(content.encode()).hexdigest()
        if content_hash in self.content_hash_db:
            return True
        
        # Level 2: Near-duplicate detection using SimHash
        simhash = self.calculate_simhash(content)
        for existing_hash in self.simhash_index:
            if self.hamming_distance(simhash, existing_hash) < 3:
                return True  # Near duplicate
        
        # Store hashes
        self.content_hash_db[content_hash] = True
        self.simhash_index[simhash] = True
        return False
```

#### 5.2 Politeness and Rate Limiting

**Interviewer**: "How do you ensure the crawler is polite and doesn't overload servers?"

**Politeness Strategies**:

```python
class PolitenessManager:
    def __init__(self):
        self.domain_delays = {}
        self.robots_cache = {}
        self.request_counts = {}  # domain -> count in time window
    
    def get_crawl_delay(self, domain):
        # Check robots.txt for crawl-delay
        robots = self.get_robots_txt(domain)
        if robots and robots.crawl_delay('*'):
            return robots.crawl_delay('*')
        
        # Adaptive delay based on server response
        if domain in self.domain_delays:
            return self.domain_delays[domain]
        
        return 1  # Default 1 second
    
    def update_delay_based_on_response(self, domain, response_time, status_code):
        current_delay = self.domain_delays.get(domain, 1)
        
        if status_code == 429:  # Too Many Requests
            self.domain_delays[domain] = current_delay * 2
        elif status_code >= 500:  # Server errors
            self.domain_delays[domain] = current_delay * 1.5
        elif response_time > 5:  # Slow response
            self.domain_delays[domain] = current_delay * 1.2
        elif response_time < 1 and status_code == 200:
            # Server responding well, can reduce delay slightly
            self.domain_delays[domain] = max(0.5, current_delay * 0.9)
```

#### 5.3 Handling Dynamic Content

**Interviewer**: "How do you handle JavaScript-heavy websites?"

**Solution: Hybrid Approach**

```python
class SmartCrawler:
    def __init__(self):
        self.simple_crawler = SimpleCrawler()  # requests-based
        self.js_crawler = JSCrawler()          # Selenium/Playwright-based
        self.js_detector = JSDetector()
    
    def crawl_page(self, url):
        # First, try simple crawling
        simple_result = self.simple_crawler.crawl(url)
        
        # Detect if page needs JavaScript
        if self.js_detector.needs_js_rendering(simple_result):
            # Use headless browser for JS-heavy pages
            return self.js_crawler.crawl(url)
        
        return simple_result
    
class JSDetector:
    def needs_js_rendering(self, html_content):
        # Heuristics to detect JS-heavy pages
        js_indicators = [
            'document.write',
            'React',
            'Angular',
            'Vue.js',
            'spa-content',
            len(re.findall(r'<script', html_content)) > 5
        ]
        
        return any(indicator in html_content for indicator in js_indicators)
```

#### 5.4 Distributed Crawling

**Interviewer**: "How do you coordinate multiple crawler instances?"

**Distributed Architecture**:

```
                [Coordinator Service]
                        |
        ┌───────────────┼───────────────┐
        |               |               |
[Crawler Node 1] [Crawler Node 2] [Crawler Node 3]
        |               |               |
[Local URL Queue] [Local URL Queue] [Local URL Queue]
        |               |               |
        └───────── [Shared Storage] ────┘
```

**Implementation**:
```python
class DistributedCrawler:
    def __init__(self, node_id, total_nodes):
        self.node_id = node_id
        self.total_nodes = total_nodes
        self.coordinator = CoordinatorClient()
    
    def should_crawl_url(self, url):
        # Consistent hashing to determine which node handles this URL
        url_hash = hash(url)
        assigned_node = url_hash % self.total_nodes
        return assigned_node == self.node_id
    
    def get_work_assignment(self):
        # Get URLs assigned to this node
        return self.coordinator.get_urls_for_node(self.node_id)
    
    def report_progress(self, crawled_count, queue_size):
        # Report to coordinator for load balancing
        self.coordinator.report_status(self.node_id, {
            'crawled_count': crawled_count,
            'queue_size': queue_size,
            'timestamp': time.time()
        })
```

---

### 6. Scale the Design (10 minutes)

#### 6.1 Massive Scale Architecture

```
                    [Load Balancer]
                          |
                 [Coordinator Cluster]
                          |
        ┌─────────────────┼─────────────────┐
        |                 |                 |
[Crawler Cluster 1] [Crawler Cluster 2] [Crawler Cluster 3]
   (100 nodes)         (100 nodes)         (100 nodes)
        |                 |                 |
        └─────── [Distributed Storage] ─────┘
                 (HDFS/S3 + Databases)
```

#### 6.2 Storage Strategy

**Content Storage**:
```python
class ContentStorage:
    def __init__(self):
        self.metadata_db = CassandraCluster()  # URL metadata
        self.content_store = HDFSCluster()     # Raw HTML content
        self.search_index = ElasticsearchCluster()  # Searchable content
    
    def store_crawled_page(self, page_data):
        # Store metadata in Cassandra
        metadata = {
            'url': page_data['url'],
            'title': page_data['title'],
            'crawled_at': page_data['crawled_at'],
            'content_hash': page_data['content_hash'],
            'file_path': self.generate_file_path(page_data['url'])
        }
        self.metadata_db.insert('crawled_pages', metadata)
        
        # Store raw content in HDFS
        file_path = self.generate_file_path(page_data['url'])
        self.content_store.write(file_path, page_data['content'])
        
        # Index for search
        search_doc = {
            'url': page_data['url'],
            'title': page_data['title'],
            'text': page_data['text'],
            'crawled_at': page_data['crawled_at']
        }
        self.search_index.index('web_pages', search_doc)
```

---

### 7. Address Bottlenecks (5 minutes)

#### 7.1 Common Bottlenecks & Solutions

**URL Frontier Bottleneck**:
- **Problem**: Single queue becomes bottleneck
- **Solution**: Partition queues by domain hash

**Network I/O Bottleneck**:
- **Problem**: Waiting for HTTP responses
- **Solution**: Async I/O, connection pooling

```python
import asyncio
import aiohttp

class AsyncCrawler:
    async def crawl_urls(self, urls):
        async with aiohttp.ClientSession() as session:
            tasks = [self.crawl_single_url(session, url) for url in urls]
            results = await asyncio.gather(*tasks, return_exceptions=True)
            return results
    
    async def crawl_single_url(self, session, url):
        try:
            async with session.get(url, timeout=10) as response:
                content = await response.text()
                return {'url': url, 'content': content}
        except Exception as e:
            return {'url': url, 'error': str(e)}
```

**Storage Bottleneck**:
- **Problem**: Writing to storage becomes slow
- **Solution**: Batch writes, async processing

```python
class BatchProcessor:
    def __init__(self, batch_size=1000):
        self.batch_size = batch_size
        self.batch = []
        self.storage = ContentStorage()
    
    def add_page(self, page_data):
        self.batch.append(page_data)
        if len(self.batch) >= self.batch_size:
            self.flush_batch()
    
    def flush_batch(self):
        # Batch write to storage
        self.storage.batch_write(self.batch)
        self.batch = []
```

---

### 8. Additional Considerations

#### 8.1 Content Quality & Filtering

```python
class ContentFilter:
    def is_quality_content(self, page_data):
        # Filter out low-quality pages
        text_length = len(page_data['text'])
        if text_length < 100:  # Too short
            return False
        
        # Check for spam indicators
        spam_ratio = self.calculate_spam_ratio(page_data['text'])
        if spam_ratio > 0.5:
            return False
        
        # Check content language
        if not self.is_target_language(page_data['text']):
            return False
        
        return True
```

#### 8.2 Monitoring & Alerting

**Key Metrics**:
- Pages crawled per second
- Queue size and growth rate
- Error rates by domain
- Storage utilization
- Duplicate detection rate

#### 8.3 Failure Handling

```python
class FailureHandler:
    def __init__(self):
        self.retry_queue = Queue()
        self.failed_urls = {}
    
    def handle_failed_url(self, url, error):
        retry_count = self.failed_urls.get(url, 0)
        
        if retry_count < 3:  # Max 3 retries
            # Exponential backoff
            delay = 2 ** retry_count
            self.schedule_retry(url, delay)
            self.failed_urls[url] = retry_count + 1
        else:
            # Permanently failed
            self.log_permanent_failure(url, error)
```

---

## Interview Success Tips

### What Interviewers Look For:
1. **Scalability**: Handling billions of URLs
2. **Politeness**: Respecting server resources
3. **Efficiency**: Avoiding duplicates, smart crawling
4. **Robustness**: Handling failures gracefully

### Common Mistakes:
- Not considering politeness/rate limiting
- Ignoring duplicate detection
- Underestimating storage requirements
- Not handling dynamic content

### Key Discussion Points:
- "How do we ensure we're being polite to servers?"
- "What happens when a crawler node fails?"
- "How do we prioritize which pages to crawl first?"
- "How do we handle the scale of billions of pages?"