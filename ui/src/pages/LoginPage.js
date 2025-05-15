import { Form, Input, Button, Card, message } from 'antd';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import { login } from '../api/auth';
import { Link } from 'react-router-dom';
import axios from "axios";

export default function LoginPage() {
    const onFinish = async (values) => {
        try {
            await login(values.email, values.password);
            // Логирование успешного входа
            await axios.post('/api/history', {
                description: `Пользователь ${values.email} вошел в систему`
            }, { withCredentials: true });
            window.location.href = '/';
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

                <Link to="/register">Нет аккаунта? Зарегистрируйтесь</Link>
            </Form>
        </Card>
    );
}