import React, { useEffect, useState } from 'react';
import axios from 'axios';

const TokenCheck = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        if (token) {
            axios.get('http://localhost:8080/api/users/me', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
                .then(response => {
                    setUser(response.data);
                    setLoading(false);
                })
                .catch(error => {
                    console.error('Помилка авторизації:', error);
                    setLoading(false);
                });
        } else {
            setLoading(false);
        }
    }, []);

    if (loading) {
        return <p>Завантаження...</p>;
    }

    if (user) {
        return (
            <div>
                <h2>Ви увійшли як: {user.email}</h2>
                <p>Роль: {user.role}</p>
            </div>
        );
    } else {
        return <p>Користувач не авторизований.</p>;
    }
};

export default TokenCheck;
