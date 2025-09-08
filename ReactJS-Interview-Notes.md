# ReactJS Interview Notes

## Table of Contents
1. [Core Concepts](#core-concepts)
2. [Hooks](#hooks)
3. [State Management](#state-management)
4. [Performance Optimization](#performance-optimization)
5. [Folder Structure](#folder-structure)
6. [Interview Questions](#interview-questions)
7. [Best Practices](#best-practices)

## Core Concepts

### 1. Components

#### Functional Components
```jsx
const UserCard = ({ name, email }) => {
  return (
    <div className="user-card">
      <h3>{name}</h3>
      <p>{email}</p>
    </div>
  );
};
```

#### Class Components
```jsx
class UserCard extends React.Component {
  render() {
    const { name, email } = this.props;
    return (
      <div className="user-card">
        <h3>{name}</h3>
        <p>{email}</p>
      </div>
    );
  }
}
```

### 2. JSX
```jsx
const element = <h1>Hello, {name}!</h1>;

// JSX compiles to:
const element = React.createElement('h1', null, `Hello, ${name}!`);
```

### 3. Props vs State
```jsx
// Props (immutable)
const Child = ({ message }) => <p>{message}</p>;

// State (mutable)
const Parent = () => {
  const [count, setCount] = useState(0);
  return <Child message={`Count: ${count}`} />;
};
```

## Hooks

### 1. useState
```jsx
const Counter = () => {
  const [count, setCount] = useState(0);
  
  return (
    <div>
      <p>{count}</p>
      <button onClick={() => setCount(count + 1)}>+</button>
    </div>
  );
};
```

### 2. useEffect
```jsx
const UserProfile = ({ userId }) => {
  const [user, setUser] = useState(null);
  
  useEffect(() => {
    fetchUser(userId).then(setUser);
  }, [userId]); // Dependency array
  
  return user ? <div>{user.name}</div> : <div>Loading...</div>;
};
```

### 3. useContext
```jsx
const ThemeContext = createContext();

const App = () => (
  <ThemeContext.Provider value="dark">
    <Header />
  </ThemeContext.Provider>
);

const Header = () => {
  const theme = useContext(ThemeContext);
  return <header className={theme}>Header</header>;
};
```

### 4. useReducer
```jsx
const initialState = { count: 0 };

const reducer = (state, action) => {
  switch (action.type) {
    case 'increment': return { count: state.count + 1 };
    case 'decrement': return { count: state.count - 1 };
    default: return state;
  }
};

const Counter = () => {
  const [state, dispatch] = useReducer(reducer, initialState);
  
  return (
    <div>
      <p>{state.count}</p>
      <button onClick={() => dispatch({ type: 'increment' })}>+</button>
    </div>
  );
};
```

### 5. useMemo & useCallback
```jsx
const ExpensiveComponent = ({ items, filter }) => {
  // Memoize expensive calculation
  const filteredItems = useMemo(() => 
    items.filter(item => item.category === filter), 
    [items, filter]
  );
  
  // Memoize callback function
  const handleClick = useCallback((id) => {
    console.log('Clicked:', id);
  }, []);
  
  return (
    <div>
      {filteredItems.map(item => 
        <Item key={item.id} item={item} onClick={handleClick} />
      )}
    </div>
  );
};
```

### 6. Custom Hooks
```jsx
const useLocalStorage = (key, initialValue) => {
  const [value, setValue] = useState(() => {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : initialValue;
  });
  
  const setStoredValue = (newValue) => {
    setValue(newValue);
    localStorage.setItem(key, JSON.stringify(newValue));
  };
  
  return [value, setStoredValue];
};

// Usage
const Settings = () => {
  const [theme, setTheme] = useLocalStorage('theme', 'light');
  return <button onClick={() => setTheme('dark')}>Toggle Theme</button>;
};
```

## State Management

### 1. Context API
```jsx
// AuthContext.js
const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  
  const login = async (credentials) => {
    const user = await authService.login(credentials);
    setUser(user);
  };
  
  return (
    <AuthContext.Provider value={{ user, login }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
```

### 2. Redux Toolkit
```jsx
// store/userSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

export const fetchUser = createAsyncThunk('user/fetch', async (id) => {
  const response = await api.getUser(id);
  return response.data;
});

const userSlice = createSlice({
  name: 'user',
  initialState: { data: null, loading: false },
  reducers: {
    clearUser: (state) => { state.data = null; }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUser.pending, (state) => { state.loading = true; })
      .addCase(fetchUser.fulfilled, (state, action) => {
        state.loading = false;
        state.data = action.payload;
      });
  }
});

export const { clearUser } = userSlice.actions;
export default userSlice.reducer;
```

## Performance Optimization

### 1. React.memo
```jsx
const ExpensiveChild = React.memo(({ data }) => {
  return <div>{data.name}</div>;
});

// With custom comparison
const MemoizedComponent = React.memo(Component, (prevProps, nextProps) => {
  return prevProps.id === nextProps.id;
});
```

### 2. Code Splitting
```jsx
import { lazy, Suspense } from 'react';

const LazyComponent = lazy(() => import('./LazyComponent'));

const App = () => (
  <Suspense fallback={<div>Loading...</div>}>
    <LazyComponent />
  </Suspense>
);
```

### 3. Virtual Scrolling
```jsx
import { FixedSizeList as List } from 'react-window';

const VirtualList = ({ items }) => (
  <List
    height={600}
    itemCount={items.length}
    itemSize={50}
    itemData={items}
  >
    {({ index, style, data }) => (
      <div style={style}>
        {data[index].name}
      </div>
    )}
  </List>
);
```

## Folder Structure

### 1. Feature-Based Structure (Recommended)
```
src/
├── components/           # Shared components
│   ├── ui/              # Basic UI components
│   │   ├── Button/
│   │   │   ├── Button.jsx
│   │   │   ├── Button.test.js
│   │   │   └── index.js
│   │   └── Modal/
│   └── layout/          # Layout components
│       ├── Header/
│       └── Sidebar/
├── features/            # Feature modules
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.jsx
│   │   │   └── SignupForm.jsx
│   │   ├── hooks/
│   │   │   └── useAuth.js
│   │   ├── services/
│   │   │   └── authService.js
│   │   └── index.js
│   ├── dashboard/
│   └── profile/
├── hooks/               # Shared custom hooks
│   ├── useLocalStorage.js
│   └── useApi.js
├── services/            # API services
│   ├── api.js
│   └── httpClient.js
├── store/               # State management
│   ├── index.js
│   └── slices/
├── utils/               # Utility functions
│   ├── helpers.js
│   └── constants.js
├── styles/              # Global styles
│   ├── globals.css
│   └── variables.css
└── App.jsx
```

### 2. Atomic Design Structure
```
src/
├── components/
│   ├── atoms/           # Basic building blocks
│   │   ├── Button/
│   │   ├── Input/
│   │   └── Text/
│   ├── molecules/       # Simple combinations
│   │   ├── SearchBox/
│   │   └── FormField/
│   ├── organisms/       # Complex components
│   │   ├── Header/
│   │   └── ProductList/
│   ├── templates/       # Page layouts
│   │   └── PageLayout/
│   └── pages/           # Complete pages
│       ├── HomePage/
│       └── ProductPage/
```

### 3. Domain-Driven Structure
```
src/
├── domains/
│   ├── user/
│   │   ├── components/
│   │   ├── services/
│   │   ├── types/
│   │   └── utils/
│   ├── product/
│   └── order/
├── shared/
│   ├── components/
│   ├── hooks/
│   ├── services/
│   └── utils/
```

## Interview Questions

### 1. What is Virtual DOM?
Virtual DOM is a JavaScript representation of the real DOM. React uses it to:
- Minimize expensive DOM operations
- Batch updates for better performance
- Enable predictable rendering

### 2. Explain React Lifecycle Methods
```jsx
class Component extends React.Component {
  componentDidMount() {
    // After component mounts
  }
  
  componentDidUpdate(prevProps, prevState) {
    // After component updates
  }
  
  componentWillUnmount() {
    // Before component unmounts
  }
}

// Hooks equivalent
useEffect(() => {
  // componentDidMount + componentDidUpdate
  return () => {
    // componentWillUnmount
  };
}, [dependencies]);
```

### 3. What are Keys in React?
Keys help React identify which items have changed, added, or removed.

```jsx
// Bad - using index
{items.map((item, index) => 
  <Item key={index} data={item} />
)}

// Good - using unique identifier
{items.map(item => 
  <Item key={item.id} data={item} />
)}
```

### 4. Controlled vs Uncontrolled Components
```jsx
// Controlled
const ControlledInput = () => {
  const [value, setValue] = useState('');
  return (
    <input 
      value={value} 
      onChange={(e) => setValue(e.target.value)} 
    />
  );
};

// Uncontrolled
const UncontrolledInput = () => {
  const inputRef = useRef();
  const handleSubmit = () => {
    console.log(inputRef.current.value);
  };
  return <input ref={inputRef} />;
};
```

### 5. Higher-Order Components (HOC)
```jsx
const withAuth = (WrappedComponent) => {
  return (props) => {
    const { user } = useAuth();
    
    if (!user) {
      return <LoginPage />;
    }
    
    return <WrappedComponent {...props} />;
  };
};

const ProtectedPage = withAuth(Dashboard);
```

### 6. Render Props Pattern
```jsx
const DataFetcher = ({ render, url }) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    fetch(url)
      .then(res => res.json())
      .then(data => {
        setData(data);
        setLoading(false);
      });
  }, [url]);
  
  return render({ data, loading });
};

// Usage
<DataFetcher 
  url="/api/users" 
  render={({ data, loading }) => 
    loading ? <Spinner /> : <UserList users={data} />
  } 
/>
```

## Best Practices

### 1. Component Design
- Keep components small and focused
- Use composition over inheritance
- Prefer functional components with hooks
- Extract custom hooks for reusable logic

### 2. State Management
- Use local state for component-specific data
- Use Context for app-wide state
- Consider Redux for complex state logic
- Normalize state structure

### 3. Performance
- Use React.memo for expensive components
- Implement proper key props
- Avoid inline functions in render
- Use useCallback and useMemo appropriately

### 4. Code Organization
- Group related files together
- Use absolute imports with path mapping
- Implement consistent naming conventions
- Separate concerns (UI, logic, data)

### 5. Testing
```jsx
import { render, screen, fireEvent } from '@testing-library/react';

test('counter increments when button clicked', () => {
  render(<Counter />);
  const button = screen.getByText('+');
  fireEvent.click(button);
  expect(screen.getByText('1')).toBeInTheDocument();
});
```

### 6. Error Boundaries
```jsx
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }
  
  static getDerivedStateFromError(error) {
    return { hasError: true };
  }
  
  componentDidCatch(error, errorInfo) {
    console.error('Error caught:', error, errorInfo);
  }
  
  render() {
    if (this.state.hasError) {
      return <h1>Something went wrong.</h1>;
    }
    return this.props.children;
  }
}
```

## Common Patterns

### 1. Compound Components
```jsx
const Tabs = ({ children, defaultTab }) => {
  const [activeTab, setActiveTab] = useState(defaultTab);
  
  return (
    <div className="tabs">
      {React.Children.map(children, child =>
        React.cloneElement(child, { activeTab, setActiveTab })
      )}
    </div>
  );
};

Tabs.TabList = ({ children, activeTab, setActiveTab }) => (
  <div className="tab-list">
    {React.Children.map(children, (child, index) =>
      React.cloneElement(child, { 
        isActive: activeTab === index,
        onClick: () => setActiveTab(index)
      })
    )}
  </div>
);

Tabs.Tab = ({ children, isActive, onClick }) => (
  <button className={`tab ${isActive ? 'active' : ''}`} onClick={onClick}>
    {children}
  </button>
);

// Usage
<Tabs defaultTab={0}>
  <Tabs.TabList>
    <Tabs.Tab>Tab 1</Tabs.Tab>
    <Tabs.Tab>Tab 2</Tabs.Tab>
  </Tabs.TabList>
</Tabs>
```

### 2. Provider Pattern
```jsx
const ApiProvider = ({ children }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const request = async (url, options) => {
    setLoading(true);
    try {
      const response = await fetch(url, options);
      const data = await response.json();
      setLoading(false);
      return data;
    } catch (err) {
      setError(err);
      setLoading(false);
      throw err;
    }
  };
  
  return (
    <ApiContext.Provider value={{ request, loading, error }}>
      {children}
    </ApiContext.Provider>
  );
};
```

## Quick Reference

### React 18 Features
- **Concurrent Features**: Automatic batching, transitions
- **Suspense**: Better loading states
- **useId**: Unique ID generation
- **useDeferredValue**: Defer non-urgent updates

### Performance Checklist
- [ ] Use React.memo for pure components
- [ ] Implement proper key props
- [ ] Avoid creating objects/functions in render
- [ ] Use useCallback/useMemo appropriately
- [ ] Implement code splitting
- [ ] Optimize bundle size
- [ ] Use React DevTools Profiler

### Security Best Practices
- Sanitize user input
- Use HTTPS for API calls
- Implement proper authentication
- Avoid dangerouslySetInnerHTML
- Validate props with PropTypes/TypeScript