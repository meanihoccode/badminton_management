import React, { useState, useEffect } from 'react';
import api from '../api';

const Players = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [payAmount, setPayAmount] = useState('');
    const [selectedUserId, setSelectedUserId] = useState(null);
    const role = localStorage.getItem('role');

    // New member state
    const [newUsername, setNewUsername] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [newFullName, setNewFullName] = useState('');
    const [newEmail, setNewEmail] = useState('');
    const [newRacket, setNewRacket] = useState('');

    const fetchUsers = async () => {
        try {
            const res = await api.get('/api/users');
            setUsers(res.data);
        } catch (error) {
            console.error("Lỗi tải danh sách người chơi:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handlePayDebt = async (e) => {
        e.preventDefault();
        if (!selectedUserId || !payAmount) return;

        try {
            await api.post(`/api/users/${selectedUserId}/pay?amount=${payAmount}`);
            alert('Thanh toán / Nạp quỹ thành công!');
            setPayAmount('');
            setSelectedUserId(null);
            fetchUsers(); // Tải lại danh sách
        } catch (error) {
            console.error(error);
            alert('Lỗi: Bạn có thể không có quyền Admin hoặc số tiền không hợp lệ.');
        }
    };

    const handleCreateUser = async (e) => {
        e.preventDefault();
        try {
            await api.post('/api/users', {
                username: newUsername,
                password: newPassword,
                fullName: newFullName,
                email: newEmail,
                racketModel: newRacket
            });
            alert('Thêm thành viên mới thành công!');
            setNewUsername('');
            setNewPassword('');
            setNewFullName('');
            setNewEmail('');
            setNewRacket('');
            fetchUsers();
        } catch (error) {
            console.error(error);
            alert('Lỗi: Tên đăng nhập có thể đã tồn tại hoặc thiếu thông tin.');
        }
    };

    if (loading) return <div className="text-center mt-8">Đang tải dữ liệu...</div>;

    return (
        <div className="animate-fade-in">
            <h2 className="page-title">Danh Sách Thành Viên & Quỹ</h2>

            <div className="grid-2">
                {/* Cột trái: Danh sách */}
                <div className="glass-panel">
                    <h3 className="mb-4">Tất cả người chơi</h3>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                        {users.map(u => (
                            <div key={u.id} className="flex-between glass-card" style={{ padding: '15px' }}>
                                <div>
                                    <div className="fw-bold">{u.fullName}</div>
                                    <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Vợt: {u.racketModel || 'Chưa cập nhật'}</div>
                                </div>
                                <div className={u.balance < 0 ? 'text-danger fw-bold' : 'text-success fw-bold'} style={{ fontSize: '1.2rem' }}>
                                    {u.balance} VNĐ
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Cột phải: Các chức năng Admin */}
                {role === 'ROLE_ADMIN' && (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                        {/* Form Thêm Thành Viên */}
                        <div className="glass-panel">
                            <h3 className="mb-4 text-primary">Thêm Thành Viên Mới</h3>
                            <form onSubmit={handleCreateUser}>
                                <div className="form-group">
                                    <label className="form-label">Tên đăng nhập</label>
                                    <input type="text" className="form-input" value={newUsername} onChange={(e) => setNewUsername(e.target.value)} required />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Mật khẩu</label>
                                    <input type="password" className="form-input" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Họ và Tên</label>
                                    <input type="text" className="form-input" value={newFullName} onChange={(e) => setNewFullName(e.target.value)} required />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Email (Dùng để nhắc nợ)</label>
                                    <input type="email" className="form-input" value={newEmail} onChange={(e) => setNewEmail(e.target.value)} required />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Dòng Vợt (Tùy chọn)</label>
                                    <input type="text" className="form-input" value={newRacket} onChange={(e) => setNewRacket(e.target.value)} />
                                </div>
                                <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Tạo Tài Khoản</button>
                            </form>
                        </div>

                        {/* Form Thanh Toán */}
                        <div className="glass-panel">
                            <h3 className="mb-4 text-success">Thanh Toán Nợ / Nạp Quỹ</h3>
                        <form onSubmit={handlePayDebt}>
                        <div className="form-group">
                            <label className="form-label">Chọn người chơi</label>
                            <select 
                                className="form-input" 
                                value={selectedUserId || ''} 
                                onChange={(e) => setSelectedUserId(e.target.value)}
                                required
                            >
                                <option value="" disabled>-- Chọn một người chơi --</option>
                                {users.map(u => (
                                    <option key={u.id} value={u.id}>{u.fullName} (ID: {u.id})</option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label">Số tiền (VNĐ)</label>
                            <input 
                                type="number" 
                                className="form-input"
                                value={payAmount}
                                onChange={(e) => setPayAmount(e.target.value)}
                                min="1"
                                placeholder="VD: 50000"
                                required 
                            />
                        </div>
                        <button type="submit" className="btn btn-secondary" style={{ width: '100%' }}>Thực hiện giao dịch</button>
                        </form>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Players;
