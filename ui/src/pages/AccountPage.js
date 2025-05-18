import React, { useContext } from 'react';
import { AuthContext } from '../AuthContext';
import { Card, Descriptions, Spin } from 'antd';
import { useNavigate } from 'react-router-dom';

const AccountPage = () => {
    const { user, loading } = useContext(AuthContext);
    const navigate = useNavigate();

    if (loading) {
        return <Spin tip="Загрузка..." style={{ display: 'block', margin: '100px auto' }} />;
    }

    if (!user) {
        navigate('/login');
        return null;
    }

    return (
        <div style={{ padding: '24px', maxWidth: '600px', margin: '0 auto' }}>
            <Card title="Моя учетная запись" bordered={false}>
                <Descriptions column={1} bordered>
                    <Descriptions.Item label="Имя">{user.name}</Descriptions.Item>
                    <Descriptions.Item label="Email">{user.email}</Descriptions.Item>
                </Descriptions>
            </Card>
        </div>
    );
};

export default AccountPage;