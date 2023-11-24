import React, { useState } from 'react';
import ReactDOM from "react-dom/client";
import "../css/index.css";
import Logo from "../assets/img/logo.png";
import { useAuth } from '../context/AuthContext';

export function MerchantPage() {

    const { currentUser } = useAuth();
    const [responseTitle, setResponseTitle]  = useState('')
    const [inputValues, setInputValues] = useState({
        deposit: '',
        withdraw: '',
        transfer: '',
        airtime: '',
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
              console.error(`Error fetching data for ${action}:`, error);
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
              console.error(`Error fetching data for ${action}:`, error);
            });
      };

    
      const handleTransfer = () => {
        const apiUrl = `https://api.example.com/transfer`;
        const inputValue = inputValues.transfer;

        fetch(apiUrl, {
            method: 'POST', // or 'GET' or 'PUT' depending on your API
            body: JSON.stringify({ value: inputValue }),
            headers: {
              'Content-Type': 'application/json',
            },
          })
            .then((response) => response.json())
            .then((data) => {
              setResponse(data);
            })
            .catch((error) => {
              console.error(`Error fetching data for ${action}:`, error);
            });
      };
    
      const handleAirtime = () => {
        const apiUrl = `api/v1/mobile/airtime`;
        const inputValue = inputValues.airtime;
        console.log("AIRTIME")
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
              setResponse(data.secnum);
            })
            .catch((error) => {
              console.error(`Error fetching data for ${action}:`, error);
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
                <nav className="app-nav">
                    <button>Home</button>
                    
                    <button>Transactions</button>
                    <button>Support</button>
                </nav>
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
                        value={inputValues.transfer}
                        onChange={(e) => handleInputChange(e, 'transfer')}
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

                    {response && (
                        <div className="response">
                        <h2>{responseTitle}</h2>
                        <pre>{JSON.stringify(response, null, 2)}</pre>
                        </div>
                    )}
                    </div>
                <footer className="app-footer">
                    <p>Legal Information</p>
                    <p>Privacy Policy</p>
                    <p>User Settings</p>
                </footer>
            </div>
        );
}