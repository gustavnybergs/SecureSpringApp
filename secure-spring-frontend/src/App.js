import React, { useState, useEffect } from 'react';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import Dashboard from './components/User/Dashboard';
import { authService } from './services/authService';
import './App.css';

function App() {
  const [currentView, setCurrentView] = useState('login'); // 'login', 'register', 'dashboard'
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Kontrollera om användaren redan är inloggad
    const checkAuth = async () => {
      if (authService.isAuthenticated()) {
        try {
          // Validera token med backend (valfritt)
          await authService.validateToken();
          setIsAuthenticated(true);
          setCurrentView('dashboard');
        } catch (error) {
          console.log('Token validation failed:', error.message);
          // Token är ogiltig, fortsätt med login
          setIsAuthenticated(false);
        }
      }
      setLoading(false);
    };

    checkAuth();
  }, []);

  const handleLoginSuccess = (result) => {
    setIsAuthenticated(true);
    setCurrentView('dashboard');
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setCurrentView('login');
  };

  const handleSwitchToRegister = () => {
    setCurrentView('register');
  };

  const handleSwitchToLogin = () => {
    setCurrentView('login');
  };

  const handleRegisterSuccess = () => {
    // Efter lyckad registrering, visa login
    setCurrentView('login');
  };

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        fontSize: '18px'
      }}>
        Laddar SecureSpringApp...
      </div>
    );
  }

  return (
    <div className="App">
      <header style={{ 
        background: '#343a40', 
        color: 'white', 
        padding: '15px 0',
        textAlign: 'center',
        marginBottom: '20px'
      }}>
        <h1>🔐 SecureSpringApp</h1>
        <p style={{ margin: '5px 0 0 0', fontSize: '14px' }}>
          Säker webbapplikation med JWT-autentisering
        </p>
      </header>

      <main>
        {!isAuthenticated && currentView === 'login' && (
          <Login 
            onLoginSuccess={handleLoginSuccess}
            onSwitchToRegister={handleSwitchToRegister}
          />
        )}

        {!isAuthenticated && currentView === 'register' && (
          <Register 
            onRegisterSuccess={handleRegisterSuccess}
            onSwitchToLogin={handleSwitchToLogin}
          />
        )}

        {isAuthenticated && currentView === 'dashboard' && (
          <Dashboard onLogout={handleLogout} />
        )}
      </main>

      <footer style={{ 
        textAlign: 'center', 
        padding: '20px',
        color: '#666',
        fontSize: '12px',
        marginTop: '50px'
      }}>
        <p>SecureSpringApp - Projektuppgift IT-säkerhet</p>
        <p>
          Backend: Spring Boot (port 8080) • 
          Frontend: React (port 3000)
        </p>
      </footer>
    </div>
  );
}

export default App;