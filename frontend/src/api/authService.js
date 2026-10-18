import { api } from './client.js';
export const register = (payload) => api('/api/auth/register', { method: 'POST', body: payload, auth: false });
export const login = (payload) => api('/api/auth/login', { method: 'POST', body: payload, auth: false });
export const getProfile = () => api('/api/profile');
export const updateProfile = (payload) => api('/api/profile', { method: 'PUT', body: payload });
