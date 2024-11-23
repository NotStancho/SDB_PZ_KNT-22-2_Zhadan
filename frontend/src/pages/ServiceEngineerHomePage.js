import React, { useState } from 'react';
import LogoutButton from '../components/LogoutButton';
import ClaimsTable from "../components/ClaimsTable";
import EditProfileModal from "../components/EditProfileModal";
import {Button, Container} from "react-bootstrap";

const ServiceEngineerHomePage = () => {
    const [reloadClaims, setReloadClaims] = useState(false);
    const [showEditProfile, setShowEditProfile] = useState(false);

    return (
        <Container className="mt-5 p-4 rounded" style={{ backgroundColor: 'rgba(255, 255, 255, 0.8)', boxShadow: '0 0 15px rgba(0, 0, 0, 0.2)' }}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <Button variant="secondary" onClick={() => setShowEditProfile(true)}>
                    Редагувати профіль
                </Button>
                <h1>Ласкаво просимо до сторінки сервісного інженера!</h1>
                <LogoutButton/>
            </div>

            <ClaimsTable role="ROLE_SERVICE_ENGINEER" reloadClaims={reloadClaims} onReload={() => setReloadClaims(prev => !prev)}/>

            <EditProfileModal
                show={showEditProfile}
                handleClose={() => setShowEditProfile(false)}
            />
        </Container>
    );
};

export default ServiceEngineerHomePage;
