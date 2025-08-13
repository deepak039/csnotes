# System Design Patterns

## 🏗️ Microservices Architecture

### Definition
Architectural style that structures an application as a collection of loosely coupled, independently deployable services.

### Core Principles
1. **Single Responsibility**: Each service has one business capability
2. **Decentralized**: Services manage their own data and business logic
3. **Fault Isolation**: Failure in one service doesn't cascade
4. **Technology Diversity**: Different services can use different technologies

### Microservices vs Monolith

#### Monolithic Architecture
```
┌─────────────────────────────────┐
│         Monolithic App          │
├─────────────────────────────────┤
│  User Service │ Order Service   │
│  Product Svc  │ Payment Service │
│  Inventory    │ Notification    │
└─────────────────────────────────┘
         │
    ┌─────────┐
    │Database │
    └─────────┘
```

#### Microservices Architecture
```
┌──────────┐  ┌──────────┐  ┌──────────┐
│User Svc  │  │Order Svc │  │Product   │
│    │     │  │    │     │  │Svc   │   │
│ ┌─────┐  │  │ ┌─────┐  │  │ ┌─────┐  │
│ │ DB  │  │  │ │ DB  │  │  │ │ DB  │  │
│ └─────┘  │  │ └─────┘  │  │ └─────┘  │
└──────────┘  └──────────┘  └──────────┘
```

### Service Communication Patterns

#### 1. Synchronous Communication (REST/gRPC)
```python
# REST API call
import requests

class OrderService:
    def create_order(self, user_id, items):
        # Validate user
        user_response = requests.get(f"http://user-service/users/{user_id}")
        if user_response.status_code != 200:
            raise UserNotFound()
        
        # Check inventory
        inventory_response = requests.post(
            "http://inventory-service/check",
            json={"items": items}
        )
        if not inventory_response.json()["available"]:
            raise InsufficientInventory()
        
        # Create order
        order = self.create_order_record(user_id, items)
        return order
```

#### 2. Asynchronous Communication (Message Queues)
```python
# Event-driven communication
import pika

class OrderService:
    def __init__(self):
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters('rabbitmq')
        )
        self.channel = self.connection.channel()
    
    def create_order(self, user_id, items):
        # Create order
        order = self.create_order_record(user_id, items)
        
        # Publish events
        self.publish_event("order.created", {
            "order_id": order.id,
            "user_id": user_id,
            "items": items,
            "total": order.total
        })
        
        return order
    
    def publish_event(self, event_type, data):
        self.channel.basic_publish(
            exchange='orders',
            routing_key=event_type,
            body=json.dumps(data)
        )
```

### Service Discovery
```python
# Service registry pattern
class ServiceRegistry:
    def __init__(self):
        self.services = {}
    
    def register(self, service_name, host, port, health_check_url):
        self.services[service_name] = {
            'instances': [],
            'health_check': health_check_url
        }
        self.add_instance(service_name, host, port)
    
    def add_instance(self, service_name, host, port):
        instance = {
            'host': host,
            'port': port,
            'healthy': True,
            'last_check': time.time()
        }
        self.services[service_name]['instances'].append(instance)
    
    def get_instance(self, service_name):
        healthy_instances = [
            instance for instance in self.services[service_name]['instances']
            if instance['healthy']
        ]
        return random.choice(healthy_instances) if healthy_instances else None
```

---

## 📊 Event Sourcing

### Definition
Stores all changes to application state as a sequence of events, rather than storing current state.

### Core Concepts
- **Events**: Immutable facts about what happened
- **Event Store**: Append-only log of events
- **Projections**: Read models built from events
- **Snapshots**: Periodic state captures for performance

### Event Sourcing Implementation
```python
# Event classes
class Event:
    def __init__(self, aggregate_id, event_type, data, timestamp=None):
        self.aggregate_id = aggregate_id
        self.event_type = event_type
        self.data = data
        self.timestamp = timestamp or datetime.utcnow()
        self.version = None

class UserRegistered(Event):
    def __init__(self, user_id, email, name):
        super().__init__(user_id, "UserRegistered", {
            "email": email,
            "name": name
        })

class UserEmailChanged(Event):
    def __init__(self, user_id, old_email, new_email):
        super().__init__(user_id, "UserEmailChanged", {
            "old_email": old_email,
            "new_email": new_email
        })

# Event Store
class EventStore:
    def __init__(self):
        self.events = []  # In production, use database
    
    def append_events(self, aggregate_id, events, expected_version):
        # Check optimistic concurrency
        current_version = self.get_current_version(aggregate_id)
        if current_version != expected_version:
            raise ConcurrencyException()
        
        # Append events
        for i, event in enumerate(events):
            event.version = current_version + i + 1
            self.events.append(event)
    
    def get_events(self, aggregate_id, from_version=0):
        return [
            event for event in self.events
            if event.aggregate_id == aggregate_id and event.version > from_version
        ]
    
    def get_current_version(self, aggregate_id):
        events = [e for e in self.events if e.aggregate_id == aggregate_id]
        return max([e.version for e in events]) if events else 0

# Aggregate
class User:
    def __init__(self, user_id):
        self.id = user_id
        self.email = None
        self.name = None
        self.version = 0
        self.uncommitted_events = []
    
    def register(self, email, name):
        if self.email is not None:
            raise UserAlreadyRegistered()
        
        event = UserRegistered(self.id, email, name)
        self.apply_event(event)
        self.uncommitted_events.append(event)
    
    def change_email(self, new_email):
        if self.email is None:
            raise UserNotRegistered()
        
        old_email = self.email
        event = UserEmailChanged(self.id, old_email, new_email)
        self.apply_event(event)
        self.uncommitted_events.append(event)
    
    def apply_event(self, event):
        if event.event_type == "UserRegistered":
            self.email = event.data["email"]
            self.name = event.data["name"]
        elif event.event_type == "UserEmailChanged":
            self.email = event.data["new_email"]
        
        self.version += 1
    
    @classmethod
    def from_events(cls, user_id, events):
        user = cls(user_id)
        for event in events:
            user.apply_event(event)
        return user
```

### CQRS (Command Query Responsibility Segregation)
```python
# Command side (Write model)
class UserCommandHandler:
    def __init__(self, event_store):
        self.event_store = event_store
    
    def handle_register_user(self, command):
        user = User(command.user_id)
        user.register(command.email, command.name)
        
        self.event_store.append_events(
            user.id,
            user.uncommitted_events,
            user.version - len(user.uncommitted_events)
        )

# Query side (Read model)
class UserProjection:
    def __init__(self):
        self.users = {}  # In production, use database
    
    def handle_user_registered(self, event):
        self.users[event.aggregate_id] = {
            "id": event.aggregate_id,
            "email": event.data["email"],
            "name": event.data["name"],
            "created_at": event.timestamp
        }
    
    def handle_user_email_changed(self, event):
        if event.aggregate_id in self.users:
            self.users[event.aggregate_id]["email"] = event.data["new_email"]
    
    def get_user(self, user_id):
        return self.users.get(user_id)
    
    def get_users_by_email_domain(self, domain):
        return [
            user for user in self.users.values()
            if user["email"].endswith(f"@{domain}")
        ]
```

---

## 🔄 Circuit Breaker Pattern

### Definition
Prevents cascading failures by monitoring service calls and "opening" the circuit when failure rate exceeds threshold.

### Circuit States
1. **Closed**: Normal operation, requests pass through
2. **Open**: Requests fail immediately, service not called
3. **Half-Open**: Limited requests allowed to test service recovery

### Implementation
```python
import time
import threading
from enum import Enum

class CircuitState(Enum):
    CLOSED = "CLOSED"
    OPEN = "OPEN"
    HALF_OPEN = "HALF_OPEN"

class CircuitBreaker:
    def __init__(self, 
                 failure_threshold=5,
                 recovery_timeout=60,
                 expected_exception=Exception):
        self.failure_threshold = failure_threshold
        self.recovery_timeout = recovery_timeout
        self.expected_exception = expected_exception
        
        self.failure_count = 0
        self.last_failure_time = None
        self.state = CircuitState.CLOSED
        self.lock = threading.Lock()
    
    def __call__(self, func):
        def wrapper(*args, **kwargs):
            return self.call(func, *args, **kwargs)
        return wrapper
    
    def call(self, func, *args, **kwargs):
        with self.lock:
            if self.state == CircuitState.OPEN:
                if self._should_attempt_reset():
                    self.state = CircuitState.HALF_OPEN
                else:
                    raise CircuitBreakerOpenException(
                        f"Circuit breaker is OPEN. Last failure: {self.last_failure_time}"
                    )
            
            try:
                result = func(*args, **kwargs)
                self._on_success()
                return result
            except self.expected_exception as e:
                self._on_failure()
                raise e
    
    def _should_attempt_reset(self):
        return (time.time() - self.last_failure_time) > self.recovery_timeout
    
    def _on_success(self):
        self.failure_count = 0
        self.state = CircuitState.CLOSED
    
    def _on_failure(self):
        self.failure_count += 1
        self.last_failure_time = time.time()
        
        if self.failure_count >= self.failure_threshold:
            self.state = CircuitState.OPEN

# Usage example
@CircuitBreaker(failure_threshold=3, recovery_timeout=30)
def call_external_service():
    response = requests.get("http://external-service/api")
    if response.status_code != 200:
        raise ServiceUnavailableException()
    return response.json()
```

### Advanced Circuit Breaker with Metrics
```python
class AdvancedCircuitBreaker:
    def __init__(self, 
                 failure_threshold=0.5,  # 50% failure rate
                 minimum_requests=10,
                 window_size=60):  # 60 seconds
        self.failure_threshold = failure_threshold
        self.minimum_requests = minimum_requests
        self.window_size = window_size
        
        self.requests = []  # (timestamp, success)
        self.state = CircuitState.CLOSED
        self.lock = threading.Lock()
    
    def call(self, func, *args, **kwargs):
        with self.lock:
            self._cleanup_old_requests()
            
            if self.state == CircuitState.OPEN:
                if self._should_attempt_reset():
                    self.state = CircuitState.HALF_OPEN
                else:
                    raise CircuitBreakerOpenException()
            
            try:
                result = func(*args, **kwargs)
                self._record_request(success=True)
                
                if self.state == CircuitState.HALF_OPEN:
                    self.state = CircuitState.CLOSED
                
                return result
            except Exception as e:
                self._record_request(success=False)
                self._check_failure_threshold()
                raise e
    
    def _cleanup_old_requests(self):
        cutoff_time = time.time() - self.window_size
        self.requests = [
            req for req in self.requests 
            if req[0] > cutoff_time
        ]
    
    def _record_request(self, success):
        self.requests.append((time.time(), success))
    
    def _check_failure_threshold(self):
        if len(self.requests) < self.minimum_requests:
            return
        
        failures = sum(1 for _, success in self.requests if not success)
        failure_rate = failures / len(self.requests)
        
        if failure_rate >= self.failure_threshold:
            self.state = CircuitState.OPEN
```

---

## 🏛️ Bulkhead Pattern

### Definition
Isolates critical resources to prevent failures in one area from cascading to others.

### Implementation Strategies

#### 1. Thread Pool Isolation
```python
import concurrent.futures
import threading

class BulkheadExecutor:
    def __init__(self):
        self.executors = {
            'critical': concurrent.futures.ThreadPoolExecutor(max_workers=10),
            'normal': concurrent.futures.ThreadPoolExecutor(max_workers=5),
            'background': concurrent.futures.ThreadPoolExecutor(max_workers=2)
        }
    
    def submit_critical(self, func, *args, **kwargs):
        return self.executors['critical'].submit(func, *args, **kwargs)
    
    def submit_normal(self, func, *args, **kwargs):
        return self.executors['normal'].submit(func, *args, **kwargs)
    
    def submit_background(self, func, *args, **kwargs):
        return self.executors['background'].submit(func, *args, **kwargs)

# Usage
bulkhead = BulkheadExecutor()

# Critical operations get dedicated threads
future = bulkhead.submit_critical(process_payment, payment_data)

# Background operations don't interfere with critical ones
bulkhead.submit_background(send_analytics, event_data)
```

#### 2. Connection Pool Isolation
```python
class IsolatedConnectionPools:
    def __init__(self):
        self.pools = {
            'user_service': self._create_pool('user_db', max_connections=20),
            'order_service': self._create_pool('order_db', max_connections=15),
            'analytics': self._create_pool('analytics_db', max_connections=5)
        }
    
    def _create_pool(self, database, max_connections):
        return ConnectionPool(
            database=database,
            max_connections=max_connections,
            timeout=30
        )
    
    def get_connection(self, service):
        return self.pools[service].get_connection()

# Usage ensures analytics queries don't exhaust user service connections
with pools.get_connection('analytics') as conn:
    run_heavy_analytics_query(conn)
```

#### 3. Memory Isolation
```python
class MemoryBulkhead:
    def __init__(self):
        self.memory_limits = {
            'cache': 1024 * 1024 * 100,  # 100MB
            'user_sessions': 1024 * 1024 * 50,  # 50MB
            'temp_data': 1024 * 1024 * 20  # 20MB
        }
        self.current_usage = {key: 0 for key in self.memory_limits}
    
    def allocate(self, category, size):
        if self.current_usage[category] + size > self.memory_limits[category]:
            raise MemoryLimitExceeded(f"Category {category} limit exceeded")
        
        self.current_usage[category] += size
        return MemoryBlock(category, size, self)
    
    def deallocate(self, category, size):
        self.current_usage[category] -= size

class MemoryBlock:
    def __init__(self, category, size, bulkhead):
        self.category = category
        self.size = size
        self.bulkhead = bulkhead
    
    def __enter__(self):
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.bulkhead.deallocate(self.category, self.size)
```

---

## 🔄 Saga Pattern

### Definition
Manages distributed transactions across multiple services using a sequence of local transactions.

### Saga Types

#### 1. Choreography-Based Saga
```python
# Each service publishes events and listens to others
class OrderService:
    def create_order(self, order_data):
        # Create order in pending state
        order = Order(
            id=generate_id(),
            user_id=order_data['user_id'],
            items=order_data['items'],
            status='PENDING'
        )
        self.save_order(order)
        
        # Publish event
        self.publish_event('OrderCreated', {
            'order_id': order.id,
            'user_id': order.user_id,
            'items': order.items,
            'total': order.total
        })

class PaymentService:
    def handle_order_created(self, event):
        try:
            # Process payment
            payment = self.process_payment(
                event['user_id'],
                event['total']
            )
            
            # Publish success event
            self.publish_event('PaymentProcessed', {
                'order_id': event['order_id'],
                'payment_id': payment.id
            })
        except PaymentFailedException:
            # Publish failure event
            self.publish_event('PaymentFailed', {
                'order_id': event['order_id'],
                'reason': 'Insufficient funds'
            })

class InventoryService:
    def handle_payment_processed(self, event):
        try:
            # Reserve inventory
            self.reserve_items(event['order_id'])
            
            self.publish_event('InventoryReserved', {
                'order_id': event['order_id']
            })
        except InsufficientInventoryException:
            # Publish compensation event
            self.publish_event('InventoryReservationFailed', {
                'order_id': event['order_id']
            })
```

#### 2. Orchestration-Based Saga
```python
class OrderSagaOrchestrator:
    def __init__(self):
        self.steps = [
            ('validate_order', self.compensate_order_validation),
            ('process_payment', self.compensate_payment),
            ('reserve_inventory', self.compensate_inventory),
            ('confirm_order', None)  # No compensation needed
        ]
    
    def execute_saga(self, order_data):
        saga_state = SagaState(order_data)
        
        try:
            for step_name, compensation in self.steps:
                result = self.execute_step(step_name, saga_state)
                saga_state.add_completed_step(step_name, result, compensation)
            
            return saga_state.get_result()
        
        except Exception as e:
            # Execute compensations in reverse order
            self.compensate(saga_state)
            raise SagaFailedException(str(e))
    
    def execute_step(self, step_name, saga_state):
        if step_name == 'validate_order':
            return self.order_service.validate_order(saga_state.order_data)
        elif step_name == 'process_payment':
            return self.payment_service.process_payment(
                saga_state.order_data['user_id'],
                saga_state.order_data['total']
            )
        elif step_name == 'reserve_inventory':
            return self.inventory_service.reserve_items(
                saga_state.order_data['items']
            )
        elif step_name == 'confirm_order':
            return self.order_service.confirm_order(saga_state.order_id)
    
    def compensate(self, saga_state):
        for step_name, result, compensation in reversed(saga_state.completed_steps):
            if compensation:
                try:
                    compensation(result)
                except Exception as e:
                    # Log compensation failure
                    logger.error(f"Compensation failed for {step_name}: {e}")

class SagaState:
    def __init__(self, order_data):
        self.order_data = order_data
        self.order_id = None
        self.completed_steps = []
    
    def add_completed_step(self, step_name, result, compensation):
        self.completed_steps.append((step_name, result, compensation))
        
        if step_name == 'validate_order':
            self.order_id = result.order_id
```

---

## 🔍 Deep Dive Questions

### Microservices
1. **How do you handle distributed transactions in microservices?**
2. **What are the challenges of service discovery at scale?**
3. **How do you implement distributed tracing across services?**

### Event Sourcing
1. **How do you handle schema evolution in event sourcing?**
2. **What are the performance implications of rebuilding projections?**
3. **How do you implement snapshots effectively?**

### Circuit Breaker
1. **How do you determine appropriate failure thresholds?**
2. **What metrics should you monitor for circuit breakers?**
3. **How do you handle partial failures in circuit breakers?**

### Bulkhead
1. **How do you size resource pools for different workloads?**
2. **What are the trade-offs between isolation and resource utilization?**
3. **How do you monitor bulkhead effectiveness?**

### Saga Pattern
1. **When would you choose choreography over orchestration?**
2. **How do you handle long-running sagas?**
3. **What are the consistency guarantees of saga patterns?**

---

## 🎯 Pattern Selection Guide

### When to Use Microservices
- ✅ Large, complex applications
- ✅ Multiple development teams
- ✅ Different scaling requirements
- ✅ Technology diversity needs
- ❌ Small applications
- ❌ Single team
- ❌ Simple business logic

### When to Use Event Sourcing
- ✅ Audit trail requirements
- ✅ Complex business logic
- ✅ Time-based queries
- ✅ Debugging needs
- ❌ Simple CRUD operations
- ❌ Performance-critical reads
- ❌ Limited storage

### When to Use Circuit Breaker
- ✅ External service dependencies
- ✅ Network calls
- ✅ Cascade failure prevention
- ✅ Graceful degradation needs
- ❌ Internal method calls
- ❌ Database queries (use connection pooling)
- ❌ Synchronous processing only

### When to Use Bulkhead
- ✅ Mixed workload types
- ✅ Resource contention issues
- ✅ Critical vs non-critical operations
- ✅ Multi-tenant systems
- ❌ Homogeneous workloads
- ❌ Simple applications
- ❌ Unlimited resources

### When to Use Saga
- ✅ Distributed transactions
- ✅ Long-running processes
- ✅ Multiple service coordination
- ✅ Eventual consistency acceptable
- ❌ ACID requirements
- ❌ Simple transactions
- ❌ Single service operations

---

*Next: [Case Studies](../05-Case-Studies/) →*