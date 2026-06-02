// ==========================================
// FRONTEND API CLIENT SERVICE
// ==========================================
// Handles all communication between the React Frontend and Spring Boot Backend.
// Uses modern browser native 'fetch' and automatically attaches JWT Bearer Tokens.

const BASE_URL = '/api'; // Vite proxy redirects this to http://localhost:8080/api

/**
 * Helper to fetch with JWT token attached in headers
 */
async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('jwtToken');
  
  // Set default headers
  const headers = {
    ...options.headers,
  };

  // If JWT exists, attach it as a Bearer Token in the Authorization Header
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // If request contains body and is not Multipart file, set Content-Type to JSON
  if (options.body && !(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers
  });

  // Handle unauthorized redirects (JWT expired or invalid)
  if (response.status === 401) {
    localStorage.clear();
    window.location.reload();
    throw new Error('Session expired. Please login again.');
  }

  const textData = await response.text();
  
  // Check if response is JSON, otherwise return plain text
  try {
    return JSON.parse(textData);
  } catch (e) {
    return textData;
  }
}

export const api = {
  // 1. Authentication Services
  login: async (username, password) => {
    const data = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    if (data.token) {
      localStorage.setItem('jwtToken', data.token);
      localStorage.setItem('username', data.username);
      localStorage.setItem('userId', data.userId);
      localStorage.setItem('role', data.role);
    }
    return data;
  },

  register: async (username, email, password) => {
    return await apiFetch('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password })
    });
  },

  logout: () => {
    localStorage.clear();
  },

  // 2. Resume Services
  uploadResume: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return await apiFetch('/resume/upload', {
      method: 'POST',
      body: formData
    });
  },

  getLatestResume: async () => {
    return await apiFetch('/resume/latest', {
      method: 'GET'
    });
  },

  // 3. Question Services
  generateQuestions: async (category, difficulty) => {
    return await apiFetch(`/questions/generate?category=${category}&difficulty=${difficulty}`, {
      method: 'GET'
    });
  },

  // 4. Mock Interview Services
  submitMockSession: async (category, answers) => {
    return await apiFetch('/mock/submit', {
      method: 'POST',
      body: JSON.stringify({ category, answers })
    });
  },

  getMockHistory: async () => {
    return await apiFetch('/mock/history', {
      method: 'GET'
    });
  },

  // 5. Study Plan Services
  generateStudyPlan: async (durationDays) => {
    return await apiFetch(`/studyplan/generate?durationDays=${durationDays}`, {
      method: 'POST'
    });
  },

  getStudyPlans: async () => {
    return await apiFetch('/studyplan/history', {
      method: 'GET'
    });
  },

  // 6. Dashboard Services
  getDashboardStats: async () => {
    return await apiFetch('/dashboard/stats', {
      method: 'GET'
    });
  },

  // 7. Job Agent Services
  getJobRecommendations: async () => {
    return await apiFetch('/agent/jobs', {
      method: 'GET'
    });
  }
};
