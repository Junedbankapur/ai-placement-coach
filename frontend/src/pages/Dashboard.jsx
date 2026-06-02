import React, { useEffect, useState } from 'react'
import { api } from '../services/api'
import { BarChart3, Award, FileSpreadsheet, Sparkles, CheckCircle2 } from 'lucide-react'
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, Filler } from 'chart.js'
import { Line } from 'react-chartjs-2'

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, Filler);

/**
 * Dashboard Component - Student performance metrics and progress charts.
 */
function Dashboard({ setCurrentPage }) {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadStats() {
      try {
        const data = await api.getDashboardStats();
        setStats(data);
      } catch (err) {
        setError('Could not compile stats: ' + err.message);
      } finally {
        setLoading(false);
      }
    }
    loadStats();
  }, []);

  if (loading) return <div className="loading-spinner">Loading placement analytics...</div>;
  if (error) return <div className="error-alert">{error}</div>;

  // Prepare Chart.js dataset
  const chartData = {
    labels: stats?.interviewHistory?.map(p => p.date) || [],
    datasets: [
      {
        fill: true,
        label: 'Mock Score (%)',
        data: stats?.interviewHistory?.map(p => p.score) || [],
        borderColor: '#6366f1', // Indigo glow
        backgroundColor: 'rgba(99, 102, 241, 0.1)',
        tension: 0.4,
        pointBackgroundColor: '#10b981', // Success green for nodes
        pointBorderColor: '#fff',
        pointHoverRadius: 8
      }
    ]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: '#121214',
        titleFont: { family: 'Outfit', size: 14 },
        bodyFont: { family: 'Inter', size: 12 },
        borderColor: 'rgba(255, 255, 255, 0.1)',
        borderWidth: 1
      }
    },
    scales: {
      y: {
        min: 0,
        max: 100,
        grid: { color: 'rgba(255, 255, 255, 0.03)' },
        ticks: { color: '#a1a1aa', font: { family: 'Inter' } }
      },
      x: {
        grid: { display: false },
        ticks: { color: '#a1a1aa', font: { family: 'Inter' } }
      }
    }
  };

  return (
    <div className="page-content">
      <header className="page-header">
        <div>
          <h1>Placement Analytics Dashboard</h1>
          <p>Real-time updates on your preparation metrics and skills gaps</p>
        </div>
        <button className="btn-primary" onClick={() => setCurrentPage('questions')}>
          <Sparkles size={16} />
          <span>Launch AI Mock Exam</span>
        </button>
      </header>

      {/* Stats Cards Row */}
      <div className="stats-grid">
        <div className="stats-card">
          <div className="stats-icon text-indigo">
            <BarChart3 size={24} />
          </div>
          <div className="stats-info">
            <span className="stats-label">Total Questions Practiced</span>
            <h3>{stats?.totalQuestionsPracticed || 0}</h3>
          </div>
        </div>

        <div className="stats-card">
          <div className="stats-icon text-emerald">
            <Award size={24} />
          </div>
          <div className="stats-info">
            <span className="stats-label">Average Score</span>
            <h3>{stats?.averageScore ? `${stats.averageScore}%` : 'N/A'}</h3>
          </div>
        </div>

        <div className="stats-card">
          <div className="stats-icon text-amber">
            <FileSpreadsheet size={24} />
          </div>
          <div className="stats-info">
            <span className="stats-label">Extracted Skills Count</span>
            <h3>{stats?.skillsCount || 0}</h3>
          </div>
        </div>
      </div>

      <div className="dashboard-layout">
        {/* Progress Chart */}
        <div className="chart-panel card">
          <h3>Mock Scores Progress Trend</h3>
          <div className="chart-container">
            {stats?.interviewHistory?.length > 0 ? (
              <Line data={chartData} options={chartOptions} />
            ) : (
              <div className="empty-state">
                <p>No mock history recorded. Take your first AI Mock Interview to draw progress charts!</p>
              </div>
            )}
          </div>
        </div>

        {/* Weak Areas Card */}
        <div className="weaknesses-panel card">
          <h3>Target Improvement Areas</h3>
          <p className="card-subtitle">AI-detected skill gaps based on your resume and interview logs</p>
          
          <ul className="weakness-list">
            {stats?.weakAreas?.length > 0 ? (
              stats.weakAreas.map((area, idx) => (
                <li key={idx} className="weakness-item">
                  <span className="danger-badge">!</span>
                  <span>{area}</span>
                </li>
              ))
            ) : (
              <li className="weakness-item-empty">
                <CheckCircle2 className="text-emerald" size={20} />
                <span>No major weaknesses flagged yet! Good work.</span>
              </li>
            )}
          </ul>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
