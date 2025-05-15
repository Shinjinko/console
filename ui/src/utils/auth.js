export const checkAuth = async () => {
    try {
        const response = await fetch('/api/users/me', {
            method: 'GET',
            credentials: 'include'
        });
        return response.ok;
    } catch (error) {
        return false;
    }
};

export const getAuthToken = () => {
    return document.cookie
        .split('; ')
        .find(row => row.startsWith('JSESSIONID='))
        ?.split('=')[1];
};