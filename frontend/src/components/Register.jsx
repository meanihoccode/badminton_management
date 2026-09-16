import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api';

const Register = () => {
    const [step, setStep] = useState(1);
    const [isLoading, setIsLoading] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });
    const navigate = useNavigate();

    // Form data
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        fullName: '',
        email: ''
    });
    const [otp, setOtp] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleInitRegister = async (e) => {
        e.preventDefault();
        setMessage({ text: '', type: '' });
        setIsLoading(true);

        try {
            const res = await api.post('/api/auth/register/init', formData);
            setMessage({ text: res.data.message || 'OTP đã được gửi đến email!', type: 'success' });
            setStep(2); // Chuyển sang bước OTP
        } catch (error) {
            let errorMsg = 'Đăng ký thất bại. Vui lòng thử lại.';
            if (error.response?.data) {
                if (error.response.data.message) {
                    errorMsg = error.response.data.message;
                } else if (typeof error.response.data === 'object') {
                    // Xử lý map lỗi validation của Spring Boot
                    const errors = Object.values(error.response.data);
                    if (errors.length > 0 && typeof errors[0] === 'string') {
                        errorMsg = errors.join(', ');
                    }
                }
            }
            setMessage({ text: errorMsg, type: 'danger' });
        } finally {
            setIsLoading(false);
        }
    };

    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setMessage({ text: '', type: '' });
        setIsLoading(true);

        try {
            const res = await api.post('/api/auth/register/verify', { 
                email: formData.email, 
                otp: otp 
            });
            setMessage({ text: 'Đăng ký thành công! Đang chuyển hướng...', type: 'success' });
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        } catch (error) {
            let errorMsg = 'Xác thực OTP thất bại.';
            if (error.response?.data?.message) errorMsg = error.response.data.message;
            setMessage({ text: errorMsg, type: 'danger' });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', padding: '20px' }}>
            <div className="glass-panel animate-fade-in" style={{ width: '100%', maxWidth: '450px' }}>
                <div className="text-center mb-4">
                    <h2 className="page-title" style={{ marginBottom: '10px' }}>Đăng Ký Tài Khoản</h2>
                    <p style={{ color: 'var(--text-muted)' }}>Tham gia sân cầu lông</p>
                </div>

                {message.text && (
                    <div className="glass-card mb-4" style={{ 
                        backgroundColor: message.type === 'success' ? 'rgba(34, 197, 94, 0.1)' : 'rgba(239, 68, 68, 0.1)', 
                        border: `1px solid var(--${message.type === 'success' ? 'success' : 'danger'}-color)`, 
                        padding: '10px' 
                    }}>
                        <p className={`text-${message.type === 'success' ? 'success' : 'danger'} text-center`} style={{ margin: 0, fontSize: '0.9rem' }}>
                            {message.text}
                        </p>
                    </div>
                )}

                {step === 1 ? (
                    <form onSubmit={handleInitRegister}>
                        <div className="form-group">
                            <label className="form-label">Tên đăng nhập (Username)</label>
                            <input type="text" name="username" className="form-input" required 
                                value={formData.username} onChange={handleChange} placeholder="Nhập tên đăng nhập..." />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Họ và tên</label>
                            <input type="text" name="fullName" className="form-input" required 
                                value={formData.fullName} onChange={handleChange} placeholder="Nhập họ và tên..." />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Email (Dùng để nhận OTP)</label>
                            <input type="email" name="email" className="form-input" required 
                                value={formData.email} onChange={handleChange} placeholder="example@gmail.com" />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Mật khẩu</label>
                            <input type="password" name="password" className="form-input" required 
                                value={formData.password} onChange={handleChange} placeholder="Nhập mật khẩu..." />
                        </div>
                        <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px' }} disabled={isLoading}>
                            {isLoading ? 'Đang xử lý...' : 'Đăng Ký'}
                        </button>
                        <div className="text-center" style={{ marginTop: '15px' }}>
                            <Link to="/login" style={{ color: 'var(--primary-color)', textDecoration: 'none', fontSize: '0.9rem' }}>Đã có tài khoản? Đăng nhập ngay</Link>
                        </div>
                    </form>
                ) : (
                    <form onSubmit={handleVerifyOtp}>
                        <p className="text-center" style={{ fontSize: '0.95rem', marginBottom: '20px' }}>
                            Mã OTP 6 số đã được gửi tới email <strong>{formData.email}</strong>. Vui lòng kiểm tra hộp thư (cả mục Spam).
                        </p>
                        <div className="form-group">
                            <label className="form-label text-center" style={{ display: 'block' }}>Nhập Mã OTP</label>
                            <input type="text" className="form-input text-center" required 
                                style={{ fontSize: '1.2rem', letterSpacing: '3px', fontWeight: 'bold' }}
                                maxLength="6"
                                value={otp} onChange={(e) => setOtp(e.target.value)} placeholder="000000" />
                        </div>
                        <button type="submit" className="btn btn-success" style={{ width: '100%', marginTop: '10px' }} disabled={isLoading}>
                            {isLoading ? 'Đang xác thực...' : 'Xác Thực & Hoàn Tất'}
                        </button>
                        <div className="text-center" style={{ marginTop: '15px' }}>
                            <button type="button" onClick={() => setStep(1)} className="btn" style={{ background: 'transparent', color: 'var(--text-muted)' }}>
                                Quay lại sửa thông tin
                            </button>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
};

export default Register;