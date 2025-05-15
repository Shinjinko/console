import { Navigate } from 'react-router-dom';
import { checkAuth } from '../utils/auth';
import {Spin} from "antd";
import {useEffect, useState} from "react";

export default function PrivateRoute({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkAuth().then(auth => {
            setIsAuthenticated(auth);
            setLoading(false);
        });
    }, []);

    if (loading) return <Spin fullscreen />;
    return isAuthenticated ? children : <Navigate to="/login" />;
}