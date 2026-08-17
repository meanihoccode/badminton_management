import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Navbar = () => {
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('username');
        navigate('/login');
    };

    const username = localStorage.getItem('username') || '';
    const role = localStorage.getItem('role') === 'ROLE_ADMIN' ? 'Admin' : 'Member';

    return (
        <nav className="navbar">
            <div className="nav-content">
                <Link to="/" className="nav-brand">
                    BadmintonManagement
                </Link>
                <div className="nav-links">
                    <Link to="/">Trang chủ</Link>
                    <Link to="/history">Lịch sử của tôi</Link>
                    {role === 'Admin' && (
                        <>
                            <Link to="/players">Thành viên</Link>
                            <Link to="/sessions">Buổi đánh</Link>
                            <Link to="/matches">Ghi kết quả</Link>
                        </>
                    )}
                    <span style={{ marginLeft: '10px', fontWeight: 'bold', color: 'var(--primary-color)' }}>
                        {username} ({role})
                    </span>
                    <button onClick={handleLogout} className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '0.9rem' }}>
                        Đăng xuất
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;

