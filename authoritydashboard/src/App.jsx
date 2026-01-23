import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';

function App() {
    return (
        <BrowserRouter>
            <div className="flex h-screen w-full bg-background-dark text-white font-display overflow-hidden">
                <Sidebar />
                <div className="flex flex-col flex-1 min-w-0">
                    <Header />
                    <Routes>
                        <Route path="/" element={<Dashboard />} />
                        {/* Placeholder for other routes */}
                        <Route path="/fleet" element={<div className="p-10 text-xl font-bold text-[#92c9a4]">Fleet Management - Coming Soon</div>} />
                        <Route path="/analytics" element={<div className="p-10 text-xl font-bold text-[#92c9a4]">Analytics - Coming Soon</div>} />
                    </Routes>
                </div>
            </div>
        </BrowserRouter>
    );
}

export default App;
