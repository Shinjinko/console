import { useEffect, useState } from 'react';
import {Table, Spin, Button, Modal, message, Popconfirm, Dropdown, Menu, Form, Input, Card, Space} from 'antd';
import axios from 'axios';

export default function ConsolesPage() {
    const [consoles, setConsoles] = useState([]);
    const [allConsoles, setAllConsoles] = useState([]);
    const [userConsolesIds, setUserConsolesIds] = useState(new Set());
    const [addVisible, setAddVisible] = useState(false);
    const [editVisible, setEditVisible] = useState(false);
    const [loading, setLoading] = useState(true);
    const [editingConsole, setEditingConsole] = useState(null);
    const [form] = Form.useForm();

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const userConsolesRes = await axios.get('/api/users/me/consoles', { withCredentials: true });
                setConsoles(userConsolesRes.data);
                const userConsolesIdsSet = new Set(userConsolesRes.data.map(c => c.id));
                setUserConsolesIds(userConsolesIdsSet);
                const allConsolesRes = await axios.get('/api/consoles', { withCredentials: true });
                setAllConsoles(allConsolesRes.data);
            } catch (error) {
                message.error('Ошибка загрузки данных');
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleRemove = async (consoleId) => {
        try {
            await axios.delete(`/api/users/me/consoles/${consoleId}`, { withCredentials: true });
            setConsoles(consoles.filter(c => c.id !== consoleId));
            setUserConsolesIds(prev => new Set([...prev].filter(id => id !== consoleId)));
            message.success('Консоль удалена');
        } catch (error) {
            message.error('Ошибка удаления консоли');
        }
    };

    const handleAdd = async (consoleId) => {
        try {
            await axios.post(`/api/users/me/consoles/${consoleId}`, {}, { withCredentials: true });
            const newConsole = allConsoles.find(c => c.id === consoleId);
            setConsoles([...consoles, newConsole]);
            setUserConsolesIds(prev => new Set([...prev, consoleId]));
            message.success('Консоль добавлена');
        } catch (error) {
            message.error('Ошибка добавления консоли');
        }
    };

    const handleEdit = (console) => {
        setEditingConsole(console);
        form.setFieldsValue({ name: console.name, type: console.type });
        setEditVisible(true);
    };

    const handleEditSubmit = async (values) => {
        try {
            await axios.put(`/api/consoles/${editingConsole.id}`,
                { name: values.name, type: values.type },
                { withCredentials: true }
            );
            setConsoles(consoles.map(c =>
                c.id === editingConsole.id ? { ...c, name: values.name, type: values.type } : c
            ));
            setEditVisible(false);
            message.success('Консоль обновлена');
        } catch (error) {
            message.error('Ошибка обновления консоли');
        }
    };

    const menu = (record) => (
        <Menu>
            <Menu.Item key="edit" onClick={() => handleEdit(record)}>
                Редактировать
            </Menu.Item>
            <Menu.Item key="delete">
                <Popconfirm title="Удалить консоль?" onConfirm={() => handleRemove(record.id)}>
                    Удалить
                </Popconfirm>
            </Menu.Item>
        </Menu>
    );

    const columns = [
        { title: 'ID', dataIndex: 'id' },
        { title: 'Название', dataIndex: 'name' },
        { title: 'Тип', dataIndex: 'type' },
        {
            title: 'Действие',
            render: (text, record) => (
                <Space>
                    <Button
                        type="link"
                        onClick={() => handleEdit(record)}
                    >
                        Редактировать
                    </Button>
                    <Popconfirm
                        title="Удалить консоль?"
                        onConfirm={() => handleRemove(record.id)}
                    >
                        <Button type="link" danger>
                            Удалить
                        </Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    const addColumns = [
        { title: 'ID', dataIndex: 'id' },
        { title: 'Название', dataIndex: 'name' },
        { title: 'Тип', dataIndex: 'type' },
        {
            title: 'Действие',
            render: (text, record) => (
                <Button onClick={() => handleAdd(record.id)}>Добавить</Button>
            ),
        },
    ];

    return (
        <Spin spinning={loading}>
            <Card
                title="Управление консолями"
                style={{
                    margin: 20,
                    maxWidth: 2000,
                }}
                extra={
                    <Button
                        type="primary"
                        onClick={() => setAddVisible(true)}
                    >
                        Добавить консоль
                    </Button>
                }
            >
                <Table
                    dataSource={consoles}
                    columns={columns}
                    rowKey="id"
                    bordered
                    pagination={{ pageSize: 15 }}
                />

            <Modal
                title="Добавить консоль"
                open={addVisible}
                onCancel={() => setAddVisible(false)}
                footer={null}
            >
                <Table
                    dataSource={allConsoles.filter(c => !userConsolesIds.has(c.id))}
                    columns={addColumns}
                    rowKey="id"
                />
            </Modal>
            <Modal
                title="Редактировать консоль"
                open={editVisible}
                onCancel={() => setEditVisible(false)}
                onOk={() => form.submit()}
            >
                <Form
                    form={form}
                    onFinish={handleEditSubmit}
                    labelCol={{ span: 6 }}
                    wrapperCol={{ span: 16 }}
                >
                    <Form.Item
                        name="name"
                        label="Название"
                        rules={[{ required: true, message: 'Введите название консоли' }]}
                    >
                        <Input style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item
                        name="type"
                        label="Тип"
                        rules={[{ required: true, message: 'Введите тип консоли' }]}
                    >
                        <Input style={{ width: '100%' }} />
                    </Form.Item>
                </Form>
            </Modal>
                </Card>
        </Spin>
    );
}