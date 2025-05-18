import { Navigate } from 'react-router-dom';
import { checkAuth } from '../utils/auth';
import {Spin} from "antd";
import {useContext, useEffect, useState} from "react";
import { AuthContext } from '../AuthContext';

export default function PrivateRoute({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const { user, setUser } = useContext(AuthContext);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkAuth().then(auth => {
            setIsAuthenticated(auth);
            setLoading(false);
        });
    }, []);

    if (loading) return <Spin fullscreen />;
    if (!isAuthenticated || !user) {
        return <Navigate to="/login" replace />;
    }
    return children;
}