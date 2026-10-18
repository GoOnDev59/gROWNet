import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './styles/app.css';
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx';
import { StudentProvider } from './context/StudentContext.jsx';
createRoot(document.getElementById('root')).render(
  <BrowserRouter><AuthProvider><StudentProvider><App /></StudentProvider></AuthProvider></BrowserRouter>
);
