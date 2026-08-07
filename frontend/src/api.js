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

export default api;