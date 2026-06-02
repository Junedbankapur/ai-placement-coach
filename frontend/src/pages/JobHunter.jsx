import React, { useState, useEffect } from 'react'
import { api } from '../services/api'
import { 
  Briefcase, AlertCircle, FileText, Sparkles, 
  ChevronDown, ChevronUp, Copy, Check, Search, Award, CheckCircle, Clock, Send, X 
} from 'lucide-react'

/**
 * PlacementBoard Component - A premium Glassmorphic Placement Office
 * where students scan available companies, check ATS matching, review cover letters,
 * apply directly, and track live placement application timelines!
 */
function JobHunter({ onPracticeRole }) {
  const [loading, setLoading] = useState(false)
  const [resumeData, setResumeData] = useState(null)
  const [jobsData, setJobsData] = useState(null)
  const [error, setError] = useState('')
  const [isScanning, setIsScanning] = useState(false)
  const [activeTab, setActiveTab] = useState('placements') // 'placements' or 'applications'
  
  // Application Management State
  const [appliedCompanies, setAppliedCompanies] = useState([]) // Array of company names applied to
  const [selectedJobForModal, setSelectedJobForModal] = useState(null) // Holds JD data for apply modal
  const [submittingApp, setSubmittingApp] = useState(false)
  const [appSuccess, setAppSuccess] = useState(false)

  // Accordion drawer for cover letter preview
  const [expandedLetter, setExpandedLetter] = useState(null)
  const [copiedIndex, setCopiedIndex] = useState(null)
  const [practicingIndex, setPracticingIndex] = useState(null)

  const username = localStorage.getItem('username') || 'Juned Bankapur'

  // Fetch resume and load application history on mount
  useEffect(() => {
    fetchResumeStatus()
    loadApplicationHistory()
  }, [])

  const fetchResumeStatus = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await api.getLatestResume()
      if (data && typeof data === 'object' && data.id) {
        setResumeData(data)
        // Automatically fetch matched jobs right away so the user sees companies immediately!
        try {
          const jobs = await api.getJobRecommendations()
          if (jobs && jobs.recommendations) {
            setJobsData(jobs)
          }
        } catch (jobsErr) {
          console.warn('Failed to automatically fetch matched jobs on mount:', jobsErr)
        }
      } else {
        setResumeData(null)
      }
    } catch (err) {
      console.error('Error loading resume:', err)
      setError('Failed to fetch CV verification status.')
    } finally {
      setLoading(false)
    }
  }

  // Load applied history from localStorage to keep state persistent
  const loadApplicationHistory = () => {
    try {
      const stored = localStorage.getItem('interviewcoach_applications')
      if (stored) {
        setAppliedCompanies(JSON.parse(stored))
      }
    } catch (e) {
      console.error('Failed to load application history:', e)
    }
  }

  // Trigger the AI recruitment database scanner
  const handleScanJobs = async () => {
    setIsScanning(true)
    setError('')
    setJobsData(null)
    
    // Simulate deep AI market scanning
    setTimeout(async () => {
      try {
        const data = await api.getJobRecommendations()
        if (data && data.recommendations) {
          setJobsData(data)
        } else {
          setError('No job matching recommendations could be retrieved.')
        }
      } catch (err) {
        setError(err.message || 'Error occurred while scanning active opportunities.')
      } finally {
        setIsScanning(false)
      }
    }, 1500)
  }

  // Copy cover letter text to clipboard
  const copyToClipboard = (text, index) => {
    navigator.clipboard.writeText(text)
    setCopiedIndex(index)
    setTimeout(() => setCopiedIndex(null), 2000)
  }

  // Launch modal for submitting application
  const openApplyModal = (job) => {
    setSelectedJobForModal(job)
    setAppSuccess(false)
    setSubmittingApp(false)
  }

  // Submit placement application
  const handleConfirmSubmit = () => {
    setSubmittingApp(true)
    
    // Simulate application processing
    setTimeout(() => {
      setSubmittingApp(false)
      setAppSuccess(true)
      
      // Update history in state and persist to local storage
      const updated = [...appliedCompanies, {
        company: selectedJobForModal.company,
        jobTitle: selectedJobForModal.jobTitle,
        location: selectedJobForModal.location,
        matchScore: selectedJobForModal.matchScore,
        prepTopics: selectedJobForModal.prepTopics,
        appliedDate: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
        status: 'Interview Scheduled' // Move them to Mock Interview shortlisted state instantly!
      }]
      
      setAppliedCompanies(updated)
      localStorage.setItem('interviewcoach_applications', JSON.stringify(updated))
      
      // Close modal automatically after 1.5 seconds success view
      setTimeout(() => {
        setSelectedJobForModal(null)
      }, 1500)
    }, 1500)
  }

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

  // Pre-load topics and direct redirect to placement simulation arena
  const handlePracticeStart = async (topics, companyName, index) => {
    setPracticingIndex(index)
    setError('')

    let targetCategory = 'Java'
    if (topics && topics.length > 0) {
      const primaryTopic = topics[0].toLowerCase()
      if (primaryTopic.includes('react') || primaryTopic.includes('hooks') || primaryTopic.includes('frontend')) {
        targetCategory = 'React'
      } else if (primaryTopic.includes('boot') || primaryTopic.includes('spring') || primaryTopic.includes('microservices') || primaryTopic.includes('rest')) {
        targetCategory = 'Spring Boot'
      } else if (primaryTopic.includes('sql') || primaryTopic.includes('database') || primaryTopic.includes('indexing') || primaryTopic.includes('queries')) {
        targetCategory = 'SQL'
      } else if (primaryTopic.includes('dsa') || primaryTopic.includes('algorithm') || primaryTopic.includes('tree') || primaryTopic.includes('graph') || primaryTopic.includes('array')) {
        targetCategory = 'DSA'
      } else if (primaryTopic.includes('behavioral') || primaryTopic.includes('hr') || primaryTopic.includes('placement')) {
        targetCategory = 'HR'
      }
    }

    try {
      const questions = await api.generateQuestions(targetCategory, 'INTERMEDIATE')
      if (Array.isArray(questions) && questions.length > 0) {
        onPracticeRole(targetCategory, questions)
      } else {
        console.warn('Backend questions list empty. Triggering local mock fallback...');
        const localMock = getLocalMockQuestions(targetCategory)
        onPracticeRole(targetCategory, localMock)
      }
    } catch (err) {
      console.warn('Failed to load questions from backend. Triggering local mock fallback...', err)
      const localMock = getLocalMockQuestions(targetCategory)
      onPracticeRole(targetCategory, localMock)
    } finally {
      setPracticingIndex(null)
    }
  }

  // Helper check if applied to specific company
  const isApplied = (companyName) => {
    return appliedCompanies.some(app => app.company.toLowerCase() === companyName.toLowerCase())
  }

  // SVG Radial Match Score Gauge
  const renderRadialGauge = (score) => {
    const radius = 36
    const circumference = 2 * Math.PI * radius
    const strokeDashoffset = circumference - (score / 100) * circumference
    const strokeColor = score >= 85 ? 'var(--emerald)' : score >= 75 ? 'var(--amber)' : 'var(--danger)'

    return (
      <div className="radial-score-container shrink-0">
        <svg className="radial-svg" width="90" height="90" viewBox="0 0 90 90">
          <circle className="radial-bg" cx="45" cy="45" r={radius} stroke="rgba(255,255,255,0.03)" strokeWidth="6" fill="transparent" />
          <circle 
            className="radial-progress" 
            cx="45" 
            cy="45" 
            r={radius} 
            stroke={strokeColor} 
            strokeWidth="6" 
            fill="transparent" 
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
          />
        </svg>
        <div className="radial-score-text">
          <span className="score-number" style={{ color: strokeColor }}>{score}%</span>
          <span className="score-label">MATCH</span>
        </div>
      </div>
    )
  }

  // 1. Loading active CV status
  if (loading) {
    return (
      <div className="page-content flex items-center justify-center" style={{ minHeight: '60vh' }}>
        <div className="text-center">
          <div className="loading-spinner mb-4"></div>
          <p>Verifying active resume audits...</p>
        </div>
      </div>
    )
  }

  // 2. Empty state: No audited resume found in system
  if (!resumeData) {
    return (
      <div className="page-content">
        <header className="page-header">
          <div>
            <h1>Placement Board & Openings</h1>
            <p>Track active corporate job openings, check ATS compatibility, and submit applications directly.</p>
          </div>
        </header>

        <div className="card text-center py-12 px-6 glass-glow-card max-width-600 margin-auto mt-12 animate-fade-in">
          <AlertCircle size={48} className="text-indigo margin-auto mb-4" />
          <h2 className="mb-2">Audited Resume Required</h2>
          <p className="text-secondary mb-6" style={{ maxWidth: '460px', margin: '0 auto 24px' }}>
            To view matched corporate placements, calculate ATS scores, and auto-draft cover letters, you must audit your resume first.
          </p>
          <div className="flex gap-4 justify-center">
            <button className="btn-primary" onClick={() => window.location.reload()}>
              Refresh Status
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="page-content">
      <header className="page-header">
        <div>
          <h1>Placement Board & Openings</h1>
          <p>Track active corporate job openings, check ATS compatibility, and submit applications directly.</p>
        </div>
      </header>

      {error && <div className="error-alert mb-6">{error}</div>}

      {/* Tabs Menu Navigation Bar */}
      <div className="tabs-navigation mb-6 flex gap-4 border-b-glass pb-2">
        <button 
          className={`tab-btn font-heading ${activeTab === 'placements' ? 'active text-indigo font-bold border-b-indigo' : 'text-secondary'}`}
          onClick={() => setActiveTab('placements')}
          style={{ background: 'none', border: 'none', padding: '10px 16px', cursor: 'pointer', fontSize: '1rem' }}
        >
          <div className="flex items-center gap-2">
            <Briefcase size={18} />
            <span>Available Placements</span>
          </div>
        </button>
        <button 
          className={`tab-btn font-heading ${activeTab === 'applications' ? 'active text-indigo font-bold border-b-indigo' : 'text-secondary'}`}
          onClick={() => setActiveTab('applications')}
          style={{ background: 'none', border: 'none', padding: '10px 16px', cursor: 'pointer', fontSize: '1rem' }}
        >
          <div className="flex items-center gap-2">
            <CheckCircle size={18} />
            <span>My Applications ({appliedCompanies.length})</span>
          </div>
        </button>
      </div>

      {/* Tab Content 1: Placements Directory */}
      {activeTab === 'placements' && (
        <div className="animate-fade-in">
          {/* Overview Dashboard row */}
          <div className="grid-2 gap-6 mb-8">
            <div className="card">
              <div className="flex items-center gap-3 mb-4">
                <FileText size={20} className="text-indigo" />
                <h3 className="font-heading">Active Candidate Profile</h3>
              </div>
              <p className="text-secondary text-sm mb-4">
                Audited source file: <strong className="text-white">{resumeData.fileName}</strong>
              </p>
              <label className="section-label">Verified Technical Skills List</label>
              <div className="flex flex-wrap gap-2 mt-2">
                {resumeData.skills && resumeData.skills.map((skill, idx) => (
                  <span key={idx} className="skill-badge-view">{skill}</span>
                ))}
              </div>
            </div>

            <div className="card flex flex-col justify-between glass-glow-card">
              <div>
                <div className="flex items-center gap-3 mb-3">
                  <Search size={20} className="text-indigo animate-pulse" />
                  <h3 className="font-heading">Hiring Market Database</h3>
                </div>
                <p className="text-secondary text-sm">
                  Click below to trigger our recruitment agent. It compiles your skills/projects against available jobs at Oracle, Capgemini, and Accenture, isolating keyword deficiencies and auto-drafting custom cover letters.
                </p>
              </div>
              <div className="mt-6">
                <button className="btn-primary w-full py-3 flex items-center justify-center gap-2" onClick={handleScanJobs} disabled={isScanning}>
                  <Sparkles size={18} />
                  <span>{isScanning ? 'Querying recruitment boards...' : 'Scan Available Placements'}</span>
                </button>
              </div>
            </div>
          </div>

          {/* Radar Scanning animation */}
          {isScanning && (
            <div className="card text-center py-12 glass-glow-card mt-6">
              <div className="agent-radar mb-4">
                <div className="radar-circle"></div>
                <Search className="radar-search" size={24} />
              </div>
              <h3 className="text-indigo font-heading animate-pulse">Running Candidate Matching System...</h3>
              <p className="text-secondary text-sm mt-2">Correlating skills & projects against available corporate openings</p>
            </div>
          )}

          {/* Jobs Listing grid */}
          {jobsData && !isScanning && (
            <div className="mt-6 animate-fade-in">
              <h2 className="font-heading mb-6 flex items-center gap-2">
                <Award size={22} className="text-indigo" />
                <span>Tailored Career Opportunities</span>
              </h2>

              <div className="flex flex-col gap-6">
                {jobsData.recommendations && jobsData.recommendations.map((rec, index) => {
                  const applied = isApplied(rec.company)
                  return (
                    <div key={index} className="card job-match-card glass-glow-card">
                      
                      {/* Company & Match row */}
                      <div className="flex flex-col md:flex-row justify-between gap-6 pb-6 border-b-glass">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-2 flex-wrap">
                            <span className="company-tag">{rec.company}</span>
                            <span className="location-tag">{rec.location}</span>
                            {applied && (
                              <span className="text-emerald text-xs font-semibold flex items-center gap-1 bg-emerald-glow px-2.5 py-1 rounded-md">
                                <CheckCircle size={12} /> Applied
                              </span>
                            )}
                          </div>
                          <h3 className="job-title-highlight font-heading">{rec.jobTitle}</h3>
                          
                          {/* Recommended study topics */}
                          <div className="mt-4">
                            <strong className="text-xs uppercase tracking-wider text-secondary block mb-2">Recommended Prep</strong>
                            <div className="flex flex-wrap gap-2">
                              {rec.prepTopics && rec.prepTopics.map((topic, i) => (
                                <span key={i} className="prep-tag">✓ {topic}</span>
                              ))}
                            </div>
                          </div>
                        </div>

                        {renderRadialGauge(rec.matchScore)}
                      </div>

                      {/* ATS Deficiencies row */}
                      <div className="py-5 border-b-glass">
                        <div className="flex items-center gap-2 mb-3">
                          <AlertCircle size={15} className="text-red shrink-0" />
                          <span className="text-xs uppercase tracking-wider font-semibold text-red">ATS Keyword Deficiencies (Recommended additions to CV)</span>
                        </div>
                        <div className="flex flex-wrap gap-2">
                          {rec.missingKeywords && rec.missingKeywords.length > 0 ? (
                            rec.missingKeywords.map((kw, i) => (
                              <span key={i} className="keyword-gap-badge">{kw}</span>
                            ))
                          ) : (
                            <span className="text-emerald text-sm">Resume is perfectly optimized!</span>
                          )}
                        </div>
                      </div>

                      {/* Accordion Cover Letter */}
                      <div className="pt-4 flex flex-col gap-4">
                        <div>
                          <button 
                            className="accordion-header flex items-center justify-between w-full text-left py-2 font-medium"
                            onClick={() => setExpandedLetter(expandedLetter === index ? null : index)}
                          >
                            <span className="flex items-center gap-2 text-sm text-indigo">
                              <FileText size={16} />
                              <span>Review Tailored Cover Letter Draft</span>
                            </span>
                            {expandedLetter === index ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                          </button>

                          {expandedLetter === index && (
                            <div className="accordion-body mt-3 p-4 bg-black-20 rounded-lg border-glass animate-slide-down">
                              <div className="flex justify-between items-start gap-4 mb-3">
                                <span className="text-xs text-secondary italic">AI cover letter drafted specifically for this role</span>
                                <button 
                                  className="btn-secondary py-1 px-3 text-xs flex items-center gap-1.5"
                                  onClick={() => copyToClipboard(rec.tailoredCoverLetter, index)}
                                >
                                  {copiedIndex === index ? <Check size={12} className="text-emerald" /> : <Copy size={12} />}
                                  <span>{copiedIndex === index ? 'Copied!' : 'Copy'}</span>
                                </button>
                              </div>
                              <p className="cover-letter-text text-sm leading-relaxed text-secondary" style={{ whiteSpace: 'pre-line' }}>
                                {rec.tailoredCoverLetter}
                              </p>
                            </div>
                          )}
                        </div>

                        {/* Submit Action Buttons */}
                        <div className="flex justify-end pt-2">
                          {applied ? (
                            <button 
                              className="btn-secondary flex items-center gap-2 text-sm py-2 px-6"
                              onClick={() => handlePracticeStart(rec.prepTopics, rec.company, index)}
                              disabled={practicingIndex === index}
                            >
                              <Sparkles size={16} />
                              <span>{practicingIndex === index ? 'Pre-loading mock questions...' : 'Practice Interview for this Role'}</span>
                            </button>
                          ) : (
                            <button 
                              className="btn-primary flex items-center gap-2 text-sm py-2 px-6"
                              onClick={() => openApplyModal(rec)}
                            >
                              <Send size={16} />
                              <span>Apply for this Company</span>
                            </button>
                          )}
                        </div>
                      </div>

                    </div>
                  )
                })}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Tab Content 2: My Applications History & Status Timelines */}
      {activeTab === 'applications' && (
        <div className="animate-fade-in">
          {appliedCompanies.length === 0 ? (
            <div className="card text-center py-12 glass-glow-card max-width-600 margin-auto mt-6">
              <Clock size={40} className="text-secondary margin-auto mb-3" />
              <h3>No Applications Submitted</h3>
              <p className="text-secondary text-sm mt-2" style={{ maxWidth: '400px', margin: '0 auto' }}>
                You haven't submitted any corporate placement applications yet. Scan available jobs and apply to start tracking live interview reviews.
              </p>
            </div>
          ) : (
            <div className="flex flex-col gap-6">
              {appliedCompanies.map((app, idx) => (
                <div key={idx} className="card job-match-card glass-glow-card">
                  {/* Details Header */}
                  <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 pb-4 border-b-glass">
                    <div>
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="company-tag">{app.company}</span>
                        <span className="location-tag">{app.location}</span>
                        <span className="text-secondary text-xs">{app.appliedDate}</span>
                      </div>
                      <h3 className="job-title-highlight font-heading" style={{ fontSize: '1.25rem', marginTop: '6px' }}>{app.jobTitle}</h3>
                    </div>

                    <div className="flex items-center gap-3">
                      <span className="text-indigo text-sm font-semibold bg-primary-glow px-3 py-1.5 rounded-lg border-glass">
                        Status: Shortlisted
                      </span>
                      <button 
                        className="btn-primary py-2 px-4 text-sm flex items-center gap-1.5"
                        onClick={() => handlePracticeStart(app.prepTopics, app.company, idx)}
                        disabled={practicingIndex === idx}
                      >
                        <Sparkles size={14} />
                        <span>{practicingIndex === idx ? 'Pre-loading...' : 'Mock Practice'}</span>
                      </button>
                    </div>
                  </div>

                  {/* Application Progress Status Tracker */}
                  <div className="pt-6">
                    <strong className="text-xs uppercase tracking-wider text-secondary block mb-4">Application Progress Tracking</strong>
                    
                    <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 w-full relative">
                      
                      {/* Step 1: Applied */}
                      <div className="flex items-center gap-3 flex-1">
                        <div className="w-8 h-8 rounded-full bg-emerald flex items-center justify-center text-white font-bold shrink-0">✓</div>
                        <div>
                          <h4 className="text-sm font-bold">Applied</h4>
                          <span className="text-xs text-secondary">CV submitted</span>
                        </div>
                      </div>

                      {/* Step 2: Resume Screened */}
                      <div className="flex items-center gap-3 flex-1">
                        <div className="w-8 h-8 rounded-full bg-emerald flex items-center justify-center text-white font-bold shrink-0">✓</div>
                        <div>
                          <h4 className="text-sm font-bold">Resume Screened</h4>
                          <span className="text-xs text-secondary">ATS Match calculated ({app.matchScore}%)</span>
                        </div>
                      </div>

                      {/* Step 3: Interview Shortlisted */}
                      <div className="flex items-center gap-3 flex-1">
                        <div className="w-8 h-8 rounded-full bg-indigo flex items-center justify-center text-white font-bold shrink-0 animate-pulse">✓</div>
                        <div>
                          <h4 className="text-indigo text-sm font-bold">Shortlisted</h4>
                          <span className="text-xs text-secondary">Interview preparation scheduled</span>
                        </div>
                      </div>

                    </div>
                  </div>

                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Modern Glassmorphic Recruiter Submission Modal */}
      {selectedJobForModal && (
        <div className="modal-backdrop flex items-center justify-center" style={{ position: 'fixed', inset: 0, zIndex: 100, background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(8px)' }}>
          <div className="card max-width-600 w-full glass-glow-card relative" style={{ maxHeight: '90vh', overflowY: 'auto', padding: '32px' }}>
            
            {/* Modal Header */}
            <div className="flex justify-between items-start mb-6">
              <div>
                <span className="company-tag">{selectedJobForModal.company}</span>
                <h3 className="font-heading mt-2" style={{ fontSize: '1.5rem' }}>Submit Corporate Application</h3>
              </div>
              <button 
                onClick={() => setSelectedJobForModal(null)}
                style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}
              >
                <X size={20} />
              </button>
            </div>

            {appSuccess ? (
              /* Success Panel View */
              <div className="text-center py-8 animate-fade-in">
                <CheckCircle size={56} className="text-emerald margin-auto mb-4 animate-bounce" />
                <h2 className="mb-2 text-emerald">Application Submitted!</h2>
                <p className="text-secondary text-sm">
                  Your CV and AI-tailored cover letter have been sent successfully to the recruiting manager at {selectedJobForModal.company}. You have been shortlisted for interview prep!
                </p>
              </div>
            ) : (
              /* Submission details form */
              <div className="flex flex-col gap-5">
                
                {/* Profile detail */}
                <div className="p-3 bg-black-20 rounded-lg border-glass flex gap-3 items-center">
                  <div className="w-10 h-10 rounded-full bg-indigo-glow border-glass flex items-center justify-center text-indigo">👩‍💻</div>
                  <div>
                    <h4 className="text-sm font-bold">{username}</h4>
                    <span className="text-xs text-secondary">Applicant Profile</span>
                  </div>
                </div>

                {/* Resume block */}
                <div className="form-group">
                  <label className="section-label">Attached Resume</label>
                  <div className="p-3 border-glass rounded-lg flex items-center gap-2 bg-black-20">
                    <FileText size={16} className="text-indigo" />
                    <span className="text-sm text-secondary">{resumeData.fileName} (Audit Active)</span>
                  </div>
                </div>

                {/* Score indicators */}
                <div className="grid-2 gap-4">
                  <div className="p-3 border-glass rounded-lg text-center bg-black-20">
                    <span className="text-xs text-secondary block">ATS Score</span>
                    <strong className="text-xl text-indigo block mt-1">{selectedJobForModal.matchScore}%</strong>
                  </div>
                  <div className="p-3 border-glass rounded-lg text-center bg-black-20">
                    <span className="text-xs text-secondary block">Deficiency Keywords</span>
                    <strong className="text-xl text-red block mt-1">
                      {selectedJobForModal.missingKeywords ? selectedJobForModal.missingKeywords.length : 0}
                    </strong>
                  </div>
                </div>

                {/* Cover letter draft */}
                <div className="form-group">
                  <label className="section-label">Tailored Cover Letter Draft</label>
                  <textarea 
                    className="sim-textarea text-xs text-secondary font-inter" 
                    rows="8" 
                    readOnly
                    value={selectedJobForModal.tailoredCoverLetter}
                    style={{ background: 'rgba(0,0,0,0.3)', width: '100%', resize: 'none', lineHeight: '1.5' }}
                  />
                </div>

                {/* Warning message */}
                {selectedJobForModal.missingKeywords && selectedJobForModal.missingKeywords.length > 0 && (
                  <div className="p-3 bg-danger-glow border-glass rounded-lg flex items-start gap-2.5">
                    <AlertCircle size={16} className="text-red shrink-0 mt-0.5" />
                    <p className="text-xs text-red" style={{ lineHeight: '1.4' }}>
                      <strong>ATS Scanner Advice:</strong> You are missing key industry keywords (e.g. {selectedJobForModal.missingKeywords.slice(0, 2).join(', ')}). We recommend adding these to your resume later, but you can submit the application now!
                    </p>
                  </div>
                )}

                {/* Modal Footer */}
                <div className="flex justify-end gap-3 pt-4 border-t-glass">
                  <button className="btn-secondary text-sm py-2 px-4" onClick={() => setSelectedJobForModal(null)} disabled={submittingApp}>
                    Cancel
                  </button>
                  <button className="btn-primary text-sm py-2 px-6 flex items-center gap-2" onClick={handleConfirmSubmit} disabled={submittingApp}>
                    {submittingApp ? (
                      <>
                        <div className="loading-spinner" style={{ width: '14px', height: '14px', borderSize: '2px', borderTopColor: 'transparent', margin: 0 }}></div>
                        <span>Submitting...</span>
                      </>
                    ) : (
                      <>
                        <Send size={14} />
                        <span>Confirm & Submit</span>
                      </>
                    )}
                  </button>
                </div>
              </div>
            )}

          </div>
        </div>
      )}

    </div>
  )
}

export default JobHunter
