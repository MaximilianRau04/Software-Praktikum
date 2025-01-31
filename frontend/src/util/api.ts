import axios from 'axios';
import { useAuth } from './auth';

const api = axios.create({
  baseURL: '/api',
  withCredentials: true
});

api.interceptors.request.use(config => {
  const { getToken } = useAuth();
  const token = getToken();
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  response => response,
  error => {
    const { clearToken } = useAuth();
    
    if (error.response?.status === 401) {
      clearToken();
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

export default api;