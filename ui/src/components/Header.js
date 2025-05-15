import { Layout, Menu, Button } from 'antd';
import { UserOutlined, ConsoleSqlOutlined } from '@ant-design/icons';

const { Header } = Layout;

export default function AppHeader() {
    return (
        <Header style={{ background: '#fff', padding: 0 }}>
            <div className="logo" style={{ float: 'left', margin: '0 24px' }}>
                <h2>Console Management</h2>
            </div>
            <Menu mode="horizontal" style={{ float: 'right' }}>
                <Menu.Item key="users" icon={<UserOutlined />}>
                    User
                </Menu.Item>
                <Menu.Item key="consoles" icon={<ConsoleSqlOutlined />}>
                    Consoles
                </Menu.Item>
            </Menu>
        </Header>
    );
}