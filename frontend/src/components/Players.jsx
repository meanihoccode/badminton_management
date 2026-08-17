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
    const [newRole, setNewRole] = useState('MEMBER');

    // Edit member state
    const [editingUserId, setEditingUserId] = useState(null);
    const [editFullName, setEditFullName] = useState('');
    const [editEmail, setEditEmail] = useState('');
    const [editRacket, setEditRacket] = useState('');

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
                racketModel: newRacket,
                role: newRole
            });
            alert('Thêm thành viên mới thành công!');
            setNewUsername('');
            setNewPassword('');
            setNewFullName('');
            setNewEmail('');
            setNewRacket('');
            setNewRole('MEMBER');
            fetchUsers();
        } catch (error) {
            console.error(error);
            alert('Lỗi: Tên đăng nhập có thể đã tồn tại hoặc thiếu thông tin.');
        }
    };

    const handleDeleteUser = async (id) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa thành viên này? Toàn bộ dữ liệu của họ sẽ bị xóa.")) return;
        try {
            await api.delete(`/api/users/${id}`);
            alert('Xóa thành viên thành công!');
            fetchUsers();
        } catch (error) {
            console.error(error);
            const errorMsg = error.response?.data || "Không thể xóa thành viên này";
            alert(errorMsg);
        }
    };

    const startEditing = (user) => {
        setEditingUserId(user.id);
        setEditFullName(user.fullName);
        setEditEmail(user.email || '');
        setEditRacket(user.racketModel || '');
    };

    const handleUpdateUser = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/api/users/${editingUserId}`, {
                fullName: editFullName,
                email: editEmail,
                racketModel: editRacket
            });
            alert('Cập nhật thông tin thành công!');
            setEditingUserId(null);
            fetchUsers();
        } catch (error) {
            console.error(error);
            alert('Lỗi: Không thể cập nhật thông tin.');
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
                            <div key={u.id} className="glass-card" style={{ padding: '15px' }}>
                                {editingUserId === u.id ? (
                                    <form onSubmit={handleUpdateUser}>
                                        <div className="form-group mb-2">
                                            <input type="text" className="form-input" value={editFullName} onChange={e => setEditFullName(e.target.value)} placeholder="Họ và tên" required />
                                        </div>
                                        <div className="form-group mb-2">
                                            <input type="email" className="form-input" value={editEmail} onChange={e => setEditEmail(e.target.value)} placeholder="Email" required />
                                        </div>
                                        <div className="form-group mb-2">
                                            <input type="text" className="form-input" value={editRacket} onChange={e => setEditRacket(e.target.value)} placeholder="Dòng vợt" />
                                        </div>
                                        <div style={{ display: 'flex', gap: '10px' }}>
                                            <button type="submit" className="btn btn-success" style={{ padding: '6px 12px', backgroundColor: 'var(--secondary-color)', color: 'white', border: 'none', borderRadius: '4px' }}>Lưu</button>
                                            <button type="button" className="btn btn-secondary" onClick={() => setEditingUserId(null)} style={{ padding: '6px 12px', border: 'none', borderRadius: '4px' }}>Hủy</button>
                                        </div>
                                    </form>
                                ) : (
                                    <div className="flex-between">
                                        <div>
                                            <div className="fw-bold">{u.fullName}</div>
                                            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Vợt: {u.racketModel || 'Chưa cập nhật'}</div>
                                            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Email: {u.email || 'Chưa cập nhật'}</div>
                                        </div>
                                        <div style={{ textAlign: 'right' }}>
                                            {role === 'ROLE_ADMIN' && (
                                                <div className={u.balance < 0 ? 'text-danger fw-bold' : 'text-success fw-bold'} style={{ fontSize: '1.2rem', marginBottom: '8px' }}>
                                                    {u.balance} VNĐ
                                                </div>
                                            )}
                                            {role === 'ROLE_ADMIN' && (
                                                <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
                                                    <button onClick={() => startEditing(u)} style={{ padding: '4px 8px', fontSize: '0.8rem', backgroundColor: '#eab308', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Sửa</button>
                                                    <button onClick={() => handleDeleteUser(u.id)} style={{ padding: '4px 8px', fontSize: '0.8rem', backgroundColor: 'var(--danger-color)', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Xóa</button>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                )}
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
                                <div className="form-group">
                                    <label className="form-label">Vai trò</label>
                                    <select className="form-input" value={newRole} onChange={(e) => setNewRole(e.target.value)}>
                                        <option value="MEMBER">MEMBER</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
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

