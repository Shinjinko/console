import axios from 'axios';
import {logActivity} from "./history";

export const login = async (email, password) => {
    const response = await axios.post(
        '/api/auth/login',
        `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`,
        {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        }
    );

    await logActivity(`Пользователь ${email} вошел в систему`);
    return response.data;
};

export const register = async (data) => {
    const response = await axios.post('/api/users/create', data, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true
    });

    await logActivity(`Зарегистрирован новый пользователь: ${data.email}`);
    return response.data;
};

export const logout = async () => {
    await axios.post('/api/auth/logout', {}, { withCredentials: true });
};

export const getCurrentUser = async () => {
    const response = await axios.get('/api/users/me', { withCredentials: true });
    return {
        id: response.data.id,
        name: response.data.name,
        email: response.data.email
    };
};

axios.defaults.withCredentials = true;