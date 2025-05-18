import { useContext } from 'react';
import { AuthContext } from '../AuthContext';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, MailOutlined, LockOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { login, getCurrentUser } from '../api/auth';
import axios from 'axios';

export default function RegisterPage() {
    const { setUser } = useContext(AuthContext);
    const navigate = useNavigate();

    const onFinish = async (values) => {
        const formData = new FormData();
        formData.append('name', values.name);
        formData.append('email', values.email);
        formData.append('password', values.password);

        try {
            await axios.post('/api/users/create', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
                withCredentials: true
            });
            await login(values.email, values.password);
            const data = await getCurrentUser();
            setUser(data);
            navigate('/');
            message.success('Регистрация и вход успешны!');
        } catch (error) {
            message.error('Ошибка: ' + (error.response?.data?.message || error.message));
        }
    };

    return (
        <Card title="Регистрация" style={{ maxWidth: 400, margin: '100px auto' }}>
            <Form onFinish={onFinish}>
                <Form.Item name="name" rules={[{ required: true, message: 'Введите имя!' }]}>
                    <Input prefix={<UserOutlined />} placeholder="Имя" />
                </Form.Item>

                <Form.Item name="email" rules={[{ required: true, message: 'Введите email!' }]}>
                    <Input prefix={<MailOutlined />} placeholder="Email" />
                </Form.Item>

                <Form.Item name="password" rules={[{ required: true, message: 'Введите пароль!' }]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="Пароль" />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" block>
                        Зарегистрироваться
                    </Button>
                </Form.Item>

                <Link to="/login">Уже есть аккаунт? Войти</Link>
            </Form>
        </Card>
    );
}