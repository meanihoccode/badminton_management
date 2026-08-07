import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setIsLoading(true);

        try {
            const response = await api.post('/api/auth/login', { username, password });
            localStorage.setItem('token', response.data.token);
            navigate('/');
        } catch (error) {
            console.error("Lỗi đăng nhập:", error);
            setErrorMessage('Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản hoặc mật khẩu.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', padding: '20px' }}>
            <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '400px' }}>
                <div className="text-center mb-4">
                    <h2 className="page-title" style={{ marginBottom: '10px' }}>Badminton Manager</h2>
                    <p style={{ color: 'var(--text-muted)' }}>Đăng nhập để vào sân</p>
                </div>

                {errorMessage && (
                    <div className="glass-card mb-4" style={{ backgroundColor: 'rgba(239, 68, 68, 0.1)', border: '1px solid var(--danger-color)', padding: '10px' }}>
                        <p className="text-danger text-center" style={{ margin: 0, fontSize: '0.9rem' }}>{errorMessage}</p>
                    </div>
                )}

                <form onSubmit={handleLogin}>
                    <div className="form-group">
                        <label className="form-label">Tên đăng nhập</label>
                        <input
                            type="text"
                            className="form-input"
                            placeholder="Nhập tên đăng nhập..."
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label className="form-label">Mật khẩu</label>
                        <input
                            type="password"
                            className="form-input"
                            placeholder="Nhập mật khẩu..."
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button 
                        type="submit" 
                        className="btn btn-primary" 
                        style={{ width: '100%', marginTop: '10px' }}
                        disabled={isLoading}
                    >
                        {isLoading ? 'Đang vào sân...' : 'Vào Sân'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;