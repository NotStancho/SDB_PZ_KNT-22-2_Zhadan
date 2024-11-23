import React, { useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Form, Button, Container, Row, Col, Alert } from 'react-bootstrap';

function RegisterPage() {
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [phone, setPhone] = useState('');
    const [message, setMessage] = useState('');
    const [messageVariant, setMessageVariant] = useState('');
    const [role] = useState('ROLE_CLIENT');

    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/users/register', {
                firstname, lastname, email, password, phone, role
            });
            if (response.status === 200) {
                setMessageVariant('success');
                setMessage('Реєстрація успішна!');
                setTimeout(() => navigate('/login'), 2000); // Переадресація після 2 секунд
            } else {
                setMessageVariant('danger');
                setMessage('Реєстрація не вдалася. Спробуйте ще раз.');
            }
        } catch (error) {
            console.error('Помилка реєстрації:', error);
            setMessageVariant('danger');
            setMessage('Помилка реєстрації. Спробуйте ще раз.');
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-center vh-100">
            <Row className="justify-content-center w-100">
                <Col xs={12} md={8} lg={6}>
                    <div className="card p-4 shadow bg-white rounded">
                        <div className="d-flex justify-content-center mb-3">
                            <h2>Реєстрація</h2>
                        </div>
                        {message && (
                            <Alert variant={messageVariant} className="text-center">
                                {message}
                            </Alert>
                        )}
                        <Form onSubmit={handleRegister}>
                            <Form.Group className="mb-3" controlId="formBasicFirstname">
                                <Form.Label>Ім'я</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="Введіть ім'я"
                                    value={firstname}
                                    onChange={(e) => setFirstname(e.target.value)}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="formBasicLastname">
                                <Form.Label>Прізвище</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="Введіть прізвище"
                                    value={lastname}
                                    onChange={(e) => setLastname(e.target.value)}
                                    required
                                />
                            </Form.Group>

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

                            <Form.Group className="mb-3" controlId="formBasicPhone">
                                <Form.Label>Номер телефону</Form.Label>
                                <Form.Control
                                    type="tel"
                                    placeholder="Введіть номер телефону у форматі: 380123456789"
                                    value={phone}
                                    onChange={(e) => setPhone(e.target.value)}
                                    required
                                />
                            </Form.Group>

                            <Button variant="primary" type="submit" className="w-100">
                                Зареєструватись
                            </Button>
                        </Form>
                        <div className="text-center mt-4">
                            <p>Вже маєте обліковий запис? <Link to="/login">Увійдіть тут</Link></p>
                        </div>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export default RegisterPage;
