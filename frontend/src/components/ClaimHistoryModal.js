import React, { useState, useEffect } from 'react';
import { Modal, Button, Table, Container } from 'react-bootstrap';

const ClaimHistoryModal = ({ show, handleClose, claimId }) => {
    const [history, setHistory] = useState([]);

    useEffect(() => {
        const fetchHistory = async () => {
            const token = localStorage.getItem('jwtToken');
            try {
                const response = await fetch(`http://localhost:8080/api/claims/${claimId}/history`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                if (!response.ok) {
                    throw new Error('Історія не знайдена');
                }
                const data = await response.json();
                setHistory(data);
            } catch (error) {
                console.error("Помилка при завантаженні історії заявки:", error);
                setHistory([]);
            }
        };

        if (claimId && show) {
            fetchHistory();
        }
    }, [claimId, show]);

    return (
        <Modal show={show} onHide={handleClose} centered size="xl">
            <Modal.Header closeButton>
                <Modal.Title className="w-100 text-center">Історія заявки</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Container>
                    {history.length > 0 ? (
                        <Table striped bordered hover>
                            <thead>
                            <tr>
                                <th style={{ width: '15%' }}>Дата дії</th>
                                <th style={{ width: '25%' }}>Співробітник</th>
                                <th>Опис дії</th>
                            </tr>
                            </thead>
                            <tbody>
                            {history.map((item) => (
                                <tr key={item.claimHistoryId}>
                                    <td>{new Date(item.actionDate).toLocaleDateString()}</td>
                                    <td>{item.employee ? `${item.employee.userId}, ${item.employee.firstname} ${item.employee.lastname}` : 'Невідомий'}</td>
                                    <td>{item.actionDescription}</td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>

                    ) : (
                        <p>Історія не знайдена для цієї заявки.</p>
                    )}
                </Container>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Закрити
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ClaimHistoryModal;

