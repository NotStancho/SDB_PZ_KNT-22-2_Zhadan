import React, { useState } from 'react';
import LogoutButton from '../components/LogoutButton';
import ClaimsTable from "../components/ClaimsTable";
import NewClaimModal from '../components/NewClaimModal';
import EditProfileModal from "../components/EditProfileModal";
import {Button, Container} from "react-bootstrap";

const ClientHomePage = () => {
    const [reloadClaims, setReloadClaims] = useState(false);
    const [showEditProfile, setShowEditProfile] = useState(false);

    const handleReload = () => {
        setReloadClaims(prev => !prev);
    };

    return (
        <Container className="mt-5 p-4 rounded" style={{ backgroundColor: 'rgba(255, 255, 255, 0.8)', boxShadow: '0 0 15px rgba(0, 0, 0, 0.2)' }}>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <Button variant="secondary" onClick={() => setShowEditProfile(true)}>
                    Редагувати профіль
                </Button>
                <h1>Ласкаво просимо до особистого кабінету!</h1>
                <LogoutButton/>
            </div>

            <ClaimsTable role="ROLE_CLIENT" reloadClaims={reloadClaims} onReload={handleReload} />

            <EditProfileModal
                show={showEditProfile}
                handleClose={() => setShowEditProfile(false)}
            />
        </Container>
    );
};

export default ClientHomePage;