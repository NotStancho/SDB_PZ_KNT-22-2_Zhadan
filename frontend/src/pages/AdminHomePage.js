import React, { useState } from 'react';
import { Tabs, Tab, Button, Container } from 'react-bootstrap';
import EditProfileModal from "../components/EditProfileModal";
import LogoutButton from "../components/LogoutButton";
import ClaimsTable from '../components/ClaimsTable';
import UsersTable from "../components/UsersTable";
import EquipmentTable from '../components/EquipmentTable';

const AdminHomePage = () => {
    const [reloadClaims, setReloadClaims] = useState(false);
    const [reloadUsers, setReloadUsers] = useState(false);
    const [reloadEquipment, setReloadEquipment] = useState(false);
    const [showEditProfile, setShowEditProfile] = useState(false);

    const handleReload = () => {
        setReloadClaims(prev => !prev);
        setReloadUsers(prev => !prev);
        setReloadEquipment(prev => !prev);
    };

    return (
        <Container className="mt-5 p-4 rounded" style={{ backgroundColor: 'rgba(255, 255, 255, 0.8)', boxShadow: '0 0 15px rgba(0, 0, 0, 0.2)' }}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <Button  onClick={() => setShowEditProfile(true)}>
                    Редагувати профіль
                </Button>
                <h1 className="text-center">Адміністраторська сторінка</h1>
                <LogoutButton/>
            </div>

            <Tabs defaultActiveKey="users" id="admin-tabs" className="bg-dark text-light p-2 rounded">
                <Tab eventKey="users" title="Користувачі" tabClassName="bg-dark text-light rounded-pill">
                    <UsersTable reloadUsers={reloadUsers} onReload={handleReload} />
                </Tab>

                <Tab eventKey="equipment" title="Обладнання" tabClassName="bg-dark text-light rounded-pill">
                    <EquipmentTable reloadEquipment={reloadEquipment} onReload={handleReload} />
                </Tab>

                <Tab eventKey="claims" title="Заявки" tabClassName="bg-dark text-light rounded-pill">
                    <ClaimsTable role="ROLE_ADMIN" reloadClaims={reloadClaims} onReload={handleReload}/>
                </Tab>
            </Tabs>
            <EditProfileModal show={showEditProfile} handleClose={() => setShowEditProfile(false)}/>
        </Container>
    );
};
export default AdminHomePage;