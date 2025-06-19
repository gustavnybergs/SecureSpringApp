import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// JWT-token skickas automatiskt med alla requests
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt-token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Automatisk utloggning vid 401 Unauthorized
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt-token');
      localStorage.removeItem('user-info');
      window.location.reload();
    }
    return Promise.reject(error);
  }
);

export const authService = {
  // Registrera ny användare med GDPR-samtycke
  register: async (userData) => {
    try {
      const requestData = {
        email: userData.email,
        password: userData.password,
        fullName: userData.fullName || '',
        username: userData.email.split('@')[0],
        consentGiven: userData.consentGiven || false
      };
      
      const response = await axios.post(`${API_BASE_URL}/auth/register`, requestData);
      
      return {
        success: true,
        message: response.data.message,
        user: {
          id: response.data.userId,
          email: response.data.email,
          username: response.data.username,
          role: response.data.role
        }
      };
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Registrering misslyckades';
      throw new Error(errorMessage);
    }
  },

  // Logga in användare
  login: async (credentials) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials);
        
        const token = response.data;
        
        if (!token || typeof token !== 'string') {
            throw new Error('Ogiltig token från servern');
        }
        
        localStorage.setItem('jwt-token', token);
        
        // Extrahera användarinfo från JWT payload
        const tokenPayload = authService.decodeJwtPayload(token);
        
        const userInfo = {
            id: tokenPayload.sub,
            email: credentials.email,
            username: tokenPayload.username,
            role: tokenPayload.roles?.[0] || 'USER'
        };
        
        localStorage.setItem('user-info', JSON.stringify(userInfo));
        
        return { token, user: userInfo };
    } catch (error) {
        const errorMessage = error.response?.data?.message || 
                            error.response?.data?.error || 
                            'Inloggning misslyckades';
        throw new Error(errorMessage);
    }
  },

  // Logga ut användare
  logout: async () => {
    try {
      localStorage.removeItem('jwt-token');
      localStorage.removeItem('user-info');
    } catch (error) {
      localStorage.removeItem('jwt-token');
      localStorage.removeItem('user-info');
    }
  },

  // Ta bort eget konto
  deleteAccount: async () => {
    try {
      await axios.delete(`${API_BASE_URL}/user/me`);
      
      localStorage.removeItem('jwt-token');
      localStorage.removeItem('user-info');
      
      return { success: true, message: 'Konto raderat framgångsrikt' };
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error || 
                          'Kontoborttagning misslyckades';
      throw new Error(errorMessage);
    }
  },

  // Kontrollera om användare är inloggad
  isAuthenticated: () => {
    const token = localStorage.getItem('jwt-token');
    if (!token) return false;
    
    try {
      const payload = authService.decodeJwtPayload(token);
      const now = Math.floor(Date.now() / 1000);
      return payload.exp > now;
    } catch (error) {
      return false;
    }
  },

  // Hämta aktuell användare från localStorage
  getCurrentUser: () => {
    const userInfo = localStorage.getItem('user-info');
    return userInfo ? JSON.parse(userInfo) : null;
  },

  // Dekoda JWT payload för UI-information
  decodeJwtPayload: (token) => {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      throw new Error('Ogiltig JWT token');
    }
  },

  // Validera token mot backend
  validateToken: async () => {
    try {
      const token = localStorage.getItem('jwt-token');
      if (!token) throw new Error('Ingen token hittad');
      
      const response = await axios.post(`${API_BASE_URL}/auth/validate-token`, {
        token: token
      });
      
      return response.data.valid;
    } catch (error) {
      throw new Error('Token-validering misslyckades');
    }
  }
};