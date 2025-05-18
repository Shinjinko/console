import { Form, Input, Button, Card, message } from 'antd';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import {getCurrentUser, login} from '../api/auth';
import { Link, useNavigate } from 'react-router-dom';
import {AuthContext} from "../AuthContext";
import {useContext} from "react";

export default function LoginPage() {
    const { setUser } = useContext(AuthContext);
    const navigate = useNavigate();

    const onFinish = async (values) => {
        try {
            await login(values.email, values.password);
            const userData = await getCurrentUser();
            setUser(userData); // Обновляем состояние
            navigate('/');
        } catch (error) {
            message.error('Ошибка входа: ' + error.message);
        }
    };

    return (
        <Card title="Вход" style={{ maxWidth: 400, margin: '100px auto' }}>
            <Form onFinish={onFinish}>
                <Form.Item name="email" rules={[{ required: true, message: 'Введите email!' }]}>
                    <Input prefix={<MailOutlined />} placeholder="Email" />
                </Form.Item>

                <Form.Item name="password" rules={[{ required: true, message: 'Введите пароль!' }]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="Пароль" />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" block>
                        Войти
                    </Button>
                </Form.Item>

                <Link to="/register">Нет аккаунта? Зарегистрироваться</Link>
            </Form>
        </Card>
    );
}