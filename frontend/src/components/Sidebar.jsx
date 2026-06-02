import React from 'react'
import { LayoutDashboard, FileText, Trophy, BookOpen, LogOut, User, Briefcase } from 'lucide-react'
import { api } from '../services/api'

/**
 * Sidebar Component - Provides left-hand glassmorphic navigation.
 */
function Sidebar({ currentPage, setCurrentPage }) {
  const username = localStorage.getItem('username') || 'User';

  const menuItems = [
    { id: 'dashboard', name: 'Dashboard', icon: LayoutDashboard },
    { id: 'resume', name: 'Resume Analyzer', icon: FileText },
    { id: 'questions', name: 'Mock Interview', icon: Trophy },
    { id: 'planner', name: 'Study Planner', icon: BookOpen },
    { id: 'jobs', name: 'Placement Board', icon: Briefcase }
  ];

  const handleLogout = () => {
    api.logout();
    window.location.reload();
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-logo">🤖</div>
        <div className="brand-text">
          <h2>AI Coach</h2>
          <span>Placement Hub</span>
        </div>
      </div>

      <nav className="sidebar-menu">
        {menuItems.map((item) => {
          const Icon = item.icon;
          return (
            <button
              key={item.id}
              className={`menu-item ${currentPage === item.id || (item.id === 'questions' && currentPage === 'mock-interview') ? 'active' : ''}`}
              onClick={() => setCurrentPage(item.id)}
            >
              <Icon className="menu-icon" size={20} />
              <span>{item.name}</span>
            </button>
          )
        })}
      </nav>

      <div className="sidebar-footer">
        <div className="user-profile">
          <div className="user-avatar">
            <User size={18} />
          </div>
          <div className="user-info">
            <span className="user-name">{username}</span>
            <span className="user-role">Student</span>
          </div>
        </div>
        <button className="btn-logout" onClick={handleLogout}>
          <LogOut size={18} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  )
}

export default Sidebar
