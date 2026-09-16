import React, { useState, useEffect } from 'react';
import api from '../api';

const Session = () => {
    // Form Tạo buổi đánh
    const [courtName, setCourtName] = useState('');
    const [sessionDate, setSessionDate] = useState('');
    
    // Form Chốt sổ
    const [sessionId, setSessionId] = useState('');
    const [courtFee, setCourtFee] = useState('');
    const [shuttlecockFee, setShuttlecockFee] = useState('');

        const [inProgressSessions, setInProgressSessions] = useState([]);

    const fetchSessions = async () => {
        try {
            const res = await api.get('/api/sessions/in-progress');
            setInProgressSessions(res.data);
            if (res.data.length > 0) {
                setSessionId(res.data[0].id);
            }
        } catch (err) {
            console.error('Lỗi khi tải buổi đánh:', err);
        }
    };

    useEffect(() => {
        fetchSessions();
    }, []);

    const role = localStorage.getItem('role');

    const handleCreateSession = async (e) => {
        e.preventDefault();
        try {
            const res = await api.post('/api/sessions', { courtName, sessionDate });
            alert(`Tạo buổi đánh thành công! ID của buổi đánh là: ${res.data.id}`);
            setCourtName('');
            setSessionDate('');
            fetchSessions();
        } catch (error) {
            console.error(error);
            alert('Lỗi tạo buổi đánh');
        }
    };

    const handleCloseSession = async (e) => {
        e.preventDefault();
        try {
            await api.post(`/api/sessions/${sessionId}/close?courtFee=${courtFee}&shuttlecockFee=${shuttlecockFee}`);
            alert('Chốt sổ thành công! Hệ thống đã chia tiền cho các thành viên.');
            setSessionId('');
            setCourtFee('');
            setShuttlecockFee('');
            fetchSessions();
        } catch (error) {
            console.error(error);
            alert('Lỗi chốt sổ. Có thể bạn không phải Admin hoặc ID không hợp lệ.');
        }
    };

    return (
        <div className="animate-fade-in">
            <h2 className="page-title">Quản Lý Buổi Đánh</h2>
            <div className="grid-2">
                <div className="glass-panel">
                    <h3 className="mb-4">Tạo buổi đánh mới</h3>
                    <form onSubmit={handleCreateSession}>
                        <div className="form-group">
                            <label className="form-label">Tên sân</label>
                            <input 
                                type="text" 
                                className="form-input" 
                                value={courtName} 
                                onChange={e => setCourtName(e.target.value)} 
                                required 
                                placeholder="VD: Sân số 1, Nhà thi đấu ABC"
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Ngày đánh</label>
                            <input 
                                type="date" 
                                className="form-input" 
                                value={sessionDate} 
                                onChange={e => setSessionDate(e.target.value)} 
                                required 
                            />
                        </div>
                        <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Tạo buổi đánh</button>
                    </form>
                </div>

                {role === 'ROLE_ADMIN' && (
                    <div className="glass-panel">
                        <h3 className="mb-4 text-danger">Chốt sổ buổi đánh (Admin)</h3>
                    <form onSubmit={handleCloseSession}>
                        <div className="form-group">
                            <label className="form-label">ID Buổi Đánh</label>
                                                        <select 
                                className="form-input" 
                                value={sessionId} 
                                onChange={e => setSessionId(e.target.value)} 
                                required
                            >
                                <option value="" disabled>-- Chọn buổi đánh --</option>
                                {inProgressSessions.map(s => (
                                    <option key={s.id} value={s.id}>
                                        #{s.id} - {s.courtName} ({s.sessionDate})
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label">Tiền Sân (VNĐ)</label>
                            <input 
                                type="number" 
                                className="form-input" 
                                value={courtFee} 
                                onChange={e => setCourtFee(e.target.value)} 
                                required 
                                placeholder="VD: 300000"
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Tiền Cầu (VNĐ)</label>
                            <input 
                                type="number" 
                                className="form-input" 
                                value={shuttlecockFee} 
                                onChange={e => setShuttlecockFee(e.target.value)} 
                                required 
                                placeholder="VD: 120000"
                            />
                        </div>
                        <button type="submit" className="btn btn-danger" style={{ width: '100%' }}>Chốt sổ & Chia tiền</button>
                    </form>
                </div>
                )}
            </div>
        </div>
    );
};

export default Session;
