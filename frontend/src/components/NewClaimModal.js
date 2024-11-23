import React, { useState } from 'react';
import { Button, Modal, Form, Row, Col, Container } from 'react-bootstrap';

const NewClaimModal = ({ show, handleClose, onSubmitSuccess }) => {
    const [serialNumber, setSerialNumber] = useState("");
    const [model, setModel] = useState("");
    const [type, setType] = useState("");
    const [purchaseDate, setPurchaseDate] = useState("");
    const [defectDescription, setDefectDescription] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newClaim = {
            equipment: { serialNumber, model, type, purchaseDate},
            defectDescription
        };

        try {
            const token = localStorage.getItem('jwtToken');
            const response = await fetch('http://localhost:8080/api/claims/add-claim', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(newClaim),
            });

            if (response.status === 201) {
                onSubmitSuccess();
                handleClose();
            } else {
                console.error("Помилка під час подання заявки");
            }
        } catch (error) {
            console.error("Помилка при поданні заявки:", error);
        }
    };

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Подати нову заявку</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Container>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Тип обладнання</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={type}
                                        onChange={(e) => setType(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Модель обладнання</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={model}
                                        onChange={(e) => setModel(e.target.value)}
                                        required
                                    />
                                </Form.Group>
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
                                    <Form.Label>Дата покупки</Form.Label>
                                    <Form.Control
                                        type="date"
                                        value={purchaseDate}
                                        onChange={(e) => setPurchaseDate(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Опис дефекту</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={6}
                                        value={defectDescription}
                                        onChange={(e) => setDefectDescription(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                    </Container>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={handleClose}>
                            Закрити
                        </Button>
                        <Button variant="primary" type="submit">
                            Подати заявку
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default NewClaimModal;
