import React, { useState, useEffect } from 'react'
import { api } from '../services/api'
import { Upload, FileText, CheckCircle, AlertCircle, Sparkles, ChevronRight } from 'lucide-react'

/**
 * ResumeUpload Component - Handles file upload and lists extracted resume traits.
 */
function ResumeUpload() {
  const [file, setFile] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fetchingLatest, setFetchingLatest] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadLatest() {
      try {
        const data = await api.getLatestResume();
        // If data is a string ("No resume uploaded yet."), keep analysis null
        if (data && typeof data === 'object' && data.id) {
          setAnalysis(data);
        }
      } catch (err) {
        console.error('Failed to load latest resume:', err);
      } finally {
        setFetchingLatest(false);
      }
    }
    loadLatest();
  }, []);

  const handleFileChange = (e) => {
    if (e.target.files[0]) {
      setFile(e.target.files[0]);
      setError('');
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a file first.');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const data = await api.uploadResume(file);
      if (data && typeof data === 'object' && data.id) {
        setAnalysis(data);
      } else {
        setError(data || 'Failed to analyze resume.');
      }
    } catch (err) {
      setError(err.message || 'An error occurred during upload.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-content">
      <header className="page-header">
        <div>
          <h1>AI Resume Auditor</h1>
          <p>Upload your PDF/Text resume to audit skills gaps and fetch placement metrics</p>
        </div>
      </header>

      <div className="resume-grid">
        {/* Upload Card */}
        <div className="upload-section card">
          <h3>Upload Resume</h3>
          <p className="card-subtitle">Supported formats: PDF, TXT (Max 5MB)</p>

          <form onSubmit={handleUpload} className="upload-form">
            <div className="drag-drop-zone">
              <input type="file" id="resumeFile" onChange={handleFileChange} accept=".pdf,.txt" />
              <label htmlFor="resumeFile" className="drop-label">
                <Upload size={36} className="text-indigo" />
                {file ? (
                  <span className="file-name-tag">{file.name}</span>
                ) : (
                  <span>Drag & drop or Click to browse</span>
                )}
              </label>
            </div>

            {error && <div className="error-alert">{error}</div>}

            <button type="submit" className="btn-primary w-full mt-4" disabled={loading}>
              <Sparkles size={16} />
              <span>{loading ? 'Auditing Resume...' : 'Analyze Resume'}</span>
            </button>
          </form>
        </div>

        {/* Audit Report Section */}
        <div className="report-section card">
          <h3>Resume Audit Report</h3>
          
          {fetchingLatest ? (
            <div className="loading-spinner">Retrieving audited records...</div>
          ) : analysis ? (
            <div className="report-container">
              {/* Extracted Skills */}
              <div className="report-block">
                <h4>Extracted Technical Skills</h4>
                <div className="skills-pill-box">
                  {analysis.skills?.map((skill, idx) => (
                    <span key={idx} className="skill-pill">
                      {skill}
                    </span>
                  ))}
                </div>
              </div>

              {/* Projects */}
              <div className="report-block">
                <h4>Projects Flagged</h4>
                <ul className="audit-list">
                  {analysis.projects?.map((proj, idx) => (
                    <li key={idx} className="audit-item font-inter">
                      <ChevronRight size={14} className="text-indigo" />
                      <span>{proj}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Strengths / Weaknesses */}
              <div className="audit-row">
                <div className="audit-col">
                  <h4 className="text-emerald">AI Strengths</h4>
                  <ul className="bullet-list">
                    {analysis.strengths?.map((str, idx) => (
                      <li key={idx} className="bullet-item">
                        <CheckCircle size={16} className="text-emerald shrink-0" />
                        <span>{str}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="audit-col">
                  <h4 className="text-red">AI Weaknesses</h4>
                  <ul className="bullet-list">
                    {analysis.weaknesses?.map((weak, idx) => (
                      <li key={idx} className="bullet-item">
                        <AlertCircle size={16} className="text-red shrink-0" />
                        <span>{weak}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          ) : (
            <div className="empty-state-report">
              <FileText size={48} className="text-secondary" />
              <p>No resume uploaded. Upload your CV on the left to review your AI Audit Report.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default ResumeUpload
