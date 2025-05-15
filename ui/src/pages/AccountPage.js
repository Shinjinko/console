import { useEffect, useState } from 'react';
import { Card, Descriptions, Button, Spin } from 'antd';
import { getCurrentUser, logout } from '../api/auth';

export default function AccountPage() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const data = await getCurrentUser();
                setUser(data);
            } catch (error) {
                console.error('Ошибка загрузки данных:', error);
            }
            setLoading(false);
        };
        fetchUser();
    }, []);

    const handleLogout = async () => {
        await logout();
        window.location.href = '/login';
    };

    return (
        <Card title="Мой аккаунт" style={{ margin: 20 }}>
            <Spin spinning={loading}>
                {user && (
                    <>
                        <Descriptions bordered column={1}>
                            <Descriptions.Item label="Имя">{user.name}</Descriptions.Item>
                            <Descriptions.Item label="Email">{user.email}</Descriptions.Item>
                        </Descriptions>
                        <Button danger onClick={handleLogout} style={{ marginTop: 16 }}>
                            Выйти
                        </Button>
                    </>
                )}
            </Spin>
        </Card>
    );
}