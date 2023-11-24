import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom'; // Updated import for React Router v6
import { AuthProvider } from '../context/AuthContext';
import { PrivateRoute } from '../components/PrivateRoute.jsx';
import { LoginPage } from '../components/LoginPage.jsx';
import { CustomerPage } from '../components/CustomerPage.jsx';
import { MerchantPage } from '../components/MerchantPage.jsx';


const App = () => {
  return (
    <AuthProvider>
      <BrowserRouter> {/* Renamed from Router to BrowserRouter for clarity */}
        <Routes> {/* Updated from Switch to Routes for React Router v6 */}
          <Route path="/login" element={<LoginPage />} /> {/* Updated syntax for React Router v6 */}
          <Route 
            path="/" 
            element={
              <PrivateRoute>
        
           <CustomerPage />
            
              </PrivateRoute>
            } 
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
