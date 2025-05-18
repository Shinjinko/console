import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AppHeader from './components/Header';
import DashboardPage from './pages/DashboardPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AccountPage from './pages/AccountPage';
import ConsolesPage from './pages/ConsolesPage';
import { AuthProvider } from './AuthContext';
import PrivateRoute from "./components/PrivateRoute";

function App() {
    return (
        <AuthProvider>
            <Router>
                <AppHeader />
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    {/* Защищенные роуты */}
                    <Route path="/" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
                    <Route path="/account" element={<PrivateRoute><AccountPage /></PrivateRoute>} />
                    <Route path="/consoles" element={<PrivateRoute><ConsolesPage /></PrivateRoute>} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;