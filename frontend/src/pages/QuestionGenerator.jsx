import React, { useState } from 'react'
import { api } from '../services/api'
import { Sparkles, Trophy, Code, Database, Settings } from 'lucide-react'

/**
 * QuestionGenerator Component - Lets users select a technical category and start a mock session.
 */
function QuestionGenerator({ onStartMock }) {
  const [category, setCategory] = useState('Java');
  const [difficulty, setDifficulty] = useState('INTERMEDIATE');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const categories = [
    { id: 'Java', name: 'Java Core', icon: Code, desc: 'Object-Oriented Concepts, Multithreading, JVM architectures' },
    { id: 'Spring Boot', name: 'Spring Boot', icon: Settings, desc: 'Dependency Injection, Beans, REST APIs, Security architectures' },
    { id: 'SQL', name: 'SQL Databases', icon: Database, desc: 'Joins, Constraints, Subqueries, Indexing performance' },
    { id: 'React', name: 'React.js', icon: Code, desc: 'Hooks (useState, useEffect), Virtual DOM, Props/States' },
    { id: 'DSA', name: 'DSA Algorithm', icon: Sparkles, desc: 'Arrays, Strings, Two-Pointers, sorting arrays' },
    { id: 'HR', name: 'HR Placement', icon: Trophy, desc: 'Behavioral answers, team cooperation, career goals' }
  ];

  const getLocalMockQuestions = (cat) => {
    const javaMock = [
      { id: 1, questionText: "What is the difference between an Abstract Class and an Interface in Java? When should you use which?", difficulty: "INTERMEDIATE", sampleAnswer: "An abstract class can have instance fields and concrete methods, supporting single inheritance. An interface can only have static final constants and abstract methods (or default methods), supporting multiple inheritance. Use an abstract class for sharing code among closely related classes; use an interface to define a contract for unrelated classes." },
      { id: 2, questionText: "Explain HashMap collision handling in Java 8.", difficulty: "INTERMEDIATE", sampleAnswer: "Java 8 uses chaining with linked lists. If a single bucket exceeds 8 nodes and total capacity is at least 64, the linked list is converted into a self-balancing red-black tree, reducing lookup time from O(N) to O(log N)." },
      { id: 3, questionText: "What is the difference between ConcurrentHashMap and SynchronizedMap?", difficulty: "ADVANCED", sampleAnswer: "ConcurrentHashMap achieves high concurrency by segmenting or locking buckets (using CAS and synchronized at node level) rather than locking the entire map. SynchronizedMap locks the entire map on every operation." }
    ];

    const springMock = [
      { id: 1, questionText: "Explain the Bean Lifecycle in Spring Boot.", difficulty: "INTERMEDIATE", sampleAnswer: "Instantiated -> Populate Properties -> BeanNameAware/BeanFactoryAware -> Pre-initialization BeanPostProcessors -> InitializingBean custom init() -> Post-initialization BeanPostProcessors -> Ready for use -> DisposableBean custom destroy()." },
      { id: 2, questionText: "Explain the difference between Constructor Injection and Setter Injection. Why is Constructor Injection preferred?", difficulty: "INTERMEDIATE", sampleAnswer: "Constructor Injection enforces required dependencies at object creation, ensures immutability of fields, and simplifies testing. Setter Injection allows optional dependencies that can be changed after instantiation." },
      { id: 3, questionText: "How does @Transactional annotation work in Spring Boot?", difficulty: "ADVANCED", sampleAnswer: "Spring AOP creates a dynamic transaction proxy wrapper around the target bean. It opens a database connection transaction before the method runs and commits/rolls back depending on if a RuntimeException was thrown." }
    ];

    const sqlMock = [
      { id: 1, questionText: "What is the difference between INNER JOIN, LEFT JOIN, and RIGHT JOIN in SQL?", difficulty: "INTERMEDIATE", sampleAnswer: "INNER JOIN returns rows only when there is a match in both tables. LEFT JOIN returns all rows from the left table and matched rows from the right table. RIGHT JOIN does the reverse, returning all right rows." },
      { id: 2, questionText: "What is a Database Index? How does it improve retrieval speed and what are the trade-offs?", difficulty: "INTERMEDIATE", sampleAnswer: "An index is a pointer structure (B-Trees) that speeds up SELECT search queries. Trade-offs include slower INSERT/UPDATE operations and additional disk storage consumption." },
      { id: 3, questionText: "What are SQL subqueries? What is the difference between a correlated and non-correlated subquery?", difficulty: "ADVANCED", sampleAnswer: "A subquery is a query nested inside another query. A non-correlated subquery executes independently once. A correlated subquery references the outer query and executes repeatedly for each row." }
    ];

    const reactMock = [
      { id: 1, questionText: "What are React Hooks? Explain the purpose of useState and useEffect hooks.", difficulty: "INTERMEDIATE", sampleAnswer: "Hooks are functions that let functional components manage state and lifecycle. useState adds reactive state trackers. useEffect manages side-effects like API data fetching after mounting." },
      { id: 2, questionText: "What is the Virtual DOM? How does React's reconciliation process optimize DOM updates?", difficulty: "INTERMEDIATE", sampleAnswer: "The Virtual DOM is a lightweight memory copy of the real DOM. React updates the Virtual DOM first, compares it with a snapshot using a diffing algorithm, and updates only the changed parts of the real DOM." },
      { id: 3, questionText: "Explain React's hook dependencies array in useEffect. What happens if you leave it empty or omit it?", difficulty: "ADVANCED", sampleAnswer: "The dependencies array determines when useEffect triggers. Leaving it empty [] makes it run only once on mount. Omitting it completely makes it run after every render, which can cause infinite loops." }
    ];

    const dsaMock = [
      { id: 1, questionText: "What is Binary Search? What is its time complexity and what condition must the array satisfy?", difficulty: "INTERMEDIATE", sampleAnswer: "Binary Search repeatedly divides the search interval in half. Its time complexity is O(log N). The input array must be sorted in ascending order." },
      { id: 2, questionText: "How do you detect a cycle in a Linked List? Explain Floyd's Cycle-Finding Algorithm (Tortoise and Hare).", difficulty: "INTERMEDIATE", sampleAnswer: "Floyd's algorithm uses two pointers moving at different speeds (slow moves 1 step, fast moves 2 steps). If there is a cycle, the pointers will meet at some node; otherwise, the fast pointer reaches null." },
      { id: 3, questionText: "Explain the difference between DFS and BFS traversal in a Graph. When is which preferred?", difficulty: "ADVANCED", sampleAnswer: "DFS (Depth First Search) uses a stack/recursion to go deep down a branch. BFS (Breadth First Search) uses a queue to traverse layer-by-layer. BFS is preferred for finding the shortest path; DFS is preferred for topological sorting." }
    ];

    const hrMock = [
      { id: 1, questionText: "Tell me about yourself, your career goals, and what motivated you to build an AI Interview Coach.", difficulty: "INTERMEDIATE", sampleAnswer: "I am an enthusiastic developer who built the AI Coach to solve real placement preparation gaps, combining standard Spring Boot enterprise backends, stateless JWT security, and modern React glassmorphic dashboards." },
      { id: 2, questionText: "Describe a difficult technical bug you faced in your project and how you went about resolving it.", difficulty: "INTERMEDIATE", sampleAnswer: "I faced Eclipse compilation errors due to Lombok eclipse agents not being installed. I solved this permanently by refactoring all builders and annotations into pure vanilla getters, setters, and constructors." }
    ];

    switch(cat) {
      case 'Spring Boot': return springMock;
      case 'SQL': return sqlMock;
      case 'React': return reactMock;
      case 'DSA': return dsaMock;
      case 'HR': return hrMock;
      default: return javaMock;
    }
  };

  const handleStart = async () => {
    setError('');
    setLoading(true);
    try {
      const data = await api.generateQuestions(category, difficulty);
      if (Array.isArray(data) && data.length > 0) {
        onStartMock(category, data);
      } else {
        console.warn('Backend returned empty questions. Triggering local mock fallback...');
        const localMock = getLocalMockQuestions(category);
        onStartMock(category, localMock);
      }
    } catch (err) {
      console.warn('Failed to connect to backend. Triggering local mock fallback...', err);
      const localMock = getLocalMockQuestions(category);
      onStartMock(category, localMock);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-content">
      <header className="page-header">
        <div>
          <h1>AI Mock Placement Arena</h1>
          <p>Select your target stack, set difficulty, and let Gemini conduct a simulated placement interview.</p>
        </div>
      </header>

      <div className="mock-setup-container card">
        <h3>Interview Settings</h3>
        
        {error && <div className="error-alert">{error}</div>}

        {/* Categories Selection */}
        <div className="setup-block">
          <label className="section-label">Select Technical/Placement Category</label>
          <div className="categories-grid">
            {categories.map((cat) => {
              const Icon = cat.icon;
              return (
                <button
                  key={cat.id}
                  className={`category-select-card ${category === cat.id ? 'active' : ''}`}
                  onClick={() => setCategory(cat.id)}
                >
                  <div className="cat-icon-container">
                    <Icon size={24} />
                  </div>
                  <h4>{cat.name}</h4>
                  <p>{cat.desc}</p>
                </button>
              )
            })}
          </div>
        </div>

        <hr className="divider" />

        {/* Difficulty Selection */}
        <div className="setup-block">
          <label className="section-label">Select Difficulty Level</label>
          <div className="difficulty-row">
            {['BEGINNER', 'INTERMEDIATE', 'ADVANCED'].map((level) => (
              <button
                key={level}
                className={`btn-diff-select ${difficulty === level ? 'active' : ''}`}
                onClick={() => setDifficulty(level)}
              >
                {level}
              </button>
            ))}
          </div>
        </div>

        {/* Start Button */}
        <div className="setup-footer">
          <button className="btn-primary btn-large mt-6" onClick={handleStart} disabled={loading}>
            <Sparkles size={18} />
            <span>{loading ? 'Assembling AI Interview Board...' : 'Begin Mock Interview'}</span>
          </button>
        </div>
      </div>
    </div>
  )
}

export default QuestionGenerator
