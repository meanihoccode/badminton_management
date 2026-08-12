import React, { useState, useEffect } from 'react';
import api from '../api';

const Match = () => {
    const [users, setUsers] = useState([]);
    
    // Form state
    const [sessionId, setSessionId] = useState('');
    const [teamAScore, setTeamAScore] = useState('');
    const [teamBScore, setTeamBScore] = useState('');
    const [playerA1Id, setPlayerA1Id] = useState('');
    const [playerA2Id, setPlayerA2Id] = useState('');
    const [playerB1Id, setPlayerB1Id] = useState('');
    const [playerB2Id, setPlayerB2Id] = useState('');

    useEffect(() => {
        // Lấy danh sách user để đổ vào dropdown
        const fetchUsers = async () => {
            try {
                const res = await api.get('/api/users');
                setUsers(res.data);
            } catch (error) {
                console.error("Lỗi tải người chơi:", error);
            }
        };
        fetchUsers();
    }, []);

    const handleRecordMatch = async (e) => {
        e.preventDefault();
        
        // Validation cơ bản
        const ids = [playerA1Id, playerA2Id, playerB1Id, playerB2Id];
        const uniqueIds = new Set(ids);
        if (uniqueIds.size !== 4) {
            alert('Lỗi: Một người không thể chơi ở nhiều vị trí trong cùng 1 trận!');
            return;
        }

        try {
            const res = await api.post('/api/matches/record', {
                sessionId: parseInt(sessionId),
                teamAScore: parseInt(teamAScore),
                teamBScore: parseInt(teamBScore),
                playerA1Id: parseInt(playerA1Id),
                playerA2Id: parseInt(playerA2Id),
                playerB1Id: parseInt(playerB1Id),
                playerB2Id: parseInt(playerB2Id)
            });
            alert('Ghi nhận kết quả thành công!');
            // Reset điểm
            setTeamAScore('');
            setTeamBScore('');
            // Giữ nguyên Session ID và người chơi cho trận tiếp theo nếu cần
        } catch (error) {
            console.error(error);
            const errorMsg = error.response?.data || "Có lỗi xảy ra khi ghi nhận trận đấu";
            alert(errorMsg);
        }
    };

    const renderPlayerSelect = (value, setValue, label) => (
        <div className="form-group" style={{ marginBottom: '10px' }}>
            <label className="form-label" style={{ fontSize: '0.8rem' }}>{label}</label>
            <select 
                className="form-input" 
                value={value} 
                onChange={e => setValue(e.target.value)} 
                required
            >
                <option value="" disabled>-- Chọn --</option>
                {users.map(u => (
                    <option key={u.id} value={u.id}>{u.fullName}</option>
                ))}
            </select>
        </div>
    );

    return (
        <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
            <h2 className="page-title text-center">Ghi Nhận Kết Quả Trận Đấu</h2>
            
            <div className="glass-panel">
                <form onSubmit={handleRecordMatch}>
                    <div className="form-group mb-4">
                        <label className="form-label text-center">ID Buổi Đánh</label>
                        <input 
                            type="number" 
                            className="form-input text-center" 
                            style={{ maxWidth: '200px', margin: '0 auto', display: 'block' }}
                            value={sessionId} 
                            onChange={e => setSessionId(e.target.value)} 
                            required 
                            placeholder="Nhập ID buổi đánh..."
                        />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }} className="mt-8">
                        {/* TEAM A */}
                        <div className="glass-card" style={{ borderTop: '4px solid var(--primary-color)' }}>
                            <h3 className="text-center mb-4" style={{ color: 'var(--primary-color)' }}>Team A</h3>
                            
                            <div className="form-group mb-4">
                                <label className="form-label text-center text-primary">Điểm số</label>
                                <input 
                                    type="number" 
                                    className="form-input text-center fw-bold" 
                                    style={{ fontSize: '1.5rem', height: '60px' }}
                                    value={teamAScore} 
                                    onChange={e => setTeamAScore(e.target.value)} 
                                    required 
                                />
                            </div>

                            {renderPlayerSelect(playerA1Id, setPlayerA1Id, "Người chơi 1")}
                            {renderPlayerSelect(playerA2Id, setPlayerA2Id, "Người chơi 2")}
                        </div>

                        {/* TEAM B */}
                        <div className="glass-card" style={{ borderTop: '4px solid var(--danger-color)' }}>
                            <h3 className="text-center mb-4" style={{ color: 'var(--danger-color)' }}>Team B</h3>
                            
                            <div className="form-group mb-4">
                                <label className="form-label text-center text-danger">Điểm số</label>
                                <input 
                                    type="number" 
                                    className="form-input text-center fw-bold" 
                                    style={{ fontSize: '1.5rem', height: '60px' }}
                                    value={teamBScore} 
                                    onChange={e => setTeamBScore(e.target.value)} 
                                    required 
                                />
                            </div>

                            {renderPlayerSelect(playerB1Id, setPlayerB1Id, "Người chơi 1")}
                            {renderPlayerSelect(playerB2Id, setPlayerB2Id, "Người chơi 2")}
                        </div>
                    </div>

                    <div className="mt-8 text-center">
                        <button type="submit" className="btn btn-secondary" style={{ padding: '15px 40px', fontSize: '1.2rem' }}>
                            Xác nhận trận đấu
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Match;
