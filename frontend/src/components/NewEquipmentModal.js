import React, { useState } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import axios from 'axios';

const NewEquipmentModal = ({ show, handleClose, onEquipmentAdded }) => {
    const [serialNumber, setSerialNumber] = useState('');
    const [model, setModel] = useState('');
    const [type, setType] = useState('');
    const [purchaseDate, setPurchaseDate] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newEquipment = {
            serialNumber,
            model,
            type,
            purchaseDate: new Date(purchaseDate).toISOString().split('T')[0]
        };

        const token = localStorage.getItem('jwtToken');

        try {
            const response = await axios.post('http://localhost:8080/api/equipment', newEquipment, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === 200) {
                onEquipmentAdded();
                handleClose();
            } else {
                console.error('Помилка при додаванні обладнання');
            }
        } catch (error) {
            console.error('Помилка при додаванні обладнання', error);
        }
    };

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Додати обладнання</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Серійний номер</Form.Label>
                        <Form.Control
                            type="text"
                            value={serialNumber}
                            onChange={(e) => setSerialNumber(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Модель</Form.Label>
                        <Form.Control
                            type="text"
                            value={model}
                            onChange={(e) => setModel(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Тип</Form.Label>
                        <Form.Control
                            type="text"
                            value={type}
                            onChange={(e) => setType(e.target.value)}
                            required
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Дата покупки</Form.Label>
                        <Form.Control
                            type="date"
                            value={purchaseDate}
                            onChange={(e) => setPurchaseDate(e.target.value)}
                            required
                        />
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

export default NewEquipmentModal;
