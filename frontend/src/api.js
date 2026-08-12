import axios from 'axios';

// Tạo một instance của axios với URL mặc định trỏ về backend Spring Boot
const api = axios.create({
    baseURL: 'http://localhost:8080',
});

// Interceptor: Trước khi request bay đi, tự động nhét Token vào Header
api.interceptors.request.use(
    (config) => {
        // Lấy token từ LocalStorage
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor: Xử lý lỗi trả về từ Backend (ví dụ: Token hết hạn)
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            // Nếu lỗi 401 (Unauthorized) hoặc 403 (Forbidden)
            const token = localStorage.getItem('token');
            if (token) {
                // Chỉ thông báo nếu trước đó có token (nghĩa là token bị hết hạn)
                alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!');
                localStorage.removeItem('token');
                localStorage.removeItem('username');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default api;