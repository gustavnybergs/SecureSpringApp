import React, { useState, useEffect } from 'react';
import { authService } from '../../services/authService';
import DeleteAccount from './DeleteAccount';

/**
 * Huvuddashboard för inloggade användare.
 * Visar profilinformation och tillhandahåller säkerhetsfunktioner.
 */
const Dashboard = ({ onLogout }) => {
  const [user, setUser] = useState(null);
  const [showDeleteAccount, setShowDeleteAccount] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const userInfo = authService.getCurrentUser();
    setUser(userInfo);
    setLoading(false);
  }, []);

  const handleLogout = async () => {
    try {
      await authService.logout();
      onLogout();
    } catch (error) {
      // Säkerställ utloggning även vid nätverksfel
      onLogout();
    }
  };

  const handleDeleteSuccess = () => {
    onLogout();
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        Laddar...
      </div>
    );
  }

  if (showDeleteAccount) {
    return (
      <DeleteAccount 
        user={user}
        onDeleteSuccess={handleDeleteSuccess}
        onCancel={() => setShowDeleteAccount(false)}
      />
    );
  }

  return (
    <div style={{ maxWidth: '600px', margin: '50px auto', padding: '20px' }}>
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: '30px'
      }}>
        <h1>Välkommen!</h1>
        <button
          onClick={handleLogout}
          style={{
            padding: '8px 16px',
            backgroundColor: '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Logga ut
        </button>
      </div>

      <div style={{ 
        background: '#f8f9fa', 
        padding: '20px', 
        borderRadius: '8px',
        marginBottom: '30px'
      }}>
        <h3>Din profil</h3>
        <p><strong>Email:</strong> {user?.email || 'Inte tillgänglig'}</p>
        {/*<p><strong>Fullständigt namn:</strong> {user?.fullName || 'Inte angivet'}</p> */}
        <p><strong>Roll:</strong> {user?.role || 'USER'}</p>
        <p><strong>Användar-ID:</strong> {user?.id || 'Inte tillgängligt'}</p>
      </div>

      
{/* Temporärt dold för presentation
      <div style={{ 
        background: '#fff3cd', 
        padding: '20px', 
        borderRadius: '8px',
        marginBottom: '30px'
      }}>
        <h3>🔐 JWT-Token Information</h3>
        <p>Du är inloggad med en JWT-token som automatiskt skickas med alla API-anrop.</p>
        <p><strong>Token finns:</strong> {authService.isAuthenticated() ? '✅ Ja' : '❌ Nej'}</p>
        <p><small>
          Token skickas automatiskt i Authorization header som: 
          <code style={{ background: '#e9ecef', padding: '2px 4px' }}>
            Bearer &lt;din-jwt-token&gt;
          </code>
        </small></p>
      </div>
*/}

      <div style={{ 
        background: '#d1ecf1', 
        padding: '20px', 
        borderRadius: '8px',
        marginBottom: '30px'
      }}>
        <h3>📱 Applikationsfunktioner</h3>
        <ul>
          <li>✅ Användarregistrering med validering</li>
          <li>✅ Säker inloggning med JWT</li>
          <li>✅ Automatisk token-hantering</li>
          <li>✅ Profilinformation</li>
          <li>✅ Säker utloggning</li>
          <li>✅ Kontoborttagning</li>
        </ul>
      </div>

      <div style={{ 
        border: '2px solid #dc3545', 
        borderRadius: '8px', 
        padding: '20px',
        background: '#f8d7da'
      }}>
        <h3 style={{ color: '#721c24' }}>⚠️ Farlig zon</h3>
        <p style={{ marginBottom: '15px' }}>
          Detta kommer permanent ta bort ditt konto och all associerad data.
          Denna åtgärd kan inte ångras.
        </p>
        <button
          onClick={() => setShowDeleteAccount(true)}
          style={{
            padding: '10px 20px',
            backgroundColor: '#dc3545',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Ta bort mitt konto
        </button>
      </div>
    </div>
  );
};

export default Dashboard;