import React, { useState, useEffect } from 'react';
import api from '../api';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const navigate = useNavigate();
    const role = localStorage.getItem('role');
    const [balance, setBalance] = useState(null);
    const [weather, setWeather] = useState(null);
    const [leaderboard, setLeaderboard] = useState([]);
    const [showTopup, setShowTopup] = useState(false);
    const [topupAmount, setTopupAmount] = useState('');
    const [initialBalance, setInitialBalance] = useState(null);
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [successAmount, setSuccessAmount] = useState(0);

    useEffect(() => {
        const fetchMe = async () => {
            try {
                const res = await api.get('/api/users/me');
                setBalance(res.data.balance);
            } catch (err) {
                console.error('Lỗi khi lấy thông tin', err);
            }
        };

        const fetchWeather = async () => {
            try {
                const res = await api.get('/api/weather');
                if (res.data && res.data.current_weather) {
                    setWeather(res.data.current_weather);
                } else if (typeof res.data === 'string') {
                    const parsed = JSON.parse(res.data);
                    setWeather(parsed.current_weather);
                }
            } catch (err) {
                console.error('Lỗi lấy thời tiết', err);
            }
        };

        const fetchLeaderboard = async () => {
            try {
                const res = await api.get('/api/users/leaderboard');
                setLeaderboard(res.data);
            } catch (err) {
                console.error('Lỗi lấy leaderboard', err);
            }
        };

        fetchMe();
        fetchWeather();
        fetchLeaderboard();
    }, []);

    // Kiem tra URL sau khi tu PayOS tra ve
    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        const status = urlParams.get('status');
        const cancel = urlParams.get('cancel');
        
        if (code === '00' && status === 'PAID' && cancel === 'false') {
            // Thanh toan thanh cong
            setShowSuccessModal(true);
            setSuccessAmount("thành công");
            
            // Xoa params khoi URL de ko bi hien lai khi F5
            window.history.replaceState(null, '', window.location.pathname);
        }
    }, []);

    const getWeatherIcon = (code) => {
        if (code === 0) return '☀️ Trời trong xanh';
        if (code === 1 || code === 2 || code === 3) return '🌤️ Có mây';
        if (code >= 51 && code <= 67) return '🌧️ Có mưa';
        if (code >= 95) return '⛈️ Sấm chớp';
        return '☁️ Âm u';
    };

    return (
        <div className="animate-fade-in">
            <h1 className="page-title text-center">Chào mừng đến với Sân Cầu Lông</h1>
            
            <div className="grid-2 mt-8">
                {/* Balance Card with Top-up Button */}
                <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div>
                            <h3 style={{ color: 'var(--success-color, #10b981)', marginBottom: '10px' }}>Số dư hiện tại</h3>
                            <p style={{ color: balance < 0 ? 'var(--danger-color)' : 'var(--success-color)', fontSize: '1.5rem', fontWeight: 'bold' }}>
                                {balance !== null ? balance.toLocaleString() + ' VND' : 'Đang tải...'}
                            </p>
                        </div>
                        <button onClick={() => { setShowTopup(true); setInitialBalance(balance); }} className="btn btn-success" style={{ padding: '10px 20px', borderRadius: '8px' }}>
                            + Nạp Tiền
                        </button>
                    </div>
                </div>

                {/* Weather Widget */}
                <div className="glass-card" style={{ background: 'linear-gradient(135deg, #e0f7fa 0%, #80deea 100%)' }}>
                    <h3 style={{ color: '#006064', marginBottom: '10px' }}>🌦️ Thời tiết Hà Nội</h3>
                    {weather ? (
                        <div>
                            <p style={{ fontSize: '1.2rem', fontWeight: 'bold', color: '#004d40' }}>
                                {getWeatherIcon(weather.weathercode)} - {weather.temperature}°C
                            </p>
                            <p style={{ fontSize: '0.9rem', color: '#004d40' }}>Gió: {weather.windspeed} km/h</p>
                        </div>
                    ) : (
                        <p style={{ color: '#006064' }}>Đang tải thời tiết...</p>
                    )}
                </div>
            </div>

            {/* Leaderboard */}
            <div className="mt-8">
                <h3 style={{ color: 'var(--primary-color)', marginBottom: '15px' }}>Bảng Xếp Hạng Siêng Năng (Top 5)</h3>
                <div className="glass-card">
                    {leaderboard.length > 0 ? (
                        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr style={{ borderBottom: '1px solid #ddd' }}>
                                    <th style={{ padding: '10px 0' }}>Hạng</th>
                                    <th>Người chơi</th>
                                    <th>Số trận tham gia</th>
                                </tr>
                            </thead>
                            <tbody>
                                {leaderboard.map((p, index) => (
                                    <tr key={index} style={{ borderBottom: '1px solid #f1f1f1' }}>
                                        <td style={{ padding: '10px 0', fontWeight: 'bold', color: index === 0 ? 'gold' : index === 1 ? 'silver' : index === 2 ? '#cd7f32' : 'inherit' }}>
                                            #{index + 1}
                                        </td>
                                        <td style={{ fontWeight: '500' }}>{p.username}</td>
                                        <td>{p.totalMatches} trận</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>Chưa có dữ liệu thi đấu.</p>
                    )}
                </div>
            </div>

            <div className="grid-2 mt-8">
                {role === 'ROLE_ADMIN' && (
                    <div className="glass-card" onClick={() => navigate('/players')} style={{ cursor: 'pointer' }}>
                        <h3 style={{ color: 'var(--primary-color)', marginBottom: '10px' }}>Quản lý thành viên</h3>
                        <p style={{ color: 'var(--text-muted)' }}>Xem danh sách người chơi, kiểm tra số dư nợ/tồn quỹ và thanh toán.</p>
                    </div>
                )}

                <div className="glass-card" onClick={() => navigate('/history')} style={{ cursor: 'pointer' }}>
                    <h3 style={{ color: 'var(--success-color, #10b981)', marginBottom: '10px' }}>Lịch sử của tôi</h3>
                    <p style={{ color: 'var(--text-muted)' }}>Xem lại lịch sử nạp tiền, trừ tiền và kết quả các trận đấu đã tham gia.</p>
                </div>

                {role === 'ROLE_ADMIN' && (
                    <>
                        <div className="glass-card" onClick={() => navigate('/sessions')} style={{ cursor: 'pointer' }}>
                            <h3 style={{ color: 'var(--secondary-color)', marginBottom: '10px' }}>Quản lý buổi đánh</h3>
                            <p style={{ color: 'var(--text-muted)' }}>Tạo buổi đánh mới hoặc chốt sổ chia tiền (chỉ dành cho Admin).</p>
                        </div>

                        <div className="glass-card" onClick={() => navigate('/matches')} style={{ cursor: 'pointer' }}>
                            <h3 style={{ color: 'var(--danger-color)', marginBottom: '10px' }}>Ghi kết quả trận đấu</h3>
                            <p style={{ color: 'var(--text-muted)' }}>Nhập điểm số, chọn người chơi để hệ thống tự động trừ tiền người thua.</p>
                        </div>
                    </>
                )}
            </div>

            {/* Top-up Modal */}
            {showTopup && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
                    <div className="glass-panel" style={{ width: '400px', backgroundColor: 'white', padding: '20px', borderRadius: '12px' }}>
                        <h3 className="text-center" style={{ color: 'var(--primary-color)', marginBottom: '20px' }}>Nạp Tiền Tự Động (VietQR)</h3>
                        
                        <div className="form-group">
                            <label className="form-label">Nhập số tiền muốn nạp (VND)</label>
                            <input 
                                type="number" 
                                className="form-input" 
                                value={topupAmount} 
                                onChange={(e) => setTopupAmount(e.target.value)} 
                                placeholder="Ví dụ: 100000"
                            />
                        </div>

                        {topupAmount > 0 && (
                            <div className="text-center mt-4">
                                <button 
                                    onClick={async () => {
                                        try {
                                            const res = await api.post('/api/payments/create', { 
                                                amount: topupAmount,
                                                returnUrl: window.location.href
                                            });
                                            window.location.href = res.data.checkoutUrl;
                                        } catch (err) {
                                            alert("Lỗi tạo mã thanh toán");
                                        }
                                    }} 
                                    className="btn btn-success" style={{ width: '100%', padding: '12px' }}
                                >
                                    Thanh Toán Qua PayOS
                                </button>
                            </div>
                        )}

                        <div className="text-center mt-3">
                            <button onClick={() => { setShowTopup(false); setTopupAmount(''); }} className="btn" style={{ background: '#ddd', color: '#333', width: '100%' }}>
                                Đóng
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Success Modal */}
            {showSuccessModal && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1100 }}>
                    <div className="glass-panel" style={{ width: '380px', backgroundColor: 'white', padding: '30px', borderRadius: '16px', textAlign: 'center', animation: 'fadeIn 0.3s ease-out' }}>
                        <div style={{ width: '70px', height: '70px', borderRadius: '50%', backgroundColor: '#d1fae5', display: 'flex', justifyContent: 'center', alignItems: 'center', margin: '0 auto 20px' }}>
                            <span style={{ color: '#10b981', fontSize: '40px' }}>✓</span>
                        </div>
                        <h2 style={{ color: '#10b981', marginBottom: '15px', fontWeight: 'bold' }}>Thành Công!</h2>
                        <p style={{ color: '#4b5563', fontSize: '1.1rem', marginBottom: '25px', lineHeight: '1.6' }}>
                            Hệ thống đã ghi nhận số dư của bạn được cập nhật. <br/>
                        </p>
                        <button onClick={() => {
                            setShowSuccessModal(false);
                            window.location.reload();
                        }} className="btn btn-success" style={{ width: '100%', padding: '12px', borderRadius: '10px', fontSize: '1.1rem', fontWeight: 'bold' }}>
                            Tuyệt vời
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Dashboard;