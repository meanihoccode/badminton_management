import React, { useState, useEffect } from 'react';
import api from '../api';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const navigate = useNavigate();
    const role = localStorage.getItem('role');
    const [balance, setBalance] = useState(null);

    useEffect(() => {
        const fetchMe = async () => {
            try {
                const res = await api.get('/api/users/me');
                setBalance(res.data.balance);
            } catch (err) {
                console.error('Lỗi khi lấy thông tin', err);
            }
        };
        fetchMe();
    }, []);

    return (
        <div className="animate-fade-in">
            <h1 className="page-title text-center">Chào mừng đến với Sân Cầu Lông</h1>
            <p className="text-center text-muted mb-4">Chọn một tác vụ bên dưới để bắt đầu</p>

            <div className="grid-2 mt-8">

                <div className="glass-card">
                    <h3 style={{ color: 'var(--success-color, #10b981)', marginBottom: '10px' }}>Số dư hiện tại</h3>
                    <p style={{ color: balance < 0 ? 'var(--danger-color)' : 'var(--success-color)', fontSize: '1.5rem', fontWeight: 'bold' }}>
                        {balance !== null ? balance.toLocaleString() + ' VNĐ' : 'Đang tải...'}
                    </p>
                </div>

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

