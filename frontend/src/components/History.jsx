import React, { useState, useEffect } from 'react';
import api from '../api';

const History = () => {
    const [transactions, setTransactions] = useState([]);
    const [matches, setMatches] = useState([]);
    const [activeTab, setActiveTab] = useState('transactions'); // 'transactions' or 'matches'

    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const [txRes, matchRes] = await Promise.all([
                    api.get('/api/users/me/transactions'),
                    api.get('/api/users/me/matches')
                ]);
                setTransactions(txRes.data);
                setMatches([...matchRes.data].reverse());
            } catch (error) {
                console.error("Lỗi tải lịch sử:", error);
            }
        };
        fetchHistory();
    }, []);

    return (
        <div className="animate-fade-in">
            <h2 className="page-title text-center">Lịch Sử Của Tôi</h2>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', marginBottom: '30px' }}>
                <button 
                    className={`btn ${activeTab === 'transactions' ? 'btn-primary' : ''}`} 
                    style={{ border: '1px solid var(--primary-color)', color: activeTab === 'transactions' ? 'white' : 'var(--primary-color)' }}
                    onClick={() => setActiveTab('transactions')}
                >
                    Lịch sử giao dịch
                </button>
                <button 
                    className={`btn ${activeTab === 'matches' ? 'btn-secondary' : ''}`} 
                    style={{ border: '1px solid var(--secondary-color)', color: activeTab === 'matches' ? 'white' : 'var(--secondary-color)' }}
                    onClick={() => setActiveTab('matches')}
                >
                    Lịch sử trận đấu
                </button>
            </div>

            {activeTab === 'transactions' && (
                <div className="glass-panel">
                    <h3 className="mb-4">Dòng tiền của bạn</h3>
                    <div style={{ overflowX: 'auto' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                            <thead>
                                <tr style={{ borderBottom: '2px solid var(--panel-border)' }}>
                                    <th style={{ padding: '12px' }}>Thời gian</th>
                                    <th style={{ padding: '12px' }}>Loại</th>
                                    <th style={{ padding: '12px' }}>Số tiền (VNĐ)</th>
                                    <th style={{ padding: '12px' }}>Ghi chú</th>
                                </tr>
                            </thead>
                            <tbody>
                                {transactions.length === 0 ? (
                                    <tr><td colSpan="4" className="text-center" style={{ padding: '20px' }}>Chưa có giao dịch nào.</td></tr>
                                ) : (
                                    transactions.map(tx => (
                                        <tr key={tx.id} style={{ borderBottom: '1px solid var(--panel-border)' }}>
                                            <td style={{ padding: '12px' }}>{new Date(tx.createdAt).toLocaleString('vi-VN')}</td>
                                            <td style={{ padding: '12px' }}>
                                                {tx.description.startsWith("Trừ") ? (
                                                    <span className="text-danger fw-bold">Bị trừ</span>
                                                ) : (
                                                    <span className="text-success fw-bold">Đã nạp</span>
                                                )}
                                            </td>
                                            <td style={{ padding: '12px' }}>{tx.amount}</td>
                                            <td style={{ padding: '12px' }}>{tx.description}</td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {activeTab === 'matches' && (
                <div className="glass-panel">
                    <h3 className="mb-4">Các trận đấu đã tham gia</h3>
                    <div className="grid-2">
                        {matches.length === 0 ? (
                            <div className="text-muted">Bạn chưa tham gia trận đấu nào.</div>
                        ) : (
                            matches.map(m => (
                                <div key={m.matchId} className="glass-card" style={{ borderTop: `4px solid ${m.feeCalculated > 0 ? 'var(--secondary-color)' : 'var(--danger-color)'}` }}>
                                    <div className="flex-between mb-4">
                                        <strong>{m.courtName}</strong>
                                        <span className="text-muted" style={{ fontSize: '0.85rem' }}>{m.sessionDate}</span>
                                    </div>
                                    <div className="text-center mb-4">
                                        <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
                                            {m.team === 'A'
                                                ? m.teamAScore + ' - ' + m.teamBScore
                                                : m.teamBScore + ' - ' + m.teamAScore
                                            }
                                        </div>
                                    </div>
                                    <div className="flex-between" style={{ borderTop: '1px solid var(--panel-border)', paddingTop: '10px' }}>
                                        <span>Hiệu số: <strong>{m.pointDifference}</strong></span>
                                        {m.feeCalculated > 0 ? (
                                            <span className="text-success fw-bold">+5000 VNĐ</span>
                                        ) : (
                                            <span className="text-danger fw-bold">{m.feeCalculated} VNĐ</span>
                                        )}
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default History;
