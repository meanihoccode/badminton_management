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
        // Ép backend trả về tiếng Việt
        config.headers['Accept-Language'] = 'vi';
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
    async (error) => {
        const originalRequest = error.config;

        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (refreshToken) {
                    // Gọi API refresh token
                    const response = await axios.post('http://localhost:8080/api/auth/refresh', {
                        refreshToken: refreshToken
                    });

                    // Lưu token mới
                    localStorage.setItem('token', response.data.token);
                    localStorage.setItem('refreshToken', response.data.refreshToken);

                    // Cập nhật header và gọi lại API cũ
                    originalRequest.headers['Authorization'] = `Bearer ${response.data.token}`;
                    return api(originalRequest);
                }
            } catch (refreshError) {
                // Nếu refresh token cũng lỗi (hết hạn 7 ngày) thì mới logout
                console.error("Refresh token failed", refreshError);
            }

            // Xoá trắng local storage và đẩy về trang đăng nhập nếu mọi cách đều thất bại
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('username');
            alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!');
            window.location.href = '/login';
        }
        
        return Promise.reject(error);
    }
);

export default api;