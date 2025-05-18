import React, { createContext, useState, useEffect } from 'react';
import { getCurrentUser } from './api/auth';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const verifyAuth = async () => {
            try {
                const isAuth = await checkAuth();
                if (isAuth) {
                    const userData = await getCurrentUser();
                    setUser(userData);
                }
                setLoading(false);
            } catch (error) {
                setLoading(false);
            }
        };
        verifyAuth();
    }, []);

    return (
        <AuthContext.Provider value={{ user, setUser, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};