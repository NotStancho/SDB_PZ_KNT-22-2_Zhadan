import React, { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Form, Button, Container, Row, Col, Alert } from 'react-bootstrap';

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [messageVariant, setMessageVariant] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/users/login', {
                email,
                password,
            });

            const token = response.data;
            localStorage.setItem('jwtToken', token);

            const userResponse = await axios.get('http://localhost:8080/api/users/me', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const userRole = userResponse.data.role;

            if (userRole  === 'ROLE_CLIENT') {
                navigate('/client-home');
            } else if (userRole  === 'ROLE_MANAGER') {
                navigate('/manager-home');
            } else if (userRole  === 'ROLE_SERVICE_ENGINEER') {
                navigate('/service_engineer-home');
            } else if (userRole  === 'ROLE_ADMIN') {
                navigate('/admin-home');
            }
        } catch (error) {
            console.error('Помилка логування:', error);
            setMessageVariant('danger');
            setMessage('Помилка логування. Спробуйте ще раз.');
        }
    };
    return (
        <Container className="d-flex justify-content-center align-items-center vh-100" style={{ maxWidth: '100%' }}>
            <Row className="w-100 justify-content-center" style={{ maxWidth: '600px' }}>
                <Col>
                    <div className="card p-3 shadow bg-white rounded">
                        <div className="d-flex justify-content-center mb-3">
                            <h2>Ласкаво просимо!</h2>
                        </div>
                        <div className="text-center mb-3">
                            <p>Будь ласка, увійдіть, щоб продовжити</p>
                        </div>
                        {message && (
                            <Alert variant={messageVariant} className="text-center">
                                {message}
                            </Alert>
                        )}
                        <Form onSubmit={handleLogin}>
                            <Form.Group className="mb-3" controlId="formBasicEmail">
                                <Form.Label>Email адреса</Form.Label>
                                <Form.Control
                                    type="email"
                                    placeholder="Введіть email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="formBasicPassword">
                                <Form.Label>Пароль</Form.Label>
                                <Form.Control
                                    type="password"
                                    placeholder="Введіть пароль"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </Form.Group>

                            <Button variant="primary" type="submit" className="w-100">
                                Увійти
                            </Button>
                        </Form>
                        <div className="text-center mt-4">
                            <p>Немає облікового запису? <Link to="/register">Зареєструйтесь тут</Link></p>
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export default LoginPage;