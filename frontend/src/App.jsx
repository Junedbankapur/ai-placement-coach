import React, { useState, useEffect } from 'react'
import Sidebar from './components/Sidebar'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import ResumeUpload from './pages/ResumeUpload'
import QuestionGenerator from './pages/QuestionGenerator'
import MockInterview from './pages/MockInterview'
import StudyPlanner from './pages/StudyPlanner'
import JobHunter from './pages/JobHunter'

/**
 * Main App Component - Orchestrates overall app layout, login verification state, 
 * and custom stateless page switching.
 */
function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authView, setAuthView] = useState('login'); // 'login' or 'register'
  const [currentPage, setCurrentPage] = useState('dashboard'); // 'dashboard', 'resume', 'questions', 'mock-interview', 'planner'
  
  // States to pass data between QuestionGenerator and MockInterview
  const [activeCategory, setActiveCategory] = useState('');
  const [activeQuestions, setActiveQuestions] = useState([]);

  // Check login state on component mount
  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    setCurrentPage('dashboard');
  };

  const handleStartMock = (category, questions) => {
    setActiveCategory(category);
    setActiveQuestions(questions);
    setCurrentPage('mock-interview'); // Switch to simulation page
  };

  const handleBackToSetup = () => {
    setCurrentPage('questions'); // Go back to settings page
  };

  // 1. Unauthenticated Auth Flow Views
  if (!isAuthenticated) {
    return authView === 'login' ? (
      <Login
        onLoginSuccess={handleLoginSuccess}
        switchToRegister={() => setAuthView('register')}
      />
    ) : (
      <Register
        switchToLogin={() => setAuthView('login')}
      />
    )
  }

  // 2. Render Page Content based on current navigation State
  const renderPage = () => {
    switch (currentPage) {
      case 'dashboard':
        return <Dashboard setCurrentPage={setCurrentPage} />;
      case 'resume':
        return <ResumeUpload />;
      case 'questions':
        return <QuestionGenerator onStartMock={handleStartMock} />;
      case 'mock-interview':
        return (
          <MockInterview
            category={activeCategory}
            questions={activeQuestions}
            onBack={handleBackToSetup}
          />
        );
      case 'planner':
        return <StudyPlanner />;
      case 'jobs':
        return (
          <JobHunter 
            onPracticeRole={(category, questions) => handleStartMock(category, questions)} 
          />
        );
      default:
        return <Dashboard setCurrentPage={setCurrentPage} />;
    }
  };

  // 3. Authenticated Application Layout Dashboard
  return (
    <div className="app-layout">
      {/* Navigation Sidebar */}
      <Sidebar currentPage={currentPage} setCurrentPage={setCurrentPage} />
      
      {/* Main Main Scroll Container */}
      <main className="main-content">
        {renderPage()}
      </main>
    </div>
  )
}

export default App
