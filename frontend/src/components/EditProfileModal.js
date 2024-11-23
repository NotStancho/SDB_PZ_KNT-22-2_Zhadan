import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Container, Row, Col } from 'react-bootstrap';
import axios from 'axios';

const EditProfileModal = ({ show, handleClose, selectedUser, isAdmin, onUserUpdated }) => {
    const [userData, setUserData] = useState({
        firstname: '',
        lastname: '',
        email: '',
        password: '',
        phone: '',
        role: ''
    });

    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        if (selectedUser) {
            setUserData({
                firstname: selectedUser.firstname,
                lastname: selectedUser.lastname,
                email: selectedUser.email,
                password: '',
                phone: selectedUser.phone,
                role: selectedUser.role
            });
        } else {
            const fetchUserData = async () => {
                const token = localStorage.getItem('jwtToken');
                try {
                    const response = await axios.get('http://localhost:8080/api/users/me', {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    setUserData({
                        firstname: response.data.firstname,
                        lastname: response.data.lastname,
                        email: response.data.email,
                        password: '',
                        phone: response.data.phone,
                        role: response.data.role
                    });
                } catch (error) {
                    console.error("Помилка при завантаженні профілю:", error);
                    setErrorMessage('Не вдалося завантажити дані профілю.');
                }
            };

            if (show) {
                fetchUserData();
            }
        }
    }, [show, selectedUser]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUserData((prevData) => ({ ...prevData, [name]: value }));
    };

    const handleSaveChanges = async () => {
        const token = localStorage.getItem('jwtToken');
        try {
            let response;

            // Якщо це редагування адміністратором іншого користувача
            if (isAdmin && selectedUser) {
                response = await axios.put(`http://localhost:8080/api/users/edit/${selectedUser.userId}`, userData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
            } else {
                // Якщо це редагування особистого профілю
                response = await axios.put('http://localhost:8080/api/users/edit-profile', userData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
            }

            if (response.status === 200) {
                handleClose();
                if (onUserUpdated) {
                    onUserUpdated();
                }
                console.log('Профіль успішно оновлено');
            }
        } catch (error) {
            console.error("Помилка при збереженні профілю:", error);
            setErrorMessage('Не вдалося зберегти зміни.');
        }
    };


    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title className="w-100 text-center">Редагування профілю</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Container>
                    {errorMessage && <p className="text-danger">{errorMessage}</p>}
                    <Form>
                        <Row>
                            <Col>
                                <Form.Group className="mb-3">
                                    <Form.Label>Ім'я</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="firstname"
                                        value={userData.firstname}
                                        onChange={handleInputChange}
                                    />
                                </Form.Group>
                            </Col>
                            <Col>
                                <Form.Group className="mb-3">
                                    <Form.Label>Прізвище</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="lastname"
                                        value={userData.lastname}
                                        onChange={handleInputChange}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Form.Group className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={userData.email}
                                onChange={handleInputChange}
                                readOnly={!isAdmin}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Пароль</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                value={userData.password}
                                onChange={handleInputChange}
                                placeholder="Введіть новий пароль"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Телефон</Form.Label>
                            <Form.Control
                                type="text"
                                name="phone"
                                value={userData.phone}
                                onChange={handleInputChange}
                            />
                        </Form.Group>

                        {isAdmin && (
                            <Form.Group className="mb-3">
                                <Form.Label>Роль</Form.Label>
                                <Form.Control
                                    as="select"
                                    name="role"
                                    value={userData.role}
                                    onChange={handleInputChange}
                                >
                                    <option value="ROLE_CLIENT">Клієнт</option>
                                    <option value="ROLE_MANAGER">Менеджер</option>
                                    <option value="ROLE_SERVICE_ENGINEER">Сервісний інженер</option>
                                    <option value="ROLE_ADMIN">Адміністратор</option>
                                </Form.Control>
                            </Form.Group>
                        )}
                    </Form>
                </Container>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Закрити
                </Button>
                <Button variant="primary" onClick={handleSaveChanges}>
                    Зберегти зміни
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default EditProfileModal;
