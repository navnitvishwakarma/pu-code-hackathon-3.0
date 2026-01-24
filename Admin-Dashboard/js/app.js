const API_BASE = 'https://go-saathi.vercel.app/api/admin';

document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
    loadTab('home');
});

function loadTab(tab) {
    const titles = {
        'home': 'Dashboard Overview',
        'drivers': 'Manage Drivers',
        'incidents': 'Incident Reports',
        'maintenance': 'Vehicle Maintenance',
        'communication': 'Communication Center'
    };
    document.getElementById('page-title').innerText = titles[tab];

    // Highlight Nav
    document.querySelectorAll('.nav-item').forEach(btn => {
        if (btn.onclick.toString().includes(tab)) {
            btn.classList.add('bg-blue-50', 'text-blue-600');
        } else {
            btn.classList.remove('bg-blue-50', 'text-blue-600');
        }
    });

    const content = document.getElementById('content-area');
    content.innerHTML = '<div class="flex justify-center p-12"><div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div></div>';

    switch (tab) {
        case 'home': renderHome(); break;
        case 'drivers': renderDrivers(); break;
        case 'incidents': renderIncidents(); break;
        case 'maintenance': renderMaintenance(); break;
        case 'communication': renderCommunication(); break;
    }
}

async function renderHome() {
    // Fetch stats in parallel
    const [incidents, drivers, maintenance] = await Promise.all([
        fetch(`${API_BASE}/incidents`).then(r => r.json()),
        fetch(`${API_BASE}/drivers`).then(r => r.json()),
        fetch(`${API_BASE}/maintenance`).then(r => r.json())
    ]);

    const activeIncidents = incidents.data ? incidents.data.filter(i => i.status === 'Open').length : 0;
    const activeDrivers = drivers.data ? drivers.data.length : 0;
    const overdueMaint = maintenance.data ? maintenance.data.filter(m => m.status === 'Overdue').length : 0;

    const html = `
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <div class="flex justify-between items-center mb-4">
                    <div class="p-2 bg-blue-100 rounded-lg text-blue-600"><i data-lucide="users" class="w-6 h-6"></i></div>
                    <span class="text-sm font-medium text-gray-400">Total Drivers</span>
                </div>
                <h3 class="text-3xl font-bold text-gray-900">${activeDrivers}</h3>
            </div>
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <div class="flex justify-between items-center mb-4">
                    <div class="p-2 bg-red-100 rounded-lg text-red-600"><i data-lucide="alert-triangle" class="w-6 h-6"></i></div>
                    <span class="text-sm font-medium text-gray-400">Active Incidents</span>
                </div>
                <h3 class="text-3xl font-bold text-gray-900">${activeIncidents}</h3>
            </div>
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <div class="flex justify-between items-center mb-4">
                    <div class="p-2 bg-yellow-100 rounded-lg text-yellow-600"><i data-lucide="wrench" class="w-6 h-6"></i></div>
                    <span class="text-sm font-medium text-gray-400">Maintenance Overdue</span>
                </div>
                <h3 class="text-3xl font-bold text-gray-900">${overdueMaint}</h3>
            </div>
        </div>
        
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center">
            <h3 class="text-lg font-semibold text-gray-800 mb-2">Welcome to Authority Panel</h3>
            <p class="text-gray-500">Select a module from the sidebar to manage data.</p>
        </div>
    `;
    document.getElementById('content-area').innerHTML = html;
    lucide.createIcons();
}

// --- DRIVERS ---
async function renderDrivers() {
    const res = await fetch(`${API_BASE}/drivers`);
    const data = await res.json();
    const drivers = data.data || [];

    let html = `
        <div class="flex justify-end mb-6">
            <button onclick="openAddDriverModal()" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors">
                <i data-lucide="plus" class="w-4 h-4"></i> Add Driver
            </button>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
            <table class="w-full text-left">
                <thead class="bg-gray-50 border-b border-gray-200 text-gray-500 text-xs uppercase font-semibold">
                    <tr>
                        <th class="px-6 py-4">ID</th>
                        <th class="px-6 py-4">Name</th>
                        <th class="px-6 py-4">Rating</th>
                        <th class="px-6 py-4">Punctuality</th>
                        <th class="px-6 py-4">Violations</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                    ${drivers.map(d => `
                        <tr class="hover:bg-gray-50 transition-colors">
                            <td class="px-6 py-4 font-mono text-xs text-gray-500">${d.driverId}</td>
                            <td class="px-6 py-4 font-medium text-gray-900">${d.name}</td>
                            <td class="px-6 py-4 flex items-center gap-1 text-yellow-600 font-bold"><i data-lucide="star" class="w-3 h-3 fill-current"></i> ${d.rating}</td>
                            <td class="px-6 py-4">${d.punctualityScore}%</td>
                            <td class="px-6 py-4 text-red-500">${d.violations.speed + d.violations.harshBraking}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
    document.getElementById('content-area').innerHTML = html;
    lucide.createIcons();
}

function openAddDriverModal() {
    openModal('Add New Driver', `
        <form onsubmit="submitDriver(event)" class="space-y-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Driver ID</label>
                <input name="driverId" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                <input name="name" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
            </div>
            <button type="submit" class="w-full bg-blue-600 text-white rounded-lg py-2.5 font-medium hover:bg-blue-700">Save Driver</button>
        </form>
    `);
}

async function submitDriver(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());
    // Defaults
    data.rating = 5.0;
    data.punctualityScore = 100;

    await fetch(`${API_BASE}/drivers`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    closeModal();
    renderDrivers();
}

// --- INCIDENTS ---
async function renderIncidents() {
    const res = await fetch(`${API_BASE}/incidents`);
    const data = await res.json();
    const items = data.data || [];

    let html = `
        <div class="flex justify-end mb-6">
            <button onclick="openAddIncidentModal()" class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors">
                <i data-lucide="alert-triangle" class="w-4 h-4"></i> Report Incident
            </button>
        </div>
        <div class="space-y-4">
            ${items.map(i => `
                <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-200 flex justify-between items-start">
                    <div>
                        <div class="flex items-center gap-3 mb-1">
                            <span class="px-2 py-0.5 rounded text-xs font-bold ${i.severity === 'Critical' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'}">${i.severity}</span>
                            <h4 class="font-semibold text-gray-900">${i.type}</h4>
                        </div>
                        <p class="text-sm text-gray-600 mb-2">${i.description}</p>
                        <div class="flex items-center gap-4 text-xs text-gray-400">
                            <span><i data-lucide="map-pin" class="w-3 h-3 inline mb-0.5"></i> ${i.location}</span>
                            <span><i data-lucide="user" class="w-3 h-3 inline mb-0.5"></i> ${i.driver}</span>
                            <span>${new Date(i.timestamp).toLocaleDateString()}</span>
                        </div>
                    </div>
                    <div class="text-right">
                        <span class="text-sm font-medium ${i.status === 'Open' ? 'text-red-500' : 'text-green-500'}">${i.status}</span>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
    document.getElementById('content-area').innerHTML = html;
    lucide.createIcons();
}

function openAddIncidentModal() {
    openModal('Report Incident', `
        <form onsubmit="submitIncident(event)" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Type</label>
                    <select name="type" class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                        <option>Accident</option>
                        <option>Breakdown</option>
                        <option>Delay</option>
                    </select>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Severity</label>
                    <select name="severity" class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                        <option>Low</option>
                        <option>Medium</option>
                        <option>Critical</option>
                    </select>
                </div>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea name="description" rows="3" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm"></textarea>
            </div>
            <div class="grid grid-cols-2 gap-4">
                <input name="driver" placeholder="Driver Name" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                <input name="location" placeholder="Location" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
            </div>
            <button type="submit" class="w-full bg-red-600 text-white rounded-lg py-2.5 font-medium hover:bg-red-700">Submit Report</button>
        </form>
    `);
}

async function submitIncident(e) {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target));
    data.status = 'Open';
    await fetch(`${API_BASE}/incidents`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
    closeModal();
    renderIncidents();
}

// --- MAINTENANCE ---
async function renderMaintenance() {
    const res = await fetch(`${API_BASE}/maintenance`);
    const data = await res.json();
    const items = data.data || [];

    let html = `
        <div class="flex justify-end mb-6">
            <button onclick="openAddMaintenanceModal()" class="bg-gray-800 hover:bg-gray-900 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors">
                <i data-lucide="plus" class="w-4 h-4"></i> Log Maintenance
            </button>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
            <table class="w-full text-left">
                <thead class="bg-gray-50 border-b border-gray-200 text-gray-500 text-xs uppercase font-semibold">
                    <tr>
                        <th class="px-6 py-4">Bus ID</th>
                        <th class="px-6 py-4">Last Service</th>
                        <th class="px-6 py-4">Next Due</th>
                        <th class="px-6 py-4">Status</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-100">
                    ${items.map(m => `
                        <tr class="hover:bg-gray-50 transition-colors">
                            <td class="px-6 py-4 font-medium">${m.busId}</td>
                            <td class="px-6 py-4 text-gray-500">${new Date(m.lastServiceDate).toLocaleDateString()}</td>
                            <td class="px-6 py-4 text-gray-900 font-medium">${new Date(m.nextServiceDate).toLocaleDateString()}</td>
                            <td class="px-6 py-4">
                                <span class="px-2 py-1 rounded text-xs font-bold ${m.status === 'Overdue' ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'}">
                                    ${m.status}
                                </span>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;
    document.getElementById('content-area').innerHTML = html;
    lucide.createIcons();
}

function openAddMaintenanceModal() {
    openModal('Log Maintenance', `
        <form onsubmit="submitMaintenance(event)" class="space-y-4">
            <input name="busId" placeholder="Bus ID (e.g., bus001)" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="text-xs text-gray-500">Last Service</label>
                    <input type="date" name="lastServiceDate" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                </div>
                <div>
                    <label class="text-xs text-gray-500">Next Due</label>
                    <input type="date" name="nextServiceDate" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                </div>
            </div>
            <select name="status" class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                <option>Good</option>
                <option>Due Soon</option>
                <option>Overdue</option>
            </select>
            <button type="submit" class="w-full bg-gray-800 text-white rounded-lg py-2.5 font-medium hover:bg-gray-900">Save Log</button>
        </form>
    `);
}

async function submitMaintenance(e) {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target));
    await fetch(`${API_BASE}/maintenance`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
    closeModal();
    renderMaintenance();
}

// --- COMMUNICATION ---
async function renderCommunication() {
    const res = await fetch(`${API_BASE}/communication`);
    const data = await res.json();
    const items = data.data || [];

    let html = `
        <div class="flex justify-end mb-6">
            <button onclick="openAddCommunicationModal()" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors">
                <i data-lucide="send" class="w-4 h-4"></i> Send Alert
            </button>
        </div>
        <div class="grid gap-4">
            ${items.map(msg => `
                <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-200">
                    <div class="flex justify-between items-start mb-2">
                        <h4 class="font-bold text-gray-900">${msg.title}</h4>
                        <span class="text-xs text-gray-400">${new Date(msg.timestamp).toLocaleDateString()}</span>
                    </div>
                    <p class="text-gray-600 text-sm mb-3">${msg.message}</p>
                    <div class="flex items-center gap-2">
                        <span class="px-2 py-1 bg-blue-50 text-blue-600 text-xs rounded font-medium">To: ${msg.target}</span>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
    document.getElementById('content-area').innerHTML = html;
    lucide.createIcons();
}

function openAddCommunicationModal() {
    openModal('Send Alert', `
        <form onsubmit="submitCommunication(event)" class="space-y-4">
            <input name="title" placeholder="Title" required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
            <textarea name="message" rows="3" placeholder="Message content..." required class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm"></textarea>
            <select name="target" class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm">
                <option>All</option>
                <option>Drivers</option>
                <option>Passengers</option>
            </select>
            <input type="hidden" name="sender" value="Authority">
            <button type="submit" class="w-full bg-blue-600 text-white rounded-lg py-2.5 font-medium hover:bg-blue-700">Broadcast</button>
        </form>
    `);
}

async function submitCommunication(e) {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target));
    await fetch(`${API_BASE}/communication`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
    closeModal();
    renderCommunication();
}

// --- UTILS ---
function openModal(title, html) {
    document.getElementById('modal-title').innerText = title;
    document.getElementById('modal-body').innerHTML = html;
    document.getElementById('modal-overlay').classList.remove('hidden');
    document.getElementById('modal-overlay').classList.add('flex');
}

function closeModal() {
    document.getElementById('modal-overlay').classList.add('hidden');
    document.getElementById('modal-overlay').classList.remove('flex');
}

function refreshData() {
    const activeTab = document.querySelector('.nav-item.bg-blue-50');
    if (activeTab) activeTab.click();
}
