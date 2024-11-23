import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {Table, Button, Dropdown, Container, Pagination, FormControl, Modal} from 'react-bootstrap';
import ClaimDetailsModal from "./ClaimDetailsModal";
import ClaimHistoryModal from "./ClaimHistoryModal";
import NewClaimModal from "./NewClaimModal";

const ClaimsTable = ({ role, reloadClaims, onReload }) => {
    const [claims, setClaims] = useState([]);
    const [statuses, setStatuses] = useState([]);
    const [statusFilter, setStatusFilter] = useState("Всі");
    const [searchTerm, setSearchTerm] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const claimsPerPage = 6;

    const [showNewClaimModal, setShowNewClaimModal] = useState(false);

    const [showDetailsModal, setShowDetailsModal] = useState(false);
    const [selectedClaimId, setSelectedClaimId] = useState(null);

    const [showHistoryModal, setShowHistoryModal] = useState(false);
    const [selectedHistoryClaimId, setSelectedHistoryClaimId] = useState(null);

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [claimToDelete, setClaimToDelete] = useState(null);

    useEffect(() => {
        const fetchClaims = async () => {
            const token = localStorage.getItem('jwtToken');
            if (token) {
                try {
                    let url = role === 'ROLE_CLIENT'
                        ? 'http://localhost:8080/api/claims/client'
                        : 'http://localhost:8080/api/claims/all';

                    const response = await axios.get(url, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    setClaims(response.data);
                } catch (error) {
                    console.error('Помилка під час отримання заявок:', error);
                }
            }
        };
        fetchClaims();
    }, [role, reloadClaims]);

    useEffect(() => {
        const fetchStatuses = async () => {
            const token = localStorage.getItem('jwtToken');
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
        };

        fetchStatuses();
    }, []);

    const handleDeleteClaim = async () => {
        if (!claimToDelete) return;

        const token = localStorage.getItem('jwtToken');
        try {
            const response = await axios.delete(`http://localhost:8080/api/claims/${claimToDelete}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.status === 200) {
                setShowDeleteModal(false);
                onReload(); // Оновлення таблиці після видалення
            }
        } catch (error) {
            console.error('Помилка при видаленні заявки:', error);
        }
    };

    const filteredClaims = claims.filter(claim =>
        (statusFilter === "Всі" || claim.status.name === statusFilter) &&
        (claim.defectDescription.toLowerCase().includes(searchTerm.toLowerCase()) ||
            claim.equipment.type.toLowerCase().includes(searchTerm.toLowerCase()) ||
            claim.equipment.model.toLowerCase().includes(searchTerm.toLowerCase()) ||
            claim.status.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            String(claim.claimId).includes(searchTerm))
    );

    const indexOfLastClaim = currentPage * claimsPerPage;
    const indexOfFirstClaim = indexOfLastClaim - claimsPerPage;
    const currentClaims = filteredClaims.slice(indexOfFirstClaim, indexOfLastClaim);

    const totalPages = Math.ceil(filteredClaims.length / claimsPerPage);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    const handleFilterChange = (status) => {
        setStatusFilter(status);
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setCurrentPage(1);
    };

    const handleDetailsClick = (claimId) => {
        setSelectedClaimId(claimId);
        setShowDetailsModal(true);
    };

    const handleHistoryClick = (claimId) => {
        setSelectedHistoryClaimId(claimId);
        setShowHistoryModal(true);
    };

    const handleDeleteClick = (claimId) => {
        setClaimToDelete(claimId);
        setShowDeleteModal(true);
    };

    const handleNewClaimSuccess = () => {
        setShowNewClaimModal(false);
        onReload();
    };

    return (
        <Container className="mt-3 p-4"
                   style={{backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 0 15px rgba(0, 0, 0, 0.1)'}}>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Заявки</h2>
                {(role === 'ROLE_ADMIN' || role === 'ROLE_CLIENT') && (
                    <Button variant="primary" onClick={() => setShowNewClaimModal(true)}>
                        Подати заявку
                    </Button>
                )}
            </div>

            <div className="mb-3 d-flex justify-content-end">
                <FormControl type="text" placeholder="Пошук..." value={searchTerm} onChange={handleSearchChange}/>
            </div>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th className="text-center">ID</th>
                    <th className="text-center">Опис дефекту</th>
                    <th className="text-center">Тип обладнання</th>
                    <th className="text-center">Модель обладнання</th>
                    <th className="d-flex align-items-center justify-content-between">
                        <span>Статус</span>
                        <Dropdown className="ms-2">
                            <Dropdown.Toggle variant="outline-secondary" id="dropdown-basic">
                                {statusFilter}
                            </Dropdown.Toggle>
                            <Dropdown.Menu>
                                <Dropdown.Item onClick={() => handleFilterChange("Всі")}>Всі</Dropdown.Item>
                                {statuses.map(status => (
                                    <Dropdown.Item key={status.statusId}
                                                   onClick={() => handleFilterChange(status.name)}>
                                        {status.name}
                                    </Dropdown.Item>
                                ))}
                            </Dropdown.Menu>
                        </Dropdown>
                    </th>
                    <th className="text-center">Деталі</th>
                    <th className="text-center">Історія</th>
                    {role === 'ROLE_ADMIN' && <th className="text-center">Дії</th>}
                </tr>
                </thead>
                <tbody>
                {currentClaims.map((claim) => (
                    <tr key={claim.claimId}>
                        <td>{claim.claimId}</td>
                        <td>{claim.defectDescription}</td>
                        <td>{claim.equipment.type}</td>
                        <td>{claim.equipment.model}</td>
                        <td>{claim.status.name}</td>
                        <td className="text-center">
                            <Button variant="info" onClick={() => handleDetailsClick(claim.claimId)}>
                                Деталі
                            </Button>
                        </td>
                        <td className="text-center">
                            <Button variant="primary" onClick={() => handleHistoryClick(claim.claimId)}>
                                Історія
                            </Button>
                        </td>
                        {role === 'ROLE_ADMIN' && (
                            <td className="text-center">
                                <Button variant="danger" onClick={() => handleDeleteClick(claim.claimId)}>
                                    Видалити
                                </Button>
                            </td>
                        )}
                    </tr>
                ))}
                </tbody>
            </Table>

            <Pagination className="d-flex justify-content-center mt-4">
                <Pagination.Prev
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                >
                    Попередня
                </Pagination.Prev>
                {[...Array(totalPages)].map((_, i) => (
                    <Pagination.Item
                        key={i + 1}
                        active={i + 1 === currentPage}
                        onClick={() => handlePageChange(i + 1)}
                    >
                        {i + 1}
                    </Pagination.Item>
                ))}
                <Pagination.Next
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                >
                    Наступна
                </Pagination.Next>
            </Pagination>

            <NewClaimModal
                show={showNewClaimModal}
                handleClose={() => setShowNewClaimModal(false)}
                onSubmitSuccess={handleNewClaimSuccess}
            />

            {selectedClaimId && (
                <ClaimDetailsModal
                    show={showDetailsModal}
                    handleClose={() => setShowDetailsModal(false)}
                    claimId={selectedClaimId}
                    role={role}
                    onClaimUpdated={() => onReload()}
                />
            )}

            {selectedHistoryClaimId && (
                <ClaimHistoryModal
                    show={showHistoryModal}
                    handleClose={() => setShowHistoryModal(false)}
                    claimId={selectedHistoryClaimId}
                />
            )}

            {/* Модальне вікно для підтвердження видалення */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Підтвердити видалення</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Ви впевнені, що хочете видалити цю заявку?
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Скасувати
                    </Button>
                    <Button variant="danger" onClick={handleDeleteClaim}>
                        Видалити
                    </Button>
                </Modal.Footer>
            </Modal>

        </Container>
    );
};

export default ClaimsTable;
