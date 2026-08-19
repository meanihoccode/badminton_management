import React, { useState, useEffect } from 'react';
import api from '../api';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const navigate = useNavigate();
    const role = localStorage.getItem('role');
    const [balance, setBalance] = useState(null);
    const [weather, setWeather] = useState(null);
    const [leaderboard, setLeaderboard] = useState([]);

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
                // Because Weather API might not need auth, but api wrapper adds token.
                const res = await api.get('/api/weather');
                if (res.data && res.data.current_weather) {
                    setWeather(res.data.current_weather);
                } else if (typeof res.data === 'string') {
                    // Fallback in case it was escaped
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

    const getWeatherIcon = (code) => {
        console.log(weather);
        if (code === 0) return '☀️ Trời trong xanh';
        if (code === 1 || code === 2 || code === 3) return '⛅ Có mây';
        if (code >= 51 && code <= 67) return '🌧️ Có mưa';
        if (code >= 95) return '⛈️ Sấm chớp';
        return '🌥️ Âm u';
    };

    return (
        <div className="animate-fade-in">
            <h1 className="page-title text-center">Chào mừng đến với Sân Cầu Lông</h1>
            
            <div className="grid-2 mt-8">
                {/* Balance Card */}
                <div className="glass-card">
                    <h3 style={{ color: 'var(--success-color, #10b981)', marginBottom: '10px' }}>Số dư hiện tại</h3>
                    <p style={{ color: balance < 0 ? 'var(--danger-color)' : 'var(--success-color)', fontSize: '1.5rem', fontWeight: 'bold' }}>
                        {balance !== null ? balance.toLocaleString() + ' VND' : 'Đang tải...'}
                    </p>
                </div>

                {/* Weather Widget */}
                <div className="glass-card" style={{ background: 'linear-gradient(135deg, #e0f7fa 0%, #80deea 100%)' }}>
                    <h3 style={{ color: '#006064', marginBottom: '10px' }}>🌤️ Thời tiết Hà Nội</h3>
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
                <h3 style={{ color: 'var(--primary-color)', marginBottom: '15px' }}>Bảng Xếp Hạng Năng Nổ (Top 5)</h3>
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
        </div>
    );
};

export default Dashboard;