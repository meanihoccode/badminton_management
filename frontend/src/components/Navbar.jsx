import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Navbar = () => {
    const navigate = useNavigate();
    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="nav-content">
                <Link to="/" className="nav-brand">
                    BadmintonManager
                </Link>
                <div className="nav-links">
                    <Link to="/players">Thành viên</Link>
                    <Link to="/sessions">Buổi đánh</Link>
                    <Link to="/matches">Ghi kết quả</Link>
                    <button onClick={handleLogout} className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '0.9rem' }}>
                        Đăng xuất
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
