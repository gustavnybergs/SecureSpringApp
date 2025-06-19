import React, { useState } from 'react';
import { authService } from '../../services/authService';

/**
 * Registreringskomponent för nya användare.
 * Validerar lösenordsstyrka enligt säkerhetskrav och kräver GDPR-samtycke.
 */
const Register = ({ onRegisterSuccess, onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
    consentGiven: false
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, type, checked, value } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
    setError('');
    setSuccess('');
  };

  const validatePassword = (password) => {
    const minLength = password.length >= 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasNumbers = (password.match(/\d/g) || []).length >= 2;
    const hasSpecialChars = (password.match(/[!@#$%&*]/g) || []).length >= 2;

    if (!minLength) return 'Lösenord måste vara minst 8 tecken';
    if (!hasUpperCase) return 'Lösenord måste innehålla minst 1 stor bokstav';
    if (!hasNumbers) return 'Lösenord måste innehålla minst 2 siffror';
    if (!hasSpecialChars) return 'Lösenord måste innehålla minst 2 specialtecken (! @ # $ % & *)';
    
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    const passwordError = validatePassword(formData.password);
    if (passwordError) {
      setError(passwordError);
      setLoading(false);
      return;
    }

    if (!formData.consentGiven) {
      setError('Du måste ge samtycke till datalagring för att registrera dig.');
      setLoading(false);
      return;
    }

    try {
      const result = await authService.register(formData);
      setSuccess('Registrering lyckad! Du kan nu logga in.');
      
      setTimeout(() => {
        onSwitchToLogin();
      }, 2000);
      
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px' }}>
      <h2>Registrera dig</h2>
      
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

      {success && (
        <div style={{ 
          color: 'green', 
          background: '#e8f5e8', 
          padding: '10px', 
          borderRadius: '4px', 
          marginBottom: '20px' 
        }}>
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            style={{
              width: '100%',
              padding: '8px',
              marginTop: '5px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Fullständigt namn (valfritt):</label>
          <input
            type="text"
            name="fullName"
            value={formData.fullName}
            onChange={handleChange}
            style={{
              width: '100%',
              padding: '8px',
              marginTop: '5px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label>Lösenord:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            style={{
              width: '100%',
              padding: '8px',
              marginTop: '5px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
          <small style={{ color: '#666', fontSize: '12px' }}>
            Krav: Minst 8 tecken, 1 stor bokstav, 2 siffror, 2 specialtecken (! @ # $ % & *)
          </small>
        </div>

        <div style={{ 
          marginBottom: '20px',
          padding: '15px',
          border: '2px solid #007bff',
          borderRadius: '8px',
          background: '#f8f9fa'
        }}>
          <div style={{ 
            display: 'flex', 
            alignItems: 'flex-start', 
            gap: '10px' 
          }}>
            <input
              type="checkbox"
              id="consentGiven"
              name="consentGiven"
              checked={formData.consentGiven}
              onChange={handleChange}
              required
              style={{
                marginTop: '3px',
                transform: 'scale(1.2)'
              }}
            />
            <label 
              htmlFor="consentGiven" 
              style={{ 
                fontSize: '14px', 
                lineHeight: '1.4',
                cursor: 'pointer'
              }}
            >
              <strong>Jag samtycker till datalagring enligt GDPR</strong>
              <br />
              <small style={{ color: '#666' }}>
                Jag godkänner att mina personuppgifter (email, namn, lösenord) 
                sparas säkert i systemet. Du kan när som helst radera ditt konto 
                och all associerad data via din profil.
              </small>
            </label>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading || !formData.consentGiven}
          style={{
            width: '100%',
            padding: '12px',
            backgroundColor: loading || !formData.consentGiven ? '#ccc' : '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading || !formData.consentGiven ? 'not-allowed' : 'pointer',
            fontSize: '16px'
          }}
        >
          {loading ? 'Registrerar...' : 'Registrera'}
        </button>
      </form>

      <p style={{ textAlign: 'center', marginTop: '20px' }}>
        Har du redan ett konto?{' '}
        <button
          onClick={onSwitchToLogin}
          style={{
            background: 'none',
            border: 'none',
            color: '#007bff',
            textDecoration: 'underline',
            cursor: 'pointer'
          }}
        >
          Logga in här
        </button>
      </p>
    </div>
  );
};

export default Register;