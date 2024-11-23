import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {Table, Button, Modal, Form, Container, Pagination, FormControl} from 'react-bootstrap';
import NewUserModal from './NewUserModal';
import EditProfileModal from "./EditProfileModal";

const UsersTable = () => {
    const [users, setUsers] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [reloadUsers, setReloadUsers] = useState(false);
    const [showNewUserModal, setShowNewUserModal] = useState(false);
    const [showEditUserModal, setShowEditUserModal] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [userToDelete, setUserToDelete] = useState(null);

    const usersPerPage = 6;
    const [currentPage, setCurrentPage] = useState(1);

    useEffect(() => {
        const fetchUsers = async () => {
            const token = localStorage.getItem('jwtToken');
            try {
                const response = await axios.get('http://localhost:8080/api/users/all', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setUsers(response.data);
            } catch (error) {
                console.error('Помилка при отриманні користувачів', error);
            }
        };
        fetchUsers();
    }, [reloadUsers]);

    const handleDeleteUser = async () => {
        if (!userToDelete) return;

        const token = localStorage.getItem('jwtToken');
        try {
            const response = await axios.delete(`http://localhost:8080/api/users/${userToDelete}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (response.status === 200) {
                setShowDeleteModal(false);
                setReloadUsers(prev => !prev);
            }
        } catch (error) {
            console.error('Помилка при видаленні користувача', error);
        }
    };

    const handleEditClick = (user) => {
        setSelectedUser(user);
        setShowEditUserModal(true);
    };

    const handleDeleteClick = (userId) => {
        setUserToDelete(userId);
        setShowDeleteModal(true);
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setCurrentPage(1);
    };

    const filteredUsers = users.filter(user =>
        user.firstname.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.lastname.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.role.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const indexOfLastUser = currentPage * usersPerPage;
    const indexOfFirstUser = indexOfLastUser - usersPerPage;
    const currentUsers = users.slice(indexOfFirstUser, indexOfLastUser);
    const totalPages = Math.ceil(users.length / usersPerPage);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    return (
        <Container className="mt-3 p-4"
                   style={{backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 0 15px rgba(0, 0, 0, 0.1)'}}>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Користувачі</h2>
                <Button variant="primary" onClick={() => setShowNewUserModal(true)}>
                    Додати користувача
                </Button>
            </div>

            <div className="mb-3 d-flex justify-content-end">
                <FormControl type="text" placeholder="Пошук..." value={searchTerm} onChange={handleSearchChange}/>
            </div>

            <Table striped bordered hover>
                <thead>
                <tr>
                    <th className="text-center">ID</th>
                    <th className="text-center">Ім'я</th>
                    <th className="text-center">Прізвище</th>
                    <th className="text-center">Email</th>
                    <th className="text-center">Телефон</th>
                    <th className="text-center">Роль</th>
                    <th className="text-center">Дії</th>
                </tr>
                </thead>
                <tbody>
                {currentUsers.map(user => (
                    <tr key={user.userId}>
                        <td>{user.userId}</td>
                        <td>{user.firstname}</td>
                        <td>{user.lastname}</td>
                        <td>{user.email}</td>
                        <td>{user.phone}</td>
                        <td>{user.role}</td>
                        <td>
                            <Button variant="info" className="me-2" onClick={() => handleEditClick(user)}>
                                Редагувати
                            </Button>
                            <Button variant="danger" onClick={() => handleDeleteClick(user.userId)}>
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

            {/* Модальне вікно для підтвердження видалення */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Підтвердити видалення</Modal.Title>
                </Modal.Header>
                <Modal.Body>Ви впевнені, що хочете видалити цього користувача?</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Скасувати
                    </Button>
                    <Button variant="danger" onClick={handleDeleteUser}>
                        Видалити
                    </Button>
                </Modal.Footer>
            </Modal>

            <NewUserModal show={showNewUserModal} handleClose={() => setShowNewUserModal(false)}
                          onUserAdded={() => setReloadUsers(prev => !prev)} />

            <EditProfileModal show={showEditUserModal} handleClose={() => setShowEditUserModal(false)}
                              selectedUser={selectedUser} isAdmin={true}
                              onUserUpdated={() => setReloadUsers(prev => !prev)} />

        </Container>
    );
};

export default UsersTable;
