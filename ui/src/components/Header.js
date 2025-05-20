import { useContext } from 'react';
import { AuthContext } from '../AuthContext';
import { Layout, Button, Space } from 'antd';
import { UserOutlined, HistoryOutlined, LogoutOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { logout } from '../api/auth';

const { Header } = Layout;

export default function AppHeader() {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <Header style={{
            background: '#fff',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '0 24px'
        }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                <Link to="/">
                    <h2 style={{ margin: 0 }}>Console Manager</h2>
                </Link>

                <Space>
                    <Button
                        type="text"
                        icon={<HistoryOutlined />}
                        onClick={() => navigate('/')}
                    >
                        Last actions
                    </Button>
                    <Button
                        type="text"
                        onClick={() => navigate('/consoles')}
                    >
                        Consoles
                    </Button>
                </Space>
            </div>

            <Space>
                {user ? (
                    <>
                        <Button
                            type="text"
                            icon={<UserOutlined />}
                            onClick={() => navigate('/account')}
                        >
                            {user.name}
                        </Button>
                        <Button
                            type="text"
                            icon={<LogoutOutlined />}
                            onClick={handleLogout}
                            danger
                        >
                            Log out
                        </Button>
                    </>
                ) : (
                    <>
                        <Button onClick={() => navigate('/login')}>Login</Button>
                        <Button type="primary" onClick={() => navigate('/register')}>
                            Register
                        </Button>
                    </>
                )}
            </Space>
        </Header>
    );
}