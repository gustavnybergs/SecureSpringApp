import React, { useState } from 'react';
import { authService } from '../../services/authService';

/**
 * Kontoborttagningskomponent med säkerhetsvalidering.
 * Kräver explicit bekräftelse för att förhindra oavsiktlig borttagning.
 */
const DeleteAccount = ({ user, onDeleteSuccess, onCancel }) => {
  const [confirmText, setConfirmText] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const CONFIRM_TEXT = 'RADERA MITT KONTO';

  const handleDelete = async () => {
    if (confirmText !== CONFIRM_TEXT) {
      setError(`Du måste skriva exakt: ${CONFIRM_TEXT}`);
      return;
    }

    setLoading(true);
    setError('');

    try {
      await authService.deleteAccount();
      alert('Ditt konto har raderats framgångsrikt.');
      onDeleteSuccess();
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '500px', margin: '50px auto', padding: '20px' }}>
      <div style={{ 
        border: '3px solid #dc3545', 
        borderRadius: '8px', 
        padding: '30px',
        background: '#fff5f5'
      }}>
        <h2 style={{ color: '#dc3545', textAlign: 'center' }}>
          ⚠️ Radera konto
        </h2>
        
        <div style={{ marginBottom: '20px' }}>
          <h4>Detta kommer att:</h4>
          <ul style={{ color: '#721c24' }}>
            <li>Permanent radera ditt konto</li>
            <li>Ta bort all din personliga data</li>
            <li>Logga ut dig från alla enheter</li>
            <li>Denna åtgärd kan INTE ångras</li>
          </ul>
        </div>

        <div style={{ 
          background: '#f8d7da', 
          padding: '15px', 
          borderRadius: '4px',
          marginBottom: '20px'
        }}>
          <p><strong>Konto som kommer raderas:</strong></p>
          <p>📧 Email: {user?.email}</p>
          <p>👤 Namn: {user?.fullName || 'Inte angivet'}</p>
          <p>🆔 ID: {user?.id}</p>
        </div>

        {error && (
          <div style={{ 
            color: 'red', 
            background: '#ffebee', 
            padding: '10px', 
            borderRadius: '4px', 
            marginBottom: '20px' 
          }}>
            {error}
          </div>
        )}

        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
            För att bekräfta, skriv: <code>{CONFIRM_TEXT}</code>
          </label>
          <input
            type="text"
            value={confirmText}
            onChange={(e) => setConfirmText(e.target.value)}
            placeholder={CONFIRM_TEXT}
            style={{
              width: '100%',
              padding: '10px',
              border: '2px solid #dc3545',
              borderRadius: '4px',
              fontSize: '14px'
            }}
          />
        </div>

        <div style={{ 
          display: 'flex', 
          gap: '15px', 
          justifyContent: 'center' 
        }}>
          <button
            onClick={onCancel}
            style={{
              padding: '12px 24px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Avbryt
          </button>
          
          <button
            onClick={handleDelete}
            disabled={loading || confirmText !== CONFIRM_TEXT}
            style={{
              padding: '12px 24px',
              backgroundColor: loading || confirmText !== CONFIRM_TEXT ? '#ccc' : '#dc3545',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: loading || confirmText !== CONFIRM_TEXT ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Raderar...' : 'Radera konto permanent'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteAccount;