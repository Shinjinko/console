import { useEffect, useState } from 'react';
import { Table, Spin, Button } from 'antd';
import axios from 'axios';

export default function ConsolesPage() {
    const [consoles, setConsoles] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get('/api/consoles', { withCredentials: true })
            .then(res => setConsoles(res.data))
            .finally(() => setLoading(false));
    }, []);

    const columns = [
        { title: 'ID', dataIndex: 'id' },
        { title: 'Название', dataIndex: 'name' },
        { title: 'Тип', dataIndex: 'type' }
    ];

    return (
        <Spin spinning={loading}>
            <Table dataSource={consoles} columns={columns} rowKey="id" />
        </Spin>
    );
}