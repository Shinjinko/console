import { useState } from 'react';
import Editor from '@monaco-editor/react';
import { Button, Select, Space, Alert } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import {logActivity} from "../api/history";
import axios from "axios";

const languages = [
    { value: 'python', label: 'Python' },
    { value: 'javascript', label: 'JavaScript' },
    { value: 'java', label: 'Java' },
    { value: 'cpp', label: 'C++' },
];

const CodeEditor = () => {
    const [code, setCode] = useState('# Начните писать код здесь\nprint("Hello World!")');
    const [selectedLanguage, setLanguage] = useState('python');
    const [output, setOutput] = useState('');
    const [isRunning, setIsRunning] = useState(false);

    const handleRunCode = async () => {
        try {
            const result = await executeCode(code, selectedLanguage);
            await axios.post('/api/history', {
                description: `Выполнен код на ${selectedLanguage}: ${result}`
            }, { withCredentials: true });
        } catch (error) {
            await axios.post('/api/history', {
                description: `Ошибка выполнения кода: ${error.message}`
            }, { withCredentials: true });
        }
    };

    return (
        <div style={{ height: 'calc(100vh - 64px)' }}>
            <Space style={{ padding: '10px', background: '#f0f2f5' }}>
                <Select
                    value={selectedLanguage}
                    onChange={setLanguage}
                    options={languages}
                    style={{ width: 120 }}
                />
                <Button
                    type="primary"
                    onClick={handleRunCode}
                    icon={isRunning ? <LoadingOutlined /> : null}
                >
                    Выполнить
                </Button>
            </Space>

            <div style={{ display: 'flex', height: '100%' }}>
                <Editor
                    height="70vh"
                    defaultLanguage={selectedLanguage}
                    value={code}
                    onChange={setCode}
                    theme="vs-dark"
                    options={{
                        minimap: { enabled: false },
                        fontSize: 14,
                    }}
                />

                <div style={{
                    width: '30%',
                    padding: '16px',
                    background: '#1e1e1e',
                    color: '#fff',
                    overflowY: 'auto'
                }}>
                    <h4>Результат выполнения:</h4>
                    <pre style={{ whiteSpace: 'pre-wrap' }}>{output || 'Нажмите "Выполнить" для запуска кода'}</pre>
                </div>
            </div>
        </div>
    );
};

const executeCode = async (code, language) => {
    return new Promise(resolve => {
        setTimeout(() => {
            resolve('Hello World!\nCode executed successfully!');
        }, 1000);
    });
};

export default CodeEditor;