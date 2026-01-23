import React from 'react';

const Header = () => {
    return (
        <header className="flex items-center justify-between border-b border-[#23482f] bg-surface-dark px-6 py-3 z-30 shrink-0 h-18">
            {/* Logo */}
            <div className="flex items-center gap-4">
                <div className="flex items-center justify-center size-10 rounded-lg bg-[#23482f] text-primary">
                    <span className="material-symbols-outlined" style={{ fontSize: '28px' }}>directions_bus</span>
                </div>
                <div>
                    <h1 className="text-white text-lg font-bold leading-tight tracking-tight">CityBus Command Center</h1>
                    <p className="text-[#92c9a4] text-xs font-medium">New Delhi Region • Live Operations</p>
                </div>
            </div>

            {/* Stats Widget */}
            <div className="hidden xl:flex items-center gap-6 bg-[#193322] px-6 py-2 rounded-xl border border-[#23482f]/50">
                <StatItem label="Active Buses" value="842" trend="▲ 2%" color="text-white" />
                <StatItem label="Avg Speed" value="24 km/h" trend="▲ 1.5%" color="text-white" />
                <StatItem label="Emergencies" value="3" sub="Alerts" color="text-red-500" />
            </div>

            {/* User Actions */}
            <div className="flex items-center gap-4">
                <button className="relative p-2 text-[#92c9a4] hover:text-white hover:bg-[#23482f] rounded-full transition-colors">
                    <span className="material-symbols-outlined">notifications</span>
                    <span className="absolute top-2 right-2 size-2 bg-red-500 rounded-full border border-surface-dark"></span>
                </button>
                <div className="h-8 w-px bg-[#23482f]"></div>
                <div className="flex items-center gap-3">
                    <div className="text-right hidden md:block">
                        <p className="text-white text-sm font-medium">Control Room 1</p>
                        <p className="text-[#92c9a4] text-xs">Admin</p>
                    </div>
                    <div className="size-10 rounded-full bg-cover bg-center border-2 border-[#23482f]"
                        style={{ backgroundImage: 'url("https://lh3.googleusercontent.com/aida-public/AB6AXuCM0z7GPjKSGhZwnMPN5m6AcLMqq67J9X7x9oUxU0dOW25tlKAg8llOBuuhmoyCKlrYzZKXf-_h-9ut6kp2DmDtFirMt6m2cfGWSr3rKCpLWvuWd2xa265bI9tgHlsO73EpJ0t0jTgnJnK3Cl3FmOfgQmWy4vbp-T2821lbcpUr3ZtkPDgvsHvfPJgp9F7pE_JL5ijLbbIPbHe9ZeqwSLr6O7IKyLkL6h-hoNMHBw8F0a2p0NmR3TEruSkX2OQIecvHhi-xiopqo-c")' }}></div>
                </div>
            </div>
        </header>
    );
};

const StatItem = ({ label, value, trend, sub, color }) => (
    <div className="flex items-center gap-3 border-r border-[#23482f] last:border-0 pr-6 last:pr-0">
        <div className="flex flex-col">
            <span className="text-[#92c9a4] text-xs font-medium uppercase tracking-wider">{label}</span>
            <div className="flex items-end gap-1.5">
                <span className={`${color} text-xl font-bold leading-none`}>{value}</span>
                {trend && <span className="text-primary text-xs font-medium mb-0.5">{trend}</span>}
                {sub && <span className="text-[#92c9a4] text-xs font-normal mb-0.5">{sub}</span>}
            </div>
        </div>
    </div>
);

export default Header;
