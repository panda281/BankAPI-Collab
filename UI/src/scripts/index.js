import React from "react";
import ReactDOM from "react-dom/client";
import "../css/index.css";
import Logo from "../assets/img/logo.png";
import App from "./app";



// Inject the App component into the root div
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);