// AuthContext.js
import React, { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export const useAuth = () => {
  return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  let response;
    let fetchSuccess = false;
    const loginUser = (userCredentials, callback) => {
        const apiUrl = `/api/v1/mobile/login`;
        console.log("Attempting to log in");
      
        fetch(apiUrl, {
          method: 'POST',
          body: JSON.stringify({ login: userCredentials.username, pin: userCredentials.password }),
          headers: {
            'Content-Type': 'application/json',
          },
        })
        .then((response) => {
          if (!response.ok) {
            // It's important to reject the promise here so it jumps to the catch block.
            return Promise.reject(`HTTP error! Status: ${response.status}`);
          }
          return response.json();
        })
        .then((data) => {
          // Assuming setCurrentUser is synchronous
          setCurrentUser({ login: data.login, pin: data.pin, profile: data.customerProfile });
          // Call the callback with no error and the data
          callback(null, data);
        })
        .catch((error) => {
          console.error(`Error fetching data for Login:`, error);
          // Call the callback with the error
          callback(error, null);
        });
      };

  const logoutUser = (callback) => {
    setCurrentUser(null);
    callback();
  };

  const authContextValue = {
    currentUser,
    loginUser,
    logoutUser
  };

  return (
    <AuthContext.Provider value={authContextValue}>
      {children}
    </AuthContext.Provider>
  );
};
