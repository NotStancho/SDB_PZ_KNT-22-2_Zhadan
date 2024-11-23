import React from 'react';
import {Navigate, Route, Routes} from 'react-router-dom';
import RegistrationPage from "./pages/RegistrationPage";
import LoginPage from "./pages/LoginPage";
import TokenCheck from "./components/TokenCheck";
import ClientHomePage from "./pages/ClientHomePage";
import ManagerHomePage from "./pages/ManagerHomePage";
import ServiceEngineerHomePage from "./pages/ServiceEngineerHomePage";
import AdminHomePage from "./pages/AdminHomePage";

const App = () => {
  return (
      <Routes>
        <Route path="/" element={<Navigate to="/register" />} />
        <Route path="/register" element={<RegistrationPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/client-home" element={<ClientHomePage/>} />
        <Route path="/manager-home" element={<ManagerHomePage />} />
        <Route path="/service_engineer-home" element={<ServiceEngineerHomePage />} />
        <Route path="/admin-home" element={<AdminHomePage />} />
        <Route path="/token-check" element={<TokenCheck />} />
      </Routes>
  );
};

export default App;
