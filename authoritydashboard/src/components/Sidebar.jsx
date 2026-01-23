import React from 'react';
import { NavLink } from 'react-router-dom';

const Sidebar = () => {
    const navItems = [
        { name: 'Live Map', icon: 'map', path: '/', activeClass: 'bg-[#23482f] text-white border-l-4 border-primary shadow-lg shadow-black/20' },
        { name: 'Fleet Status', icon: 'directions_bus', path: '/fleet' },
        { name: 'Analytics', icon: 'monitoring', path: '/analytics' },
        { name: 'Incidents', icon: 'warning', path: '/incidents', badge: 3 },
        { name: 'Reports', icon: 'bar_chart', path: '/reports' }
    ];

    return (
        <aside className="w-80 bg-surface-dark border-r border-[#23482f] flex flex-col z-20 overflow-y-auto shrink-0 transition-all duration-300">
            <nav className="flex flex-col gap-1 p-4 pb-2">
                {navItems.map((item) => (
                    <NavLink
                        key={item.name}
                        to={item.path}
                        className={({ isActive }) =>
                            `flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors ${isActive
                                ? item.activeClass
                                : 'text-[#92c9a4] hover:bg-[#23482f]/50 hover:text-white'
                            }`
                        }
                    >
                        <span className={`material-symbols-outlined ${item.path === '/' ? 'text-primary' : ''}`}>
                            {item.icon}
                        </span>
                        <span className="text-sm font-medium">{item.name}</span>
                        {item.badge && (
                            <span className="ml-auto bg-red-500/20 text-red-400 text-xs px-2 py-0.5 rounded-full font-bold">
                                {item.badge}
                            </span>
                        )}
                    </NavLink>
                ))}
            </nav>

            <div className="h-px w-full bg-[#23482f] my-2"></div>

            {/* Filters (Mock functionality for UI demo) */}
            <div className="p-4 flex flex-col gap-5">
                <div className="flex items-center justify-between">
                    <h3 className="text-white text-xs font-bold uppercase tracking-wider text-opacity-80">Map Filters</h3>
                    <button className="text-primary text-xs hover:underline">Reset</button>
                </div>

                {/* City Filter */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-[#92c9a4] text-xs font-medium">Region / City</label>
                    <div className="relative">
                        <select className="w-full bg-[#193322] text-white text-sm border border-[#326744] rounded-lg px-3 py-2.5 focus:ring-1 focus:ring-primary focus:border-primary appearance-none outline-none">
                            <option>New Delhi</option>
                            <option>Mumbai</option>
                            <option>Bengaluru</option>
                        </select>
                        <span className="material-symbols-outlined absolute right-3 top-2.5 text-[#92c9a4] pointer-events-none text-lg">expand_more</span>
                    </div>
                </div>

                {/* Depot Filter */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-[#92c9a4] text-xs font-medium">Bus Depot</label>
                    <div className="relative">
                        <select className="w-full bg-[#193322] text-white text-sm border border-[#326744] rounded-lg px-3 py-2.5 focus:ring-1 focus:ring-primary focus:border-primary appearance-none outline-none">
                            <option>All Depots</option>
                            <option>Rajghat Depot</option>
                            <option>Mayapuri Depot</option>
                        </select>
                        <span className="material-symbols-outlined absolute right-3 top-2.5 text-[#92c9a4] pointer-events-none text-lg">expand_more</span>
                    </div>
                </div>

                {/* Fleet List Sample */}
                <div className="mt-4">
                    <h3 className="text-white text-xs font-bold uppercase tracking-wider text-opacity-80 mb-3">Filtered Buses (4)</h3>
                    <div className="flex flex-col gap-2">
                        <FleetItem id="DL-1PC-1234" route="534" status="safe" color="bg-primary" />
                        <FleetItem id="DL-1PC-5678" route="419" status="busy" color="bg-warning" />
                        <FleetItem id="DL-1PC-9988" route="202" status="full" color="bg-red-500" />
                    </div>
                </div>
            </div>
        </aside>
    );
};

const FleetItem = ({ id, route, color }) => (
    <div className="flex items-center justify-between p-3 rounded-lg bg-[#193322] border border-[#23482f] hover:border-primary/50 cursor-pointer group transition-all">
        <div className="flex items-center gap-3">
            <div className={`size-2 rounded-full ${color} shadow-lg`}></div>
            <div>
                <p className="text-sm font-bold text-white group-hover:text-primary transition-colors">{id}</p>
                <p className="text-xs text-[#92c9a4]">Route {route}</p>
            </div>
        </div>
        <span className="material-symbols-outlined text-[#92c9a4] group-hover:translate-x-1 transition-transform text-sm">chevron_right</span>
    </div>
);

export default Sidebar;
