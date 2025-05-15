import { Tree, Button } from 'antd';
import { FolderOutlined, FileTextOutlined } from '@ant-design/icons';

const FilesPanel = () => {
    const files = [
        {
            title: 'Мои проекты',
            key: '0',
            children: [
                {
                    title: 'project1.py',
                    key: '1',
                    icon: <FileTextOutlined />
                },
                {
                    title: 'web-app',
                    key: '2',
                    icon: <FolderOutlined />,
                    children: [
                        { title: 'index.js', key: '3', icon: <FileTextOutlined /> },
                    ]
                },
            ],
        },
    ];

    return (
        <div style={{ padding: '16px' }}>
            <Button type="primary" block style={{ marginBottom: '16px' }}>
                Новый файл
            </Button>
            <Tree
                showIcon
                treeData={files}
            />
        </div>
    );
};

export default FilesPanel;