import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, Button, Modal, Container, Pagination, FormControl } from 'react-bootstrap';
import NewEquipmentModal from "./NewEquipmentModal";
import EditEquipmentModal from "./EditEquipmentModal";

const EquipmentTable = () => {
    const [equipment, setEquipment] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [reloadEquipment, setReloadEquipment] = useState(false);
    const [showNewEquipmentModal, setShowNewEquipmentModal] = useState(false);
    const [showEditEquipmentModal, setShowEditEquipmentModal] = useState(false);
    const [selectedEquipment, setSelectedEquipment] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [equipmentToDelete, setEquipmentToDelete] = useState(null);

    const equipmentPerPage = 6;
    const [currentPage, setCurrentPage] = useState(1);

    useEffect(() => {
        const fetchEquipment = async () => {
            const token = localStorage.getItem('jwtToken');
            try {
                const response = await axios.get('http://localhost:8080/api/equipment', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setEquipment(response.data);
            } catch (error) {
                console.error('Помилка при отриманні обладнання', error);
            }
        };
        fetchEquipment();
    }, [reloadEquipment]);

    const handleDeleteEquipment = async () => {
        if (!equipmentToDelete) return;

        const token = localStorage.getItem('jwtToken');
        try {
            const response = await axios.delete(`http://localhost:8080/api/equipment/${equipmentToDelete}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (response.status === 200) {
                setShowDeleteModal(false);
                setReloadEquipment(prev => !prev);
            }
        } catch (error) {
            console.error('Помилка при видаленні обладнання', error);
        }
    };

    const handleEditClick = (equipment) => {
        setSelectedEquipment(equipment);
        setShowEditEquipmentModal(true);
    };

    const handleDeleteClick = (equipmentId) => {
        setEquipmentToDelete(equipmentId);
        setShowDeleteModal(true);
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setCurrentPage(1);
    };

    const filteredEquipment = equipment.filter(e =>
        e.serialNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
        e.model.toLowerCase().includes(searchTerm.toLowerCase()) ||
        e.type.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const indexOfLastEquipment = currentPage * equipmentPerPage;
    const indexOfFirstEquipment = indexOfLastEquipment - equipmentPerPage;
    const currentEquipment = filteredEquipment.slice(indexOfFirstEquipment, indexOfLastEquipment);
    const totalPages = Math.ceil(filteredEquipment.length / equipmentPerPage);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    return (
        <Container className="mt-3 p-4"
                   style={{backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 0 15px rgba(0, 0, 0, 0.1)'}}>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Обладнання</h2>
                <Button variant="primary" onClick={() => setShowNewEquipmentModal(true)}>
                    Додати обладнання
                </Button>
            </div>

            <div className="mb-3 d-flex justify-content-end">
                <FormControl type="text" placeholder="Пошук..." value={searchTerm} onChange={handleSearchChange}/>
            </div>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th className="text-center">ID</th>
                    <th className="text-center">Серійний номер</th>
                    <th className="text-center">Модель</th>
                    <th className="text-center">Тип</th>
                    <th className="text-center">Дата покупки</th>
                    <th className="text-center">Дії</th>
                </tr>
                </thead>
                <tbody>
                {currentEquipment.map(equipment => (
                    <tr key={equipment.equipmentId}>
                        <td>{equipment.equipmentId}</td>
                        <td>{equipment.serialNumber}</td>
                        <td>{equipment.model}</td>
                        <td>{equipment.type}</td>
                        <td>{new Date(equipment.purchaseDate).toLocaleDateString()}</td>
                        <td className="text-center">
                            <Button variant="info" className="me-2" onClick={() => handleEditClick(equipment)}>
                                Редагувати
                            </Button>
                            <Button variant="danger" onClick={() => handleDeleteClick(equipment.equipmentId)}>
                                Видалити
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </Table>

            <Pagination className="d-flex justify-content-center mt-4">
                <Pagination.Prev onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1}>
                    Попередня
                </Pagination.Prev>
                {[...Array(totalPages)].map((_, i) => (
                    <Pagination.Item key={i + 1} active={i + 1 === currentPage} onClick={() => handlePageChange(i + 1)}>
                        {i + 1}
                    </Pagination.Item>
                ))}
                <Pagination.Next onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages}>
                    Наступна
                </Pagination.Next>
            </Pagination>

            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Підтвердити видалення</Modal.Title>
                </Modal.Header>
                <Modal.Body>Ви впевнені, що хочете видалити це обладнання?</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Скасувати
                    </Button>
                    <Button variant="danger" onClick={handleDeleteEquipment}>
                        Видалити
                    </Button>
                </Modal.Footer>
            </Modal>

            <NewEquipmentModal show={showNewEquipmentModal} handleClose={() => setShowNewEquipmentModal(false)}
                               onEquipmentAdded={() => setReloadEquipment(prev => !prev)} />

            <EditEquipmentModal show={showEditEquipmentModal} handleClose={() => setShowEditEquipmentModal(false)}
                                selectedEquipment={selectedEquipment} onEquipmentUpdated={() => setReloadEquipment(prev => !prev)} />

        </Container>
    );
};

export default EquipmentTable;
