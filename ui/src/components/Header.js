import { useContext } from 'react';
import { AuthContext } from '../AuthContext';
import { Layout, Menu } from 'antd';
import { UserOutlined, ConsoleSqlOutlined, LogoutOutlined } from '@ant-design/icons';
import {Link, useNavigate} from 'react-router-dom';
import { logout } from '../api/auth';

const { Header } = Layout;

export default function AppHeader() {
    const context = useContext(AuthContext);
    const navigate = useNavigate();

    // Проверка на наличие контекста
    if (!context) {
        console.error('AuthContext is undefined. Ensure AppHeader is wrapped in AuthProvider.');
        return null;
    }

    const { user, setUser } = context;

    const handleLogout = async () => {
        try {
            await logout();
            setUser(null);
            navigate('/login');
        } catch (error) {
            console.error('Ошибка выхода:', error);
        }
    };

    return (
        <Header style={{ background: '#fff', padding: 0 }}>
            <div className="logo" style={{ float: 'left', margin: '0 24px' }}>
                <Link to="/">
                    <h2>Console Manager</h2>
                </Link>
            </div>
            <Menu mode="horizontal" style={{ float: 'right', lineHeight: '64px' }}>
                <Menu.Item key="consoles" icon={<ConsoleSqlOutlined />} onClick={() => navigate('/consoles')}>
                    Consoles
                </Menu.Item>
                <Menu.SubMenu key="user" title={<span><UserOutlined /> User</span>}>
                    {user ? (
                        <>
                            <Menu.Item key="account" onClick={() => navigate('/account')}>
                                {user.name}
                            </Menu.Item>
                            <Menu.Item key="logout" onClick={handleLogout}>
                                <LogoutOutlined /> Выйти
                            </Menu.Item>
                        </>
                    ) : (
                        <>
                            <Menu.Item key="login" onClick={() => navigate('/login')}>
                                Войти
                            </Menu.Item>
                            <Menu.Item key="register" onClick={() => navigate('/register')}>
                                Зарегистрироваться
                            </Menu.Item>
                        </>
                    )}
                </Menu.SubMenu>
            </Menu>
        </Header>
    );
}