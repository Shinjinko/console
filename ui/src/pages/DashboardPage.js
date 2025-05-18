import { useEffect, useState } from 'react';
import { List, Card, Spin } from 'antd';
import { getActivityHistory } from '../api/history';

export default function DashboardPage() {
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const fetchHistory = async () => {
            try {
                const data = await getActivityHistory();
                setHistory(data);
            } catch (error) {
                console.error('Ошибка загрузки истории:', error);
            }
            setLoading(false);
        };
        fetchHistory();
    }, []);

    return (
        <Card title="Последние действия" style={{ margin: 20 }}>
            <Spin spinning={loading}>
                <List
                    dataSource={history}
                    renderItem={(item) => (
                        <List.Item>
                            {item.timestamp}: {item.action}
                        </List.Item>
                    )}
                />
            </Spin>
        </Card>
    );
}