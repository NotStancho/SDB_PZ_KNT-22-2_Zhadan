import React, { useState, useEffect } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import axios from 'axios';

const EditEquipmentModal = ({ show, handleClose, selectedEquipment, onEquipmentUpdated }) => {
    const [serialNumber, setSerialNumber] = useState('');
    const [model, setModel] = useState('');
    const [type, setType] = useState('');
    const [purchaseDate, setPurchaseDate] = useState('');

    useEffect(() => {
        if (selectedEquipment) {
            setSerialNumber(selectedEquipment.serialNumber);
            setModel(selectedEquipment.model);
            setType(selectedEquipment.type);
            setPurchaseDate(new Date(selectedEquipment.purchaseDate).toISOString().split('T')[0]);
        }
    }, [selectedEquipment]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const updatedEquipment = {
            serialNumber,
            model,
            type,
            purchaseDate: new Date(purchaseDate).toISOString().split('T')[0]
        };

        const token = localStorage.getItem('jwtToken');

        try {
            const response = await axios.put(`http://localhost:8080/api/equipment/${selectedEquipment.equipmentId}`, updatedEquipment, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.status === 200) {
                onEquipmentUpdated();  // Оновлення таблиці
                handleClose();
            } else {
                console.error('Помилка при оновленні обладнання');
            }
        } catch (error) {
            console.error('Помилка при оновленні обладнання', error);
        }
    };

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Редагувати обладнання</Modal.Title>
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
                            Оновити
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default EditEquipmentModal;
