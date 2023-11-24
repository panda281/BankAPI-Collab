import React, { useState } from 'react';
import ReactDOM from "react-dom/client";
import "../css/index.css";
import Logo from "../assets/img/logo.png";
import { useAuth } from '../context/AuthContext';

export function CustomerPage() {


    const { currentUser } = useAuth();
    const [responseTitle, setResponseTitle]  = useState('')
    const [inputValues, setInputValues] = useState({
        deposit: '',
        withdraw: '',
        transferId: '',
        transferAmount: '',
        airtime: '',
        otp: '',
        phoneNumber: '',
      });
  
    const [response, setResponse] = useState(null);

    const handleDeposit = () => {
        const apiUrl = `/api/v1/mobile/deposit`;
        const inputValue = inputValues.withdraw;
        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            body: JSON.stringify({ 
              login: currentUser.login,
              pin: currentUser.pin,
              amount: inputValue }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response.json())
            .then((data) => {
              setResponseTitle("OTP")
              setResponse(data);
            })
            .catch((error) => {
              
              console.error(`Error fetching data:`, error);
            });
      };
    
      const handleWithdraw = () => {
        const apiUrl = `/api/v1/mobile/withdraw`;
        const inputValue = inputValues.withdraw;

        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            body: JSON.stringify({ 
              login: currentUser.login,
              pin: currentUser.pin,
              amount: inputValue }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response.json())
            .then((data) => {
              setResponseTitle("OTP");
              setResponse(data);
            })
            .catch((error) => {
              
              console.error(`Error fetching data:`, error);
            });
      };

    
      const handleTransfer = () => {
        const apiUrl = `/api/v1/mobile/${inputValues.transferId}/transfer`;
        const inputValue = inputValues.transferAmount;

        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            body: JSON.stringify({
              login: currentUser.login,
              pin: currentUser.pin,
              amount: inputValue
            }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response)
            .then((data) => {
              setResponseTitle("Success");
              setResponse(null)
            })
            .catch((error) => {
              
              console.error(`Error fetching data:`, error);
            });
      };
    
      const handleAirtime = () => {
        const apiUrl = `api/v1/mobile/airtime`;
        const inputValue = inputValues.airtime;
        
        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            
            body: JSON.stringify({
              login: currentUser.login,
              pin: currentUser.pin,
              amount: inputValue }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response.json())
            .then((data) => {
              
              setResponseTitle("Top-Up Number");
              setResponse(data.senum);
              console.log(data);
            })
            .catch((error) => {
              
              console.error(`Error fetching data:`, error);
            });
      };

      const handleViewOtp = () => {
        const apiUrl = `api/v1/mobile/merchant`;
        
        
        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            
            body: JSON.stringify({
              login: currentUser.login,
              pin: currentUser.pin,
              otp: inputValues.otp,
              accountNumber: inputValues.accountNumber
             }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response.json())
            .then((data) => {
              
              setResponseTitle("Transaction Details");
              setResponse(data);
              
            })
            .catch((error) => {
              
              console.error(`Error fetching data for:`, error);
            });
      };

      const handleApproveOtp = () => {
        const apiUrl = `api/v1/mobile/merchant/approve`;
        
        
        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            
            body: JSON.stringify({
              login: currentUser.login,
              pin: currentUser.pin,
              otp: inputValues.otp,
              accountNumber: inputValues.accountNumber
             }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response.json())
            .then((data) => {
              
              setResponseTitle("Transaction Approved");
              setResponse(null);
              
            })
            .catch((error) => {
              
              console.error(`Error fetching data:`, error);
            });
      };
    
    const handleInputChange = (event, action) => {
        const { value } = event.target;
        setInputValues((prevInputValues) => ({
          ...prevInputValues,
          [action]: value,
        }));
      };
        return (
            <div className="app">
              <header className="app-header">
                    <img src={Logo} className="app-logo" alt="Bank logo" />
                    <h1>Gebeya Bank</h1>
                </header>
              {currentUser.profile == "DEFAULT" && (
                <div>
                
                
                <div className="app-content">
                    <input
                        type="text"
                        value={inputValues.deposit}
                        onChange={(e) => handleInputChange(e, 'deposit')}
                    />
                    <button className="action-button" onClick={handleDeposit}>
                        Deposit Money
                    </button>

                    <input
                        type="text"
                        value={inputValues.withdraw}
                        onChange={(e) => handleInputChange(e, 'withdraw')}
                    />
                    <button className="action-button" onClick={handleWithdraw}>
                        Withdraw Money
                    </button>

                    <input
                        type="text"
                        value={inputValues.transferId}
                        onChange={(e) => handleInputChange(e, 'transferId')}
                    />
                    <input
                        type="text"
                        value={inputValues.transferAmount}
                        onChange={(e) => handleInputChange(e, 'transferAmount')}
                    />
                    <button className="action-button" onClick={handleTransfer}>
                        Transfer Funds
                    </button>

                    <input
                        type="text"
                        value={inputValues.airtime}
                        onChange={(e) => handleInputChange(e, 'airtime')}
                    />
                    <button className="action-button" onClick={handleAirtime}>
                        Buy Airtime
                    </button>

                    
                    </div>
                  
                
                </div>
                )}

                {currentUser.profile == "MERCHANT" && (
                  <div>
                    <div className="app-content">

                    OTP <input
                        type="text"
                        value={inputValues.otp}
                        onChange={(e) => handleInputChange(e, 'otp')}
                    />
                    Account Number <input
                        type="text"
                        value={inputValues.accountNumber}
                        onChange={(e) => handleInputChange(e, 'accountNumber')}
                    />
                    <button className="action-button" onClick={handleViewOtp}>
                        View Request
                    </button>
                    <button className="action-button" onClick={handleApproveOtp}>
                        Approve Request
                    </button>
                    </div>
                  </div>
                  
                    

                )}
                {responseTitle && (
                        <div className="response">
                        <h1>{responseTitle}</h1>
                        {response && (
                        <pre>{JSON.stringify(response, null, 2)}</pre>
                        )}
                        </div>
                    )}
                <footer className="app-footer">
                    <p>Legal Information</p>
                    <p>Privacy Policy</p>
                    <p>User Settings</p>
                </footer>
            </div>
        );
}