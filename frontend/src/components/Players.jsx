import React, { useState, useEffect } from 'react';
import api from '../api';

const Players = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [debouncedKeyword, setDebouncedKeyword] = useState('');
    const [payAmount, setPayAmount] = useState('');
    const [payNote, setPayNote] = useState('');
    const [showAddForm, setShowAddForm] = useState(false);
    const [showPayForm, setShowPayForm] = useState(false);
    const [qrCodeData, setQrCodeData] = useState(null);

    const handleGenerateQR = async () => {
        if (!payAmount) {
            alert('Vui lòng nhập số tiền!');
            return;
        }
        try {
            const res = await api.get('/api/payment/generate-qr?amount=' + payAmount + (payNote ? '&addInfo=' + encodeURIComponent(payNote) : ''));
            if (res.data && res.data.qrDataURL) {
                setQrCodeData(res.data.qrDataURL);
            }
        } catch (error) {
            console.error(error);
            alert('Lỗi tạo mã QR');
        }
    };
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

    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const renderPagination = (currentPage, totalPages, onPageChange) => {
        if (totalPages <= 1) return null;
        let pages = [];
        let startPage = Math.max(0, currentPage - 2);
        let endPage = Math.min(totalPages - 1, currentPage + 2);
        
        if (startPage > 0) pages.push(0);
        if (startPage > 1) pages.push(-1);

        for (let i = startPage; i <= endPage; i++) {
            pages.push(i);
        }

        if (endPage < totalPages - 2) pages.push(-1);
        if (endPage < totalPages - 1) pages.push(totalPages - 1);

        return (
            <div style={{ display: 'flex', justifyContent: 'center', gap: '5px', marginTop: '20px', flexWrap: 'wrap' }}>
                <button className="btn btn-secondary" disabled={currentPage === 0} onClick={() => onPageChange(currentPage - 1)}>&laquo;</button>
                {pages.map((p, idx) => p === -1 ? (
                    <span key={"dots-" + idx} style={{ padding: '5px 10px' }}>...</span>
                ) : (
                    <button 
                        key={p} 
                        className={currentPage === p ? 'btn btn-primary' : 'btn btn-secondary'} 
                        onClick={() => onPageChange(p)}
                        style={{ padding: '5px 10px', minWidth: '35px' }}
                    >
                        {p + 1}
                    </button>
                ))}
                <button className="btn btn-secondary" disabled={currentPage >= totalPages - 1} onClick={() => onPageChange(currentPage + 1)}>&raquo;</button>
            </div>
        );
    };

    const fetchUsers = async (page = 0, keyword = searchKeyword) => {
        try {
            const res = await api.get('/api/users?page=' + page + '&size=5' + (keyword ? '&keyword=' + encodeURIComponent(keyword) : ''));
            if (res.data.content) {
                setUsers(res.data.content);
                setCurrentPage(res.data.currentPage);
                setTotalPages(res.data.totalPages);
            } else {
                setUsers(res.data);
            }
        } catch (error) {
            console.error("Lỗi tải danh sách người chơi:", error);
        } finally {
            setLoading(false);
        }
    };

        useEffect(() => {
        const timerId = setTimeout(() => {
            setDebouncedKeyword(searchKeyword);
        }, 500);
        return () => clearTimeout(timerId);
    }, [searchKeyword]);

    useEffect(() => {
        setCurrentPage(0);
        fetchUsers(0, debouncedKeyword);
    }, [debouncedKeyword]);

    useEffect(() => {
        fetchUsers(currentPage, debouncedKeyword);
    }, [currentPage]);

    const handlePayDebt = async (e) => {
        e.preventDefault();
        if (!selectedUserId || !payAmount) return;

        try {
            const res = await api.post(`/api/users/` + selectedUserId + `/pay?amount=` + payAmount + (payNote ? `&note=` + encodeURIComponent(payNote) : ''));
            if (res.data && res.data.latestReceipt) {
                alert("Thanh toán thành công!\n\n" + res.data.latestReceipt);
            } else {
                alert('Thanh toán / Nạp quỹ thành công!');
            }
            setPayAmount('');
            setPayNote('');
            setSelectedUserId(null);
            setQrCodeData(null);
            fetchUsers(currentPage, searchKeyword); // Tải lại danh sách
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
            fetchUsers(currentPage);
        } catch (error) {
            console.error(error);
            let errorMsg = 'Lỗi: Tên đăng nhập có thể đã tồn tại hoặc thiếu thông tin.';
            if (error.response?.data) {
                if (typeof error.response.data === 'string') {
                    errorMsg = error.response.data;
                } else if (typeof error.response.data === 'object') {
                    errorMsg = Object.values(error.response.data).join('\n');
                }
            }
            alert(errorMsg);
        }
    };

    const handleDeleteUser = async (id) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa thành viên này? Toàn bộ dữ liệu của họ sẽ bị xóa.")) return;
        try {
            await api.delete(`/api/users/${id}`);
            alert('Xóa thành viên thành công!');
            fetchUsers(currentPage);
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
            fetchUsers(currentPage);
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
                    <div style={{ marginBottom: '15px' }}>
                        <input 
                            type="text" 
                            className="form-input" 
                            placeholder="🔍 Tìm kiếm theo tên..." 
                            value={searchKeyword}
                            onChange={(e) => {
                                setSearchKeyword(e.target.value);
                            }}
                        />
                    </div>
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

                    {renderPagination(currentPage, totalPages, setCurrentPage)}
                </div>

                {/* Cột phải: Các chức năng Admin */}
                {role === 'ROLE_ADMIN' && (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                                                {/* Toggle Buttons */}
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)} style={{ flex: 1 }}>
                                {showAddForm ? 'Đóng form' : '+ Thêm Thành Viên'}
                            </button>
                            <button className="btn btn-secondary" onClick={() => setShowPayForm(!showPayForm)} style={{ flex: 1 }}>
                                {showPayForm ? 'Đóng form' : '$ Thanh toán nợ'}
                            </button>
                        </div>

                        {/* Form Thêm Thành Viên */}
                        {showAddForm && (
                        <div className="glass-panel">
                            <h3 className="mb-4 text-primary">Thêm Thành Viên Mới</h3>
                            <form onSubmit={handleCreateUser}>
                                <div className="form-group">
                                    <label className="form-label">Tên đăng nhập</label>
                                    <input type="text" className="form-input" minLength="3" value={newUsername} onChange={(e) => setNewUsername(e.target.value)} required />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Mật khẩu</label>
                                    <input type="password" className="form-input" minLength="6" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">Họ và Tên</label>
                                    <input type="text" className="form-input" minLength="2" value={newFullName} onChange={(e) => setNewFullName(e.target.value)} required />
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
                        )}

                        {showPayForm && (
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
                            <div className="form-group">
                                <label className="form-label">Ghi chú (Tùy chọn)</label>
                                <input 
                                    type="text" 
                                    className="form-input"
                                    value={payNote}
                                    onChange={(e) => setPayNote(e.target.value)}
                                    placeholder="VD: Đóng quỹ tháng 5"
                                />
                            </div>
                                                        <div className="form-group" style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" className="btn btn-secondary" style={{ flex: 1 }}>Thực hiện giao dịch</button>
                                <button type="button" className="btn btn-success" onClick={handleGenerateQR}>Tạo mã VietQR</button>
                            </div>
                            
                            {qrCodeData && (
                                <div style={{ marginTop: '20px', textAlign: 'center' }}>
                                    <p>Quét mã bằng App Ngân hàng</p>
                                    <img src={qrCodeData} alt="VietQR" style={{ width: '250px', border: '1px solid #ccc', borderRadius: '10px' }} />
                                </div>
                            )}
                        </form>
                        </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Players;

