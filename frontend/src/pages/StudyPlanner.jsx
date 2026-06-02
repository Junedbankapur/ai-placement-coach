import React, { useState, useEffect } from 'react'
import { api } from '../services/api'
import { Sparkles, Calendar, BookOpen, Clock, CheckSquare } from 'lucide-react'

/**
 * StudyPlanner Component - Displays timeline plans.
 */
function StudyPlanner() {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [duration, setDuration] = useState(7);
  const [error, setError] = useState('');

  async function loadPlans() {
    try {
      const data = await api.getStudyPlans();
      if (Array.isArray(data)) {
        setPlans(data);
      }
    } catch (err) {
      console.error('Failed to load plans:', err);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPlans();
  }, []);

  const handleGenerate = async () => {
    setError('');
    setGenerating(true);
    try {
      const newPlan = await api.generateStudyPlan(duration);
      if (newPlan && newPlan.id) {
        setPlans(prev => [newPlan, ...prev]);
      } else {
        setError(newPlan || 'Failed to generate study plan.');
      }
    } catch (err) {
      setError(err.message || 'An error occurred.');
    } finally {
      setGenerating(false);
    }
  };

  if (loading) return <div className="loading-spinner">Retrieving active roadmaps...</div>;

  // Extract current active plan details
  const activePlan = plans[0]; // The newest plan is first
  let parsedRoadmap = null;

  if (activePlan && activePlan.roadmapJson) {
    try {
      parsedRoadmap = JSON.parse(activePlan.roadmapJson);
    } catch (e) {
      console.error('Failed to parse roadmapJson:', e);
    }
  }

  return (
    <div className="page-content">
      <header className="page-header">
        <div>
          <h1>AI Study Planner</h1>
          <p>Generate highly structured, day-by-day technical placement roadmaps based on your skills gaps.</p>
        </div>
      </header>

      {error && <div className="error-alert">{error}</div>}

      <div className="planner-grid">
        {/* Settings Generator panel */}
        <div className="planner-setup card">
          <h3>Create Placement Strategy</h3>
          <p className="card-subtitle">Select your schedule commitment intensity</p>

          <div className="duration-select-row mt-4">
            <button
              className={`btn-dur-select ${duration === 7 ? 'active' : ''}`}
              onClick={() => setDuration(7)}
            >
              7-Day Intensive
            </button>
            <button
              className={`btn-dur-select ${duration === 30 ? 'active' : ''}`}
              onClick={() => setDuration(30)}
            >
              30-Day Placement
            </button>
          </div>

          <button className="btn-primary w-full mt-6" onClick={handleGenerate} disabled={generating}>
            <Sparkles size={16} />
            <span>{generating ? 'Drafting Roadmap Timeline...' : 'Generate New Study Plan'}</span>
          </button>
        </div>

        {/* Timeline viewer */}
        <div className="planner-timeline card">
          {generating ? (
            <div className="timeline-loading text-center">
              <div className="spinner-glow">🤖</div>
              <h4>Analyzing Extracted Weakness Vectors...</h4>
              <p>Constructing optimal day-by-day learning modules using Gemini AI...</p>
            </div>
          ) : parsedRoadmap ? (
            <div className="timeline-container">
              <div className="timeline-header-block">
                <Calendar size={24} className="text-indigo" />
                <h3>{parsedRoadmap.title || 'Personalized Strategy Plan'}</h3>
              </div>

              <div className="timeline-tree mt-6">
                {parsedRoadmap.days?.map((day, idx) => (
                  <div key={idx} className="timeline-node">
                    {/* Visual dot & line */}
                    <div className="timeline-axis">
                      <div className="timeline-dot">{day.dayNumber}</div>
                      <div className="timeline-line"></div>
                    </div>

                    {/* Module Content */}
                    <div className="timeline-content card">
                      <div className="timeline-node-header">
                        <h4>{day.topic}</h4>
                        <div className="timeline-meta">
                          <Clock size={14} className="text-secondary" />
                          <span>{day.estimatedHours} Hours Est.</span>
                        </div>
                      </div>

                      <ul className="timeline-tasks mt-4">
                        {day.tasks?.map((task, tid) => (
                          <li key={tid} className="task-item font-inter">
                            <CheckSquare size={16} className="text-indigo shrink-0" />
                            <span>{task}</span>
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="empty-state-timeline text-center">
              <BookOpen size={48} className="text-secondary margin-auto" />
              <p className="mt-4">No active study plans found. Generate a plan on the left to see your placement roadmaps!</p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default StudyPlanner
