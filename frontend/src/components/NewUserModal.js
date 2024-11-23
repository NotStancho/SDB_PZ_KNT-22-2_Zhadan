import React, { useState } from 'react';
import { Button, Modal, Form } from 'react-bootstrap';
import axios from 'axios';

const NewUserModal = ({ show, handleClose, onUserAdded }) => {
    const [firstname, setFirstname] = useState('');
    const [lastname, setLastname] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [phone, setPhone] = useState('');
    const [role, setRole] = useState('ROLE_CLIENT');

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newUser = { firstname, lastname, email, password, phone, role };
        const token = localStorage.getItem('jwtToken');

        try {
            const response = await axios.post('http://localhost:8080/api/users/register', newUser, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === 200) {
                onUserAdded();
                handleClose();
            } else {
                console.error('Помилка при додаванні користувача');
            }
        } catch (error) {
            console.error('Помилка при додаванні користувача', error);
        }
    };

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Додати користувача</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Ім'я</Form.Label>
                        <Form.Control type="text" value={firstname} onChange={(e) => setFirstname(e.target.value)} required />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Прізвище</Form.Label>
                        <Form.Control type="text" value={lastname} onChange={(e) => setLastname(e.target.value)} required />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Пароль</Form.Label>
                        <Form.Control type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Телефон</Form.Label>
                        <Form.Control type="text" value={phone} onChange={(e) => setPhone(e.target.value)} required />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Роль</Form.Label>
                        <Form.Control as="select" value={role} onChange={(e) => setRole(e.target.value)}>
                            <option value="ROLE_CLIENT">Клієнт</option>
                            <option value="ROLE_MANAGER">Менеджер</option>
                            <option value="ROLE_SERVICE_ENGINEER">Сервісний інженер</option>
                            <option value="ROLE_ADMIN">Адміністратор</option>
                        </Form.Control>
                    </Form.Group>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleClose}>
                            Скасувати
                        </Button>
                        <Button variant="primary" type="submit">
                        Додати
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default NewUserModal;
