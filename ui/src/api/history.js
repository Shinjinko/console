import axios from 'axios';

export const getActivityHistory = async () => {
    const response = await axios.get('/api/history', {
        withCredentials: true
    });
    return response.data.map(item => ({
        id: item.id,
        action: item.description,
        timestamp: item.createdAt ? new Date(item.createdAt).toLocaleString() : 'N/A'
    }));
};

export const logActivity = async (action) => {
    await axios.post('/api/history', {
        description: action
    }, {
        withCredentials: true
    });
};