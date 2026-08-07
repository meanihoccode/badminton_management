import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/login';
import Navbar from './components/Navbar';
import Dashboard from './components/Dashboard';
import Players from './components/Players';
import Session from './components/Session';
import Match from './components/Match';

// Component bảo vệ Route
const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem('token');
    if (!token) {
        return <Navigate to="/login" replace />;
    }
    return (
        <>
            <Navbar />
            <div className="container">
                {children}
            </div>
        </>
    );
};

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<Login />} />

                <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
                <Route path="/players" element={<ProtectedRoute><Players /></ProtectedRoute>} />
                <Route path="/sessions" element={<ProtectedRoute><Session /></ProtectedRoute>} />
                <Route path="/matches" element={<ProtectedRoute><Match /></ProtectedRoute>} />
                
                {/* Fallback route */}
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </Router>
    );
}

export default App;