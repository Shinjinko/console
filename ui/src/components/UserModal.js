import React from 'react';
import { Modal, Form, Input, message } from 'antd';

const UserModal = ({ visible, user, onCancel, onSuccess }) => {
    const [form] = Form.useForm();

    const handleSubmit = async (values) => {
        try {
            if (user.id) {
                await api.put(`/users/${user.id}`, values);
                message.success('Пользователь обновлен');
            } else {
                await api.post('/users/create', values);
                message.success('Пользователь создан');
            }
            onSuccess();
        } catch (error) {
            message.error('Ошибка: ' + (error.response?.data || error.message));
        }
    };

    return (
        <Modal
            title={user.id ? 'Редактирование пользователя' : 'Создание пользователя'}
            visible={visible}
            onOk={() => form.submit()}
            onCancel={onCancel}
            destroyOnClose
        >
            <Form
                form={form}
                layout="vertical"
                initialValues={user}
                onFinish={handleSubmit}
            >
                <Form.Item
                    name="name"
                    label="Имя"
                    rules={[{ required: true, message: 'Введите имя' }]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    name="email"
                    label="Email"
                    rules={[{ type: 'email', message: 'Некорректный email' }]}
                >
                    <Input />
                </Form.Item>

                <Form.Item
                    name="password"
                    label="Пароль"
                    rules={[{ required: !user.id, message: 'Введите пароль' }]}
                >
                    <Input.Password />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default UserModal;