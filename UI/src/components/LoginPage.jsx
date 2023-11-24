// LoginPage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

import '../css/LoginPage.css'; // Adjust the path as needed


export const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { currentUser, loginUser } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (event) => {
    event.preventDefault(); // Prevent the default form submit behavior
    // Here you would replace this with a call to your authentication API
    try {
      // Simulating API call
      const userCredentials = { username, password };
      loginUser(userCredentials, (error, data) => {
        if (error) {
          // Handle error
          console.log(error);
          return;
        }
        // Replace with actual success condition
          console.log(currentUser);
          navigate('/');
      });
      
    } catch (error) {
      // Handle errors (e.g., network error, etc.)
    }
  };

  return (
    <div className="login-container">
      <h2>Login Page</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label htmlFor="username">Login:</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="password">Pin:</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Log in</button>
      </form>
    </div>
  );
};
