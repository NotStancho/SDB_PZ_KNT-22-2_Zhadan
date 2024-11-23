import React, { useState, useEffect } from 'react';
import { Modal, Button, Row, Col, Form, Container } from 'react-bootstrap';
import axios from "axios";

const ClaimDetailsModal = ({ show, handleClose, claimId, role, onClaimUpdated }) => {
    const [claim, setClaim] = useState(null);
    const [statuses, setStatuses] = useState([]);
    const [editable, setEditable] = useState(role === 'ROLE_MANAGER' || role === 'ROLE_ADMIN');
    const [statusEditable, setStatusEditable] = useState(role === 'ROLE_MANAGER' || role === 'ROLE_ADMIN' || role === 'ROLE_SERVICE_ENGINEER');
    const [statusId, setStatusId] = useState('');
    const [actionDescription, setActionDescription] = useState('');
    const [type, setType] = useState('');
    const [model, setModel] = useState('');
    const [serialNumber, setSerialNumber] = useState('');
    const [purchaseDate, setPurchaseDate] = useState('');
    const [isDescriptionValid, setIsDescriptionValid] = useState(true);

    // Отримання деталей заявки
    useEffect(() => {
        const fetchClaimDetails = async () => {
            const token = localStorage.getItem('jwtToken');
            try {
                const response = await fetch(`http://localhost:8080/api/claims/${claimId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                const data = await response.json();
                setClaim(data);
                setStatusId(data.status.statusId);
                setActionDescription(data.actionDescription || '');
                setType(data.equipment.type);
                setModel(data.equipment.model);
                setSerialNumber(data.equipment.serialNumber);
                setPurchaseDate(new Date(data.equipment.purchaseDate).toISOString().split('T')[0]);
            } catch (error) {
                console.error("Помилка при завантаженні деталей заявки:", error);
            }
        };

        if (claimId && show) {
            fetchClaimDetails();
        }
    }, [claimId, show]);

    // Отримання статусів
    useEffect(() => {
        const fetchStatuses = async () => {
            const token = localStorage.getItem('jwtToken');
            if (role === 'ROLE_MANAGER' || role === 'ROLE_ADMIN' || role === 'ROLE_SERVICE_ENGINEER') {
                try {
                    const response = await axios.get('http://localhost:8080/api/statuses', {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    setStatuses(response.data);
                } catch (error) {
                    console.error('Помилка під час отримання статусів:', error);
                }
            }
        };

        fetchStatuses();
    }, [role]);

    const handleSaveChanges = async () => {
        if (statusEditable && !actionDescription.trim()) {
            setIsDescriptionValid(false);
            return;
        } else {
            setIsDescriptionValid(true);
        }

        const updatedClaim = {
            ...claim,
            status: { statusId },
            equipment: {
                equipmentId: claim.equipment.equipmentId,
                type,
                model,
                serialNumber,
                purchaseDate: purchaseDate,
            },
            actionDescription: actionDescription
        };

        const token = localStorage.getItem('jwtToken');
        try {
            const response = await fetch(`http://localhost:8080/api/claims/${claimId}?actionDescription=${actionDescription}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(updatedClaim),
            });

            if (response.ok) {
                console.log('Зміни збережено успішно');
                handleClose();
                onClaimUpdated();
            } else {
                console.error('Помилка при збереженні змін');
            }
        } catch (error) {
            console.error("Помилка при збереженні змін:", error);
        }

        console.log('Updated claim data:', updatedClaim);

    };

    return (
        <Modal show={show} onHide={handleClose} centered size="lg">
            <Modal.Header closeButton>
                <Modal.Title className="w-100 text-center">Деталі заявки</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {claim && (
                    <Container>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-0">
                                    <Form.Label><strong>ID заявки: </strong>{claim.claimId}</Form.Label>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label><strong>Тип обладнання:</strong></Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={type}
                                        onChange={(e) => setType(e.target.value)}
                                        readOnly={!editable}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label><strong>Модель обладнання:</strong></Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={model}
                                        onChange={(e) => setModel(e.target.value)}
                                        readOnly={!editable}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label><strong>Серійний номер:</strong></Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={serialNumber}
                                        onChange={(e) => setSerialNumber(e.target.value)}
                                        readOnly={!editable}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label><strong>Дата покупки:</strong></Form.Label>
                                    <Form.Control
                                        type="date"
                                        value={purchaseDate}
                                        onChange={(e) => setPurchaseDate(e.target.value)}
                                        readOnly={!editable}
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label><strong>Опис дефекту:</strong></Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        value={claim.defectDescription}
                                        onChange={(e) => setClaim({ ...claim, defectDescription: e.target.value })}
                                        readOnly={!editable}
                                        style={{ maxHeight: '150px', overflowY: 'auto' }}
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Статус:</Form.Label>
                                    {statusEditable ? (
                                        <Form.Control
                                            as="select"
                                            value={statusId}
                                            onChange={(e) => setStatusId(e.target.value)}
                                            readOnly={!statusEditable}
                                        >
                                            {statuses.map(statusOption => (
                                                <option key={statusOption.statusId} value={statusOption.statusId}>
                                                    {statusOption.name}
                                                </option>
                                            ))}
                                        </Form.Control>
                                    ) : (
                                        <Form.Control
                                            type="text"
                                            value={claim.status.name}
                                            readOnly
                                        />
                                    )}
                                </Form.Group>

                                <Form.Group className="mb-0">
                                    <Form.Label><strong>Власник заявки: </strong></Form.Label>
                                    <div className="mt-1">{`${claim.client.firstname} ${claim.client.lastname}`}</div>
                                    <div className="mt-1">{claim.client.email}</div>
                                    <div className="mt-1">{claim.client.phone}</div>
                                </Form.Group>
                                {statusEditable && (
                                    <Form.Group className="mb-3">
                                        <Form.Label><strong>Опис виконаної роботи:</strong></Form.Label>
                                        <Form.Control
                                            as="textarea"
                                            rows={3}
                                            value={actionDescription}
                                            onChange={(e) => setActionDescription(e.target.value)}
                                            isInvalid={!isDescriptionValid}
                                            style={{ maxHeight: '150px', overflowY: 'auto' }}
                                        />
                                        {!isDescriptionValid && (
                                            <Form.Control.Feedback type="invalid">
                                                Опис виконаної роботи є обов'язковим.
                                            </Form.Control.Feedback>
                                        )}
                                    </Form.Group>
                                )}
                            </Col>
                        </Row>
                    </Container>
                )}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Закрити
                </Button>
                {editable || statusEditable ? (
                    <Button variant="primary" onClick={handleSaveChanges}>
                        Зберегти зміни
                    </Button>
                ) : null}
            </Modal.Footer>
        </Modal>
    );
};

export default ClaimDetailsModal;
