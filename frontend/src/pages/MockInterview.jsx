import React, { useState } from 'react'
import { api } from '../services/api'
import { ArrowLeft, ArrowRight, Sparkles, Award, ChevronDown, ChevronUp } from 'lucide-react'

/**
 * MockInterview Component - Displays interactive mock questions and renders AI feedback reports.
 */
function MockInterview({ category = 'General', questions = [], onBack }) {
  const [currentIdx, setCurrentIdx] = useState(0);
  const [userAnswers, setUserAnswers] = useState(() => {
    return questions && Array.isArray(questions) ? Array(questions.length).fill('') : [];
  });
  const [evaluation, setEvaluation] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [openAccordions, setOpenAccordions] = useState({});

  // Safe defense against empty or undefined questions to prevent crashes
  if (!questions || !Array.isArray(questions) || questions.length === 0) {
    return (
      <div className="page-content">
        <div className="card text-center" style={{ padding: '40px', maxWidth: '500px', margin: '60px auto' }}>
          <Award size={48} className="text-indigo" style={{ margin: '0 auto 20px auto', display: 'block' }} />
          <h3>No Mock Session Active</h3>
          <p className="mt-4" style={{ color: '#a1a1aa', marginBottom: '24px' }}>
            Please select a category and generate questions before entering the placement simulator.
          </p>
          <button className="btn-primary" onClick={onBack} style={{ margin: '0 auto' }}>
            <span>Go to Setup Arena</span>
          </button>
        </div>
      </div>
    );
  }

  const handleTextChange = (val) => {
    const updated = [...userAnswers];
    updated[currentIdx] = val;
    setUserAnswers(updated);
  };

  const handleNext = () => {
    if (currentIdx < questions.length - 1) {
      setCurrentIdx(currentIdx + 1);
    }
  };

  const handlePrev = () => {
    if (currentIdx > 0) {
      setCurrentIdx(currentIdx - 1);
    }
  };

  const handleSubmit = async () => {
    // Basic validation
    if (userAnswers.some(ans => ans.trim() === '')) {
      if (!window.confirm('Some answers are left blank. Are you sure you want to submit?')) {
        return;
      }
    }

    setError('');
    setLoading(true);
    try {
      // Build request payload: { category: "...", answers: [ { questionText: "...", sampleAnswer: "...", userAnswer: "..." } ] }
      const answersPayload = questions.map((q, idx) => ({
        questionText: q.questionText,
        sampleAnswer: q.sampleAnswer,
        userAnswer: userAnswers[idx]
      }));

      const data = await api.submitMockSession(category, answersPayload);
      if (data && data.score !== undefined) {
        setEvaluation(data);
      } else {
        setError(data || 'Failed to submit mock interview.');
      }
    } catch (err) {
      setError(err.message || 'An error occurred during grading.');
    } finally {
      setLoading(false);
    }
  };

  const toggleAccordion = (idx) => {
    setOpenAccordions(prev => ({
      ...prev,
      [idx]: !prev[idx]
    }));
  };

  if (loading) {
    return (
      <div className="fullscreen-loading">
        <div className="spinner-glow">🤖</div>
        <h2>Gemini AI is Grading Your Mock Session...</h2>
        <p>Analyzing technical details, measuring accuracy, and writing strategy strategy reports...</p>
      </div>
    )
  }

  // If evaluation results are loaded, display the Score Card
  if (evaluation) {
    return (
      <div className="page-content">
        <header className="page-header">
          <div>
            <h1>Interview Performance Review</h1>
            <p>Granular AI feedback and scorecard for your <strong>{category}</strong> mock exam</p>
          </div>
          <button className="btn-secondary" onClick={onBack}>
            <span>Try Another Mock</span>
          </button>
        </header>

        <div className="eval-grid">
          {/* Score Card Panel */}
          <div className="score-panel card text-center">
            <Award size={48} className="text-indigo margin-auto" />
            <h3>Overall Score</h3>
            <div className="score-radial">
              <span className="score-num">{evaluation.score}</span>
              <span className="score-total">/100</span>
            </div>
            <p className="score-verdict">
              {evaluation.score >= 80 ? 'Placement Ready!' : evaluation.score >= 60 ? 'Needs Solidification' : 'Requires Focus'}
            </p>
          </div>

          {/* AI Feedback Panel */}
          <div className="feedback-panel card">
            <h3>Recruiter Feedback Summary</h3>
            <p className="card-subtitle">Identified strengths, details missed, and key focal points</p>
            <div className="feedback-content font-inter">
              {evaluation.feedback?.split('\n').map((line, i) => (
                <p key={i} className="feedback-paragraph">{line}</p>
              ))}
            </div>
          </div>
        </div>

        {/* Detailed Question Review Accordion */}
        <div className="accordion-section card">
          <h3>Question-by-Question Comparison</h3>
          <p className="card-subtitle">Review what you wrote side-by-side with the interviewer's ideal reference response</p>

          <div className="accordion-list mt-6">
            {questions.map((q, idx) => {
              const isOpen = !!openAccordions[idx];
              return (
                <div key={idx} className="accordion-item">
                  <button className="accordion-header" onClick={() => toggleAccordion(idx)}>
                    <div className="header-info">
                      <span className="q-badge">Q{idx + 1}</span>
                      <h4>{q.questionText}</h4>
                    </div>
                    {isOpen ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                  </button>

                  {isOpen && (
                    <div className="accordion-body">
                      <div className="qa-compare-grid">
                        <div className="qa-compare-col border-red">
                          <h5>Your Submitted Response:</h5>
                          <p className="qa-text italic">{userAnswers[idx] || '[Left Blank]'}</p>
                        </div>
                        <div className="qa-compare-col border-emerald">
                          <h5>Ideal Sample Answer:</h5>
                          <p className="qa-text text-emerald-light">{q.sampleAnswer}</p>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        </div>
      </div>
    )
  }

  // Active Simulator View
  return (
    <div className="page-content">
      <header className="page-header">
        <div>
          <h1>Mock Placement Simulator</h1>
          <p>Topic: <strong>{category}</strong> | Answer the questions in depth to score maximum points.</p>
        </div>
        <span className="badge">Question {currentIdx + 1} of {questions.length}</span>
      </header>

      {error && <div className="error-alert">{error}</div>}

      <div className="mock-sim-card card">
        <div className="sim-header">
          <Award size={24} className="text-indigo" />
          <h3>{questions[currentIdx].questionText}</h3>
        </div>

        <div className="sim-body mt-6">
          <label htmlFor="answerArea">Type your technical response below:</label>
          <textarea
            id="answerArea"
            value={userAnswers[currentIdx]}
            onChange={(e) => handleTextChange(e.target.value)}
            placeholder="Interviewer: 'Explain in your own words, including core definitions and features...'"
            rows={10}
            className="sim-textarea"
          />
        </div>

        <div className="sim-footer mt-6">
          <button className="btn-secondary" onClick={handlePrev} disabled={currentIdx === 0}>
            <ArrowLeft size={16} />
            <span>Previous</span>
          </button>

          {currentIdx < questions.length - 1 ? (
            <button className="btn-primary" onClick={handleNext}>
              <span>Next Question</span>
              <ArrowRight size={16} />
            </button>
          ) : (
            <button className="btn-success" onClick={handleSubmit}>
              <Sparkles size={16} />
              <span>Submit Mock Interview</span>
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default MockInterview
