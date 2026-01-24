// Main Script for GoSaarthi Admin

// Wait for DOM to load
document.addEventListener('DOMContentLoaded', () => {
    // Initialize Icons
    lucide.createIcons();

    // Tab Switching Logic
    const tabTriggers = document.querySelectorAll('.tab-trigger');
    const tabContents = document.querySelectorAll('.tab-content');

    tabTriggers.forEach(trigger => {
        trigger.addEventListener('click', () => {
            // Remove active class from all triggers
            tabTriggers.forEach(t => {
                t.dataset.state = 'inactive';
                t.classList.remove('text-foreground', 'shadow-sm', 'bg-background');
                t.classList.add('text-muted-foreground');
            });

            // Add active class to clicked trigger
            trigger.dataset.state = 'active';
            trigger.classList.remove('text-muted-foreground');
            trigger.classList.add('text-foreground', 'shadow-sm', 'bg-background');

            // Hide all contents
            tabContents.forEach(content => {
                content.classList.remove('active');
            });

            // Show target content
            const targetId = trigger.dataset.target;
            const targetContent = document.getElementById(targetId);
            if (targetContent) {
                targetContent.classList.add('active');

                // Initialize specific tab logic if needed
                if (targetId === 'enhanced-dashboard') initDashboard();
                if (targetId === 'history') initHistory();
                if (targetId === 'analytics') initAnalytics();
                if (targetId === 'ai-prediction') initAIPrediction();
                if (targetId === 'assignments') initAssignments();
                if (targetId === 'users') initUsers();
                if (targetId === 'incidents') initIncidents();
                if (targetId === 'maintenance') initMaintenance();
                if (targetId === 'communication') initCommunication();
            }
        });
    });

    // ... existing initDashboard ...

    // ... (Existing code for Dashboard, Map, History, Drivers) ...

    // --- Real Data: Incidents ---
    async function initIncidents() {
        try {
            const response = await fetch('https://go-saathi.vercel.app/api/admin/incidents');
            const data = await response.json();
            if (data.success && data.data) {
                renderIncidents(data.data);
                updateIncidentStats(data.data);
            }
        } catch (e) { console.error("Incidents fetch failed", e); }
    }

    function updateIncidentStats(data) {
        const active = data.filter(i => i.status === 'Open').length;
        const critical = data.filter(i => i.status === 'Open' && i.severity === 'Critical').length;
        document.getElementById('inc-active').innerText = active;
        document.getElementById('inc-critical').innerText = critical;
    }

    function renderIncidents(data) {
        const container = document.getElementById('incidents-list');
        if (!container) return;

        container.innerHTML = data.map(inc => {
            const borderClass = inc.severity === 'Critical' ? 'border-l-4 border-l-red-500' :
                inc.severity === 'Medium' ? 'border-l-4 border-l-yellow-500' : 'border-l-4 border-l-blue-500';

            return `
            <div class="flex items-start justify-between p-4 border rounded-lg mb-3 ${borderClass} bg-card text-card-foreground shadow-sm">
                <div>
                    <h4 class="font-semibold">${inc.type} - ${inc.severity}</h4>
                    <p class="text-sm text-muted-foreground">${inc.description}</p>
                    <div class="flex items-center gap-2 mt-2 text-xs text-muted-foreground">
                        <span><i data-lucide="user" class="h-3 w-3 inline"></i> ${inc.driver}</span>
                        <span>•</span>
                        <span><i data-lucide="map-pin" class="h-3 w-3 inline"></i> ${inc.location}</span>
                        <span>•</span>
                        <span>${new Date(inc.timestamp).toLocaleDateString()}</span>
                    </div>
                </div>
                <span class="px-2 py-1 rounded text-xs font-semibold ${inc.status === 'Open' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}">
                    ${inc.status}
                </span>
            </div>
        `;
        }).join('');
        lucide.createIcons();
    }

    // --- Real Data: Maintenance ---
    async function initMaintenance() {
        try {
            const response = await fetch('https://go-saathi.vercel.app/api/admin/maintenance');
            const data = await response.json();
            if (data.success && data.data) {
                renderMaintenance(data.data);
                updateMaintenanceStats(data.data);
            }
        } catch (e) { console.error("Maintenance fetch failed", e); }
    }

    function updateMaintenanceStats(data) {
        const dueSoon = data.filter(m => m.status === 'Due Soon').length;
        const overdue = data.filter(m => m.status === 'Overdue').length;
        document.getElementById('maint-due-soon').innerText = dueSoon;
        document.getElementById('maint-overdue').innerText = overdue;
    }

    function renderMaintenance(data) {
        const tbody = document.getElementById('maintenance-table-body');
        if (!tbody) return;

        tbody.innerHTML = data.map(record => {
            const statusClass = record.status === 'Good' ? 'text-green-600' :
                record.status === 'Overdue' ? 'text-red-600 font-bold' : 'text-yellow-600';

            return `
            <tr class="border-b transition-colors hover:bg-muted/50">
                <td class="p-4 font-medium">${record.busId}</td>
                <td class="p-4">${new Date(record.lastServiceDate).toLocaleDateString()}</td>
                <td class="p-4">${new Date(record.nextServiceDate).toLocaleDateString()}</td>
                <td class="p-4">${record.mileage} km</td>
                <td class="p-4 ${statusClass}">${record.status}</td>
                <td class="p-4">--</td>
            </tr>
        `;
        }).join('');
    }

    // --- Real Data: Communication ---
    async function initCommunication() {
        try {
            const response = await fetch('https://go-saathi.vercel.app/api/admin/communication');
            const data = await response.json();
            if (data.success && data.data) {
                renderCommunication(data.data);
                // Updating total sent count based on fetched items
                document.getElementById('comm-total-sent').innerText = data.data.length;
                document.getElementById('comm-total-recipients').innerText = data.data.length * 50; // Estimate
            }
        } catch (e) { console.error("Communication fetch failed", e); }
    }

    function renderCommunication(data) {
        const container = document.getElementById('comm-list');
        if (!container) return;

        container.innerHTML = data.map(msg => `
        <div class="mb-4 p-4 border rounded-lg bg-card text-card-foreground shadow-sm">
            <div class="flex justify-between items-start mb-2">
                <h4 class="font-semibold">${msg.title}</h4>
                <span class="text-xs text-muted-foreground">${new Date(msg.timestamp).toLocaleDateString()}</span>
            </div>
            <p class="text-sm mb-2">${msg.message}</p>
            <div class="flex items-center gap-2 text-xs">
                <span class="bg-primary/10 text-primary px-2 py-1 rounded">To: ${msg.target}</span>
                <span class="text-muted-foreground">From: ${msg.sender}</span>
            </div>
        </div>
    `).join('');
    }

    // --- Assignments & Users (Mock/Local for now as no Endpoints yet) ---
    function initAssignments() {
        console.log("Assignments initialized");
        // Could enable form logic here
    }

    function initUsers() {
        console.log("Users initialized");
        // Could fetch from /api/users if we added an endpoint list
    }

    initDashboard();

    // Modal Close Logic
    window.onclick = function (event) {
        const modal = document.getElementById('bus-modal');
        if (event.target == modal) {
            closeModal();
        }
    }
});

// --- Mock Data: Live Dashboard ---
// --- Mock Data: Live Dashboard ---
// Replaced with Real API Call
let mockBusesLive = [];

async function fetchActiveBuses() {
    try {
        // Fetch from deployed Vercel backend
        const response = await fetch('https://go-saathi.vercel.app/api/buses');
        const data = await response.json();
        if (data.success) {
            mockBusesLive = data.buses.map(bus => ({
                id: bus.busId,
                busNumber: `BUS-${bus.busId.replace('bus', '')}`,
                route: "Route 1A - Vadodara to Waghodia", // Placeholder, ideally fetch route name
                driver: "Active Driver", // Placeholder
                latitude: bus.lat,
                longitude: bus.lng,
                status: bus.speed < 5 ? 'stopped' : 'on-time',
                gpsActive: true,
                driverAppStatus: "online",
                speed: bus.speed || 0,
                passengers: bus.currentPassengers || 0,
                capacity: 50,
                lastUpdate: new Date(bus.lastUpdated).toLocaleTimeString(),
                eta: "Calculating...",
                nextStop: "Next Stand",
                health: { engine: "good", fuel: 80, battery: 90 }
            }));

            // Re-render
            const busListContainer = document.getElementById('bus-list-container');
            if (busListContainer) renderBusList(busListContainer);
            if (map) updateMapMarkers();
        }
    } catch (error) {
        console.error("Failed to fetch buses:", error);
    }
}

// Poll every 5 seconds
setInterval(fetchActiveBuses, 5000);


const statusConfig = {
    "on-time": { color: "bg-green-500", label: "🟢 On Time", badge: "bg-primary text-primary-foreground hover:bg-primary/80" },
    "slight-delay": { color: "bg-yellow-500", label: "🟡 Slight Delay", badge: "bg-secondary text-secondary-foreground hover:bg-secondary/80" },
    "major-delay": { color: "bg-red-500", label: "🔴 Major Delay", badge: "bg-destructive text-destructive-foreground hover:bg-destructive/80" },
    "stopped": { color: "bg-gray-500", label: "⚫ Stopped", badge: "border border-input bg-background hover:bg-accent hover:text-accent-foreground" }
};

// --- Mock Data: Trip History ---
const mockTrips = [
    { id: "1", busNumber: "BUS-101", route: "Route A - Downtown Loop", driver: "John Smith", date: "2026-01-21", startTime: "06:00", endTime: "08:30", duration: 150, delay: 0, status: "completed", passengers: 145 },
    { id: "2", busNumber: "BUS-102", route: "Route B - Airport Express", driver: "Sarah Johnson", date: "2026-01-21", startTime: "06:15", endTime: "09:00", duration: 165, delay: 15, status: "delayed", passengers: 98 },
    { id: "3", busNumber: "BUS-103", route: "Route C - University Circle", driver: "Mike Davis", date: "2026-01-21", startTime: "07:00", endTime: "09:15", duration: 135, delay: 0, status: "completed", passengers: 87 },
    { id: "4", busNumber: "BUS-104", route: "Route D - Suburban Line", driver: "Emily Brown", date: "2026-01-21", startTime: "06:30", endTime: "10:45", duration: 255, delay: 30, status: "delayed", passengers: 112 },
    { id: "5", busNumber: "BUS-101", route: "Route A - Downtown Loop", driver: "John Smith", date: "2026-01-21", startTime: "09:00", endTime: "11:25", duration: 145, delay: 0, status: "completed", passengers: 132 },
    { id: "6", busNumber: "BUS-105", route: "Route E - Industrial Park", driver: "Robert Wilson", date: "2026-01-20", startTime: "14:00", endTime: "16:20", duration: 140, delay: 5, status: "completed", passengers: 76 },
    { id: "7", busNumber: "BUS-102", route: "Route B - Airport Express", driver: "Sarah Johnson", date: "2026-01-20", startTime: "15:30", endTime: "18:45", duration: 195, delay: 45, status: "delayed", passengers: 124 },
    { id: "8", busNumber: "BUS-103", route: "Route C - University Circle", driver: "Mike Davis", date: "2026-01-20", startTime: "16:00", endTime: "18:10", duration: 130, delay: 0, status: "completed", passengers: 94 },
    { id: "9", busNumber: "BUS-106", route: "Route A - Downtown Loop", driver: "Lisa Anderson", date: "2026-01-19", startTime: "10:00", endTime: "11:30", duration: 90, delay: 0, status: "cancelled", passengers: 0 },
    { id: "10", busNumber: "BUS-104", route: "Route D - Suburban Line", driver: "Emily Brown", date: "2026-01-19", startTime: "14:00", endTime: "18:15", duration: 255, delay: 0, status: "completed", passengers: 156 }
];

// --- Dashboard Functions ---
// --- Dashboard Functions ---
function initDashboard() {
    fetchActiveBuses(); // Initial Fetch
    initMap();
}

function renderBusList(container) {
    if (!container) return;

    container.innerHTML = mockBusesLive.map(bus => {
        const status = statusConfig[bus.status] || statusConfig['on-time'];
        const engineColor = bus.health.engine === 'good' ? 'bg-green-500' : bus.health.engine === 'warning' ? 'bg-yellow-500' : 'bg-red-500';

        return `
            <div class="rounded-lg border bg-card text-card-foreground shadow-sm border-2">
                <div class="p-4">
                    <div class="flex items-start justify-between">
                        <div class="flex items-start space-x-4 flex-1">
                            <div class="p-3 ${status.color} bg-opacity-20 rounded-lg relative">
                                <i data-lucide="bus" class="h-6 w-6"></i>
                                <div class="absolute -top-1 -right-1 h-3 w-3 rounded-full ${status.color} ${bus.gpsActive ? 'animate-pulse' : ''}"></div>
                            </div>
                            <div class="space-y-2 flex-1">
                                <div class="flex items-center gap-2 flex-wrap">
                                    <h3 class="font-semibold">${bus.busNumber}</h3>
                                    <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${status.badge}">
                                        ${status.label}
                                    </div>
                                    <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${bus.gpsActive ? "bg-primary text-primary-foreground" : "bg-destructive text-destructive-foreground"}">
                                        ${bus.gpsActive ? '<i data-lucide="wifi" class="h-3 w-3 mr-1"></i> GPS' : '<i data-lucide="wifi-off" class="h-3 w-3 mr-1"></i> GPS'}
                                    </div>
                                    <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${bus.driverAppStatus === "online" ? "bg-primary text-primary-foreground" : "border-input bg-background text-foreground"}">
                                        Driver ${bus.driverAppStatus}
                                    </div>
                                </div>
                                <p class="text-sm text-muted-foreground">${bus.route}</p>
                                <div class="grid grid-cols-2 md:grid-cols-4 gap-2 text-sm">
                                    <div>
                                        <p class="text-muted-foreground">Driver</p>
                                        <p class="font-medium">${bus.driver}</p>
                                    </div>
                                    <div>
                                        <p class="text-muted-foreground">Speed</p>
                                        <p class="font-medium">${bus.speed} km/h</p>
                                    </div>
                                    <div>
                                        <p class="text-muted-foreground">Next Stop</p>
                                        <p class="font-medium">${bus.nextStop}</p>
                                    </div>
                                    <div>
                                        <p class="text-muted-foreground">ETA</p>
                                        <p class="font-medium">${bus.eta}</p>
                                    </div>
                                </div>
                                <div class="grid grid-cols-3 gap-2 text-xs">
                                    <div class="flex items-center gap-1">
                                        <div class="w-2 h-2 rounded-full ${engineColor}"></div>
                                        <span>Engine: ${bus.health.engine}</span>
                                    </div>
                                    <div>Fuel: ${bus.health.fuel}%</div>
                                    <div>Battery: ${bus.health.battery}%</div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="flex flex-col items-end gap-2">
                            <p class="text-xs text-muted-foreground">${bus.lastUpdate}</p>
                            <button onclick="openModal('${bus.id}')" class="inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-9 px-3">
                                View Details
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    lucide.createIcons();
}

let map;
let markers = [];

function initMap() {
    const mapContainer = document.getElementById('map');
    if (!mapContainer) return;

    if (map) {
        map.remove();
        map = null;
    }

    // Vadodara Center
    map = L.map('map').setView([22.3072, 73.1812], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    updateMapMarkers();

    setTimeout(() => map.invalidateSize(), 100);
}

function updateMapMarkers() {
    if (!map) return;

    // Clear existing markers
    markers.forEach(marker => map.removeLayer(marker));
    markers = [];

    // Add new markers
    markers = mockBusesLive.map(bus => {
        const color = bus.status === 'on-time' ? 'green' :
            bus.status === 'slight-delay' ? 'gold' :
                bus.status === 'major-delay' ? 'red' : 'grey';

        const marker = L.circleMarker([bus.latitude, bus.longitude], {
            color: color, fillColor: color, fillOpacity: 0.8, radius: 8
        }).addTo(map);

        marker.bindPopup(`<b>${bus.busNumber}</b><br>${bus.route}<br>Status: ${bus.status}`);
        return marker;
    });
}

function openModal(busId) {
    const bus = mockBusesLive.find(b => b.id === busId);
    if (!bus) return;

    const modal = document.getElementById('bus-modal');
    const content = document.getElementById('modal-content-area');
    const title = document.getElementById('modal-title');

    title.innerText = `${bus.busNumber} - Detailed Information`;
    const status = statusConfig[bus.status];

    content.innerHTML = `
        <div class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <h4 class="font-semibold mb-2">Bus Information</h4>
                    <div class="space-y-1 text-sm">
                        <p><strong>Bus Number:</strong> ${bus.busNumber}</p>
                        <p><strong>Route:</strong> ${bus.route}</p>
                        <p><strong>Status:</strong> ${status.label}</p>
                        <p><strong>Speed:</strong> ${bus.speed} km/h</p>
                        <p><strong>Passengers:</strong> ${bus.passengers}/${bus.capacity}</p>
                    </div>
                </div>
                <div>
                    <h4 class="font-semibold mb-2">Driver Information</h4>
                    <div class="space-y-1 text-sm">
                        <p><strong>Name:</strong> ${bus.driver}</p>
                        <p><strong>App Status:</strong> ${bus.driverAppStatus}</p>
                        <p><strong>GPS:</strong> ${bus.gpsActive ? "Active" : "Inactive"}</p>
                        <p><strong>Last Update:</strong> ${bus.lastUpdate}</p>
                    </div>
                </div>
                <div>
                    <h4 class="font-semibold mb-2">Location</h4>
                    <div class="space-y-1 text-sm">
                        <p><strong>Latitude:</strong> ${bus.latitude.toFixed(4)}</p>
                        <p><strong>Longitude:</strong> ${bus.longitude.toFixed(4)}</p>
                        <p><strong>Next Stop:</strong> ${bus.nextStop}</p>
                        <p><strong>ETA:</strong> ${bus.eta}</p>
                    </div>
                </div>
                <div>
                    <h4 class="font-semibold mb-2">Health Status</h4>
                    <div class="space-y-1 text-sm">
                        <p><strong>Engine:</strong> ${bus.health.engine}</p>
                        <p><strong>Fuel:</strong> ${bus.health.fuel}%</p>
                        <p><strong>Battery:</strong> ${bus.health.battery}%</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    modal.classList.remove('hidden');
    modal.classList.add('flex');
}

function closeModal() {
    const modal = document.getElementById('bus-modal');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
}

// --- History Functions ---
function initHistory() {
    // Only init if table body exists
    if (!document.getElementById('history-table-body')) return;

    renderHistoryTable(mockTrips);
    updateHistoryStats(mockTrips);

    // Event Listeners for Filters (ensure single binding)
    const searchInput = document.getElementById('history-search');
    if (searchInput && !searchInput.dataset.bound) {
        searchInput.addEventListener('input', filterHistory);
        searchInput.dataset.bound = true;
    }
    const routeSelect = document.getElementById('history-route-select');
    if (routeSelect && !routeSelect.dataset.bound) {
        routeSelect.addEventListener('change', filterHistory);
        routeSelect.dataset.bound = true;
    }
    const statusSelect = document.getElementById('history-status-select');
    if (statusSelect && !statusSelect.dataset.bound) {
        statusSelect.addEventListener('change', filterHistory);
        statusSelect.dataset.bound = true;
    }
}

function filterHistory() {
    const searchQuery = document.getElementById('history-search').value.toLowerCase();
    const routeFilter = document.getElementById('history-route-select').value;
    const statusFilter = document.getElementById('history-status-select').value;

    const filtered = mockTrips.filter(trip => {
        const matchesSearch = trip.busNumber.toLowerCase().includes(searchQuery) || trip.driver.toLowerCase().includes(searchQuery);
        const matchesRoute = routeFilter === 'all' || trip.route === routeFilter;
        const matchesStatus = statusFilter === 'all' || trip.status === statusFilter;
        return matchesSearch && matchesRoute && matchesStatus;
    });

    renderHistoryTable(filtered);
    updateHistoryStats(filtered);
}

function updateHistoryStats(data) {
    const total = data.length;
    const completed = data.filter(t => t.status === 'completed').length;
    const delayed = data.filter(t => t.status === 'delayed').length;
    const avgDelay = data.length > 0 ? Math.round(data.reduce((acc, t) => acc + t.delay, 0) / data.length) : 0;
    const successRate = total > 0 ? Math.round((completed / total) * 100) : 0;

    document.getElementById('stat-total-trips').innerText = total;
    document.getElementById('stat-completed-trips').innerText = completed;
    document.getElementById('stat-success-rate').innerText = `${successRate}% success rate`;
    document.getElementById('stat-delayed-trips').innerText = delayed;
    document.getElementById('stat-avg-delay').innerText = `${avgDelay} min`;
}

function renderHistoryTable(data) {
    const tbody = document.getElementById('history-table-body');
    if (!tbody) return;

    tbody.innerHTML = data.map(trip => {
        const statusClass = trip.status === 'completed' ? 'bg-primary text-primary-foreground hover:bg-primary/80' :
            trip.status === 'delayed' ? 'bg-secondary text-secondary-foreground hover:bg-secondary/80' :
                'bg-destructive text-destructive-foreground hover:bg-destructive/80';

        return `
            <tr class="border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted">
                <td class="p-4 align-middle font-medium">${trip.busNumber}</td>
                <td class="p-4 align-middle">
                    <div class="flex items-center gap-2">
                        <i data-lucide="map-pin" class="h-4 w-4"></i> ${trip.route}
                    </div>
                </td>
                <td class="p-4 align-middle">
                    <div class="flex items-center gap-2">
                        <i data-lucide="user" class="h-4 w-4"></i> ${trip.driver}
                    </div>
                </td>
                <td class="p-4 align-middle">
                    <div class="flex items-center gap-2">
                        <i data-lucide="calendar" class="h-4 w-4"></i> ${new Date(trip.date).toLocaleDateString()}
                    </div>
                </td>
                <td class="p-4 align-middle">
                    <div class="flex items-center gap-2">
                        <i data-lucide="clock" class="h-4 w-4"></i> ${trip.startTime}
                    </div>
                </td>
                <td class="p-4 align-middle">
                    <div class="flex items-center gap-2">
                         <i data-lucide="clock" class="h-4 w-4"></i> ${trip.endTime}
                    </div>
                </td>
                <td class="p-4 align-middle">${trip.duration} min</td>
                <td class="p-4 align-middle">
                     <span class="${trip.delay > 0 ? 'text-red-500 font-medium' : ''}">
                        ${trip.delay > 0 ? `+${trip.delay}` : trip.delay} min
                     </span>
                </td>
                <td class="p-4 align-middle">${trip.passengers}</td>
                <td class="p-4 align-middle">
                    <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${statusClass}">
                        ${trip.status}
                    </div>
                </td>
            </tr>
        `;
    }).join('');

    lucide.createIcons();
}

// --- Analytics & AI Predictions (Placeholder Charts) ---
function initAnalytics() {
    const canvas = document.getElementById('analytics-chart');
    if (!canvas || canvas.dataset.initialized) return;

    new Chart(canvas, {
        type: 'bar',
        data: {
            labels: ['Route A', 'Route B', 'Route C', 'Route D', 'Route E'],
            datasets: [{
                label: 'Average Delay (min)',
                data: [5, 25, 3, 18, 8],
                backgroundColor: '#3b82f6',
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } }
        }
    });

    const trendCanvas = document.getElementById('trend-chart');
    if (trendCanvas && !trendCanvas.dataset.initialized) {
        new Chart(trendCanvas, {
            type: 'line',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Avg Delay',
                    data: [12, 15, 8, 20, 18, 10, 5],
                    borderColor: '#ef4444',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true
            }
        });
        trendCanvas.dataset.initialized = true;
    }

    canvas.dataset.initialized = true;
}

// --- Mock Data: AI Predictions ---
const delayPredictions = [
    {
        route: "Route A - Downtown Loop",
        predictedDelay: 5,
        confidence: 92,
        factors: ["Light traffic", "Good weather"],
        alternateRoute: "Via 5th Avenue",
        timeSaved: 8,
        status: "low-risk"
    },
    {
        route: "Route B - Airport Express",
        predictedDelay: 25,
        confidence: 88,
        factors: ["Heavy traffic", "Road construction"],
        alternateRoute: "Via Highway 101",
        timeSaved: 15,
        status: "high-risk"
    },
    {
        route: "Route C - University Circle",
        predictedDelay: 12,
        confidence: 85,
        factors: ["Moderate traffic", "Event at stadium"],
        alternateRoute: "Via University Drive",
        timeSaved: 10,
        status: "medium-risk"
    },
    {
        route: "Route D - Suburban Line",
        predictedDelay: 3,
        confidence: 95,
        factors: ["Clear roads", "Optimal weather"],
        alternateRoute: null,
        timeSaved: 0,
        status: "low-risk"
    }
];

function initAIPrediction() {
    console.log("AI Prediction Initialized");
    renderAIPredictions();
    renderAICharts();


    function renderAIPredictions() {
        const highRiskRoutes = delayPredictions.filter(p => p.status === "high-risk").length;
        const avgConfidence = Math.round(delayPredictions.reduce((sum, p) => sum + p.confidence, 0) / delayPredictions.length);
        const potentialTimeSaved = delayPredictions.reduce((sum, p) => sum + p.timeSaved, 0);
        const activePredictions = delayPredictions.length;

        // Update Summary
        document.getElementById('ai-high-risk').textContent = highRiskRoutes;
        document.getElementById('ai-confidence').textContent = `${avgConfidence}%`;
        document.getElementById('ai-time-saved').textContent = `${potentialTimeSaved} min`;
        document.getElementById('ai-active-pred').textContent = activePredictions;

        // Render List
        const container = document.getElementById('ai-prediction-list');
        if (!container) return;

        container.innerHTML = delayPredictions.map(prediction => {
            const borderClass = prediction.status === "high-risk" ? "border-red-500 bg-red-50 dark:bg-red-950/20" :
                prediction.status === "medium-risk" ? "border-yellow-500 bg-yellow-50 dark:bg-yellow-950/20" :
                    "border-green-500 bg-green-50 dark:bg-green-950/20";

            const badgeClass = prediction.status === "high-risk" ? "bg-destructive text-destructive-foreground hover:bg-destructive/80" :
                prediction.status === "medium-risk" ? "bg-secondary text-secondary-foreground hover:bg-secondary/80" :
                    "bg-primary text-primary-foreground hover:bg-primary/80";

            return `
            <div class="p-4 border-2 rounded-lg ${borderClass}">
                <div class="flex items-start justify-between mb-3">
                    <div>
                        <h4 class="font-semibold text-lg">${prediction.route}</h4>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${badgeClass}">
                                ${prediction.predictedDelay} min delay predicted
                            </div>
                            <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 text-foreground">
                                ${prediction.confidence}% confidence
                            </div>
                        </div>
                    </div>
                </div>

                <div class="grid md:grid-cols-2 gap-4">
                    <div>
                        <p class="text-sm font-semibold mb-2">Contributing Factors:</p>
                        <ul class="space-y-1">
                            ${prediction.factors.map(factor => `
                                <li class="text-sm flex items-center gap-2">
                                    <div class="w-1.5 h-1.5 rounded-full bg-current"></div>
                                    ${factor}
                                </li>
                            `).join('')}
                        </ul>
                    </div>

                    ${prediction.alternateRoute ? `
                        <div>
                            <p class="text-sm font-semibold mb-2">Suggested Optimization:</p>
                            <div class="p-3 bg-background rounded border">
                                <div class="flex items-center gap-2 mb-2">
                                    <i data-lucide="navigation" class="h-4 w-4 text-blue-600"></i>
                                    <span class="text-sm font-medium">${prediction.alternateRoute}</span>
                                </div>
                                <p class="text-xs text-muted-foreground mb-2">
                                    Saves approximately ${prediction.timeSaved} minutes
                                </p>
                            </div>
                        </div>
                    ` : ''}
                </div>
            </div>
        `;
        }).join('');

        lucide.createIcons();
    }

    renderAICharts();
}

function renderAIPredictions() {
    const highRiskRoutes = delayPredictions.filter(p => p.status === "high-risk").length;
    const avgConfidence = Math.round(delayPredictions.reduce((sum, p) => sum + p.confidence, 0) / delayPredictions.length);
    const potentialTimeSaved = delayPredictions.reduce((sum, p) => sum + p.timeSaved, 0);
    const activePredictions = delayPredictions.length;

    // Update Summary
    document.getElementById('ai-high-risk').textContent = highRiskRoutes;
    document.getElementById('ai-confidence').textContent = `${avgConfidence}%`;
    document.getElementById('ai-time-saved').textContent = `${potentialTimeSaved} min`;
    document.getElementById('ai-active-pred').textContent = activePredictions;

    // Render List
    const container = document.getElementById('ai-prediction-list');
    if (!container) return;

    container.innerHTML = delayPredictions.map(prediction => {
        const borderClass = prediction.status === "high-risk" ? "border-red-500 bg-red-50 dark:bg-red-950/20" :
            prediction.status === "medium-risk" ? "border-yellow-500 bg-yellow-50 dark:bg-yellow-950/20" :
                "border-green-500 bg-green-50 dark:bg-green-950/20";

        const badgeClass = prediction.status === "high-risk" ? "bg-destructive text-destructive-foreground hover:bg-destructive/80" :
            prediction.status === "medium-risk" ? "bg-secondary text-secondary-foreground hover:bg-secondary/80" :
                "bg-primary text-primary-foreground hover:bg-primary/80";

        return `
            <div class="p-4 border-2 rounded-lg ${borderClass}">
                <div class="flex items-start justify-between mb-3">
                    <div>
                        <h4 class="font-semibold text-lg">${prediction.route}</h4>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 ${badgeClass}">
                                ${prediction.predictedDelay} min delay predicted
                            </div>
                            <div class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 text-foreground">
                                ${prediction.confidence}% confidence
                            </div>
                        </div>
                    </div>
                </div>

                <div class="grid md:grid-cols-2 gap-4">
                    <div>
                        <p class="text-sm font-semibold mb-2">Contributing Factors:</p>
                        <ul class="space-y-1">
                            ${prediction.factors.map(factor => `
                                <li class="text-sm flex items-center gap-2">
                                    <div class="w-1.5 h-1.5 rounded-full bg-current"></div>
                                    ${factor}
                                </li>
                            `).join('')}
                        </ul>
                    </div>

                    ${prediction.alternateRoute ? `
                        <div>
                            <p class="text-sm font-semibold mb-2">Suggested Optimization:</p>
                            <div class="p-3 bg-background rounded border">
                                <div class="flex items-center gap-2 mb-2">
                                    <i data-lucide="navigation" class="h-4 w-4 text-blue-600"></i>
                                    <span class="text-sm font-medium">${prediction.alternateRoute}</span>
                                </div>
                                <p class="text-xs text-muted-foreground mb-2">
                                    Saves approximately ${prediction.timeSaved} minutes
                                </p>
                            </div>
                        </div>
                    ` : ''}
                </div>
            </div>
        `;
    }).join('');

    lucide.createIcons();
}

function renderAICharts() {
    // Historical vs Predicted Chart
    const histCanvas = document.getElementById('ai-historical-chart');
    if (histCanvas && !histCanvas.dataset.initialized) {
        new Chart(histCanvas, {
            type: 'line',
            data: {
                labels: ["6AM", "7AM", "8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM", "6PM"],
                datasets: [
                    {
                        label: 'Historical Delay',
                        data: [5, 12, 25, 18, 8, 6, 10, 8, 15, 12, 20, 30, 28],
                        borderColor: '#94a3b8',
                        tension: 0.1
                    },
                    {
                        label: 'AI Predicted Delay',
                        data: [4, 10, 22, 15, 7, 5, 9, 7, 12, 10, 18, 28, 25],
                        borderColor: '#3b82f6',
                        tension: 0.1
                    }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: 'Minutes' }
                    }
                }
            }
        });
        histCanvas.dataset.initialized = true;
    }
}

// --- Driver Performance ---
const mockDriverPerformance = [
    { id: "1", name: "John Smith", speedViolations: 2, harshBraking: 5, rashDriving: 1, punctualityScore: 95, attendancePercent: 98, dutyHours: 168, rating: 4.8, totalTrips: 145, onTimeTrips: 138, feedback: "Excellent driver, very punctual" },
    { id: "2", name: "Sarah Johnson", speedViolations: 8, harshBraking: 12, rashDriving: 4, punctualityScore: 78, attendancePercent: 92, dutyHours: 162, rating: 4.2, totalTrips: 132, onTimeTrips: 103, feedback: "Good driver, needs improvement" },
    { id: "3", name: "Mike Davis", speedViolations: 1, harshBraking: 3, rashDriving: 0, punctualityScore: 98, attendancePercent: 100, dutyHours: 172, rating: 4.9, totalTrips: 156, onTimeTrips: 153, feedback: "Outstanding performance" },
    { id: "4", name: "Emily Brown", speedViolations: 4, harshBraking: 8, rashDriving: 2, punctualityScore: 88, attendancePercent: 95, dutyHours: 165, rating: 4.5, totalTrips: 141, onTimeTrips: 124, feedback: "Reliable and professional" },
    { id: "5", name: "Robert Wilson", speedViolations: 0, harshBraking: 2, rashDriving: 0, punctualityScore: 99, attendancePercent: 100, dutyHours: 170, rating: 5.0, totalTrips: 148, onTimeTrips: 147, feedback: "Perfect record, exemplary" }
];

function initDriverPerformance() {
    const avgPunctuality = Math.round(mockDriverPerformance.reduce((sum, d) => sum + d.punctualityScore, 0) / mockDriverPerformance.length);
    const avgRating = (mockDriverPerformance.reduce((sum, d) => sum + d.rating, 0) / mockDriverPerformance.length).toFixed(1);
    const totalViolations = mockDriverPerformance.reduce((sum, d) => sum + d.speedViolations + d.harshBraking + d.rashDriving, 0);
    const topPerformer = mockDriverPerformance.reduce((max, d) => d.punctualityScore > max.punctualityScore ? d : max);

    document.getElementById('dp-avg-punctuality').innerText = `${avgPunctuality}%`;
    document.getElementById('dp-avg-rating').innerText = `${avgRating} ⭐`;
    document.getElementById('dp-total-violations').innerText = totalViolations;
    document.getElementById('dp-top-performer').innerText = topPerformer.name;
    document.getElementById('dp-top-score').innerText = `${topPerformer.punctualityScore}% punctual`;

    const tableCtn = document.getElementById('driver-table-body');
    if (tableCtn) {
        tableCtn.innerHTML = mockDriverPerformance.map(driver => {
            const performance = driver.punctualityScore >= 95 ? { label: "Excellent", color: "text-green-600" } :
                driver.punctualityScore >= 85 ? { label: "Good", color: "text-blue-600" } :
                    driver.punctualityScore >= 75 ? { label: "Average", color: "text-yellow-600" } :
                        { label: "Needs Improvement", color: "text-red-600" };
            const successRate = Math.round((driver.onTimeTrips / driver.totalTrips) * 100);

            return `
                <tr class="border-b transition-colors hover:bg-muted/50">
                    <td class="p-4 align-middle font-medium flex items-center gap-2"><i data-lucide="user" class="h-4 w-4"></i> ${driver.name}</td>
                    <td class="p-4 align-middle">
                        <div class="flex flex-col gap-1">
                            <div class="flex items-center gap-2">
                                <span class="font-semibold ${performance.color}">${driver.punctualityScore}%</span>
                                <span class="text-xs border px-1 rounded">${performance.label}</span>
                            </div>
                            <div class="h-1 w-full bg-slate-200 rounded overflow-hidden"><div class="h-full bg-primary" style="width: ${driver.punctualityScore}%"></div></div>
                        </div>
                    </td>
                    <td class="p-4 align-middle">
                        <div class="flex items-center gap-1"><i data-lucide="star" class="h-4 w-4 text-yellow-500 fill-yellow-500"></i> ${driver.rating}</div>
                    </td>
                    <td class="p-4 align-middle"><span class="border px-2 py-0.5 rounded text-xs ${driver.speedViolations > 5 ? 'bg-red-500 text-white' : ''}">${driver.speedViolations}</span></td>
                    <td class="p-4 align-middle"><span class="border px-2 py-0.5 rounded text-xs">${driver.harshBraking}</span></td>
                    <td class="p-4 align-middle"><span class="border px-2 py-0.5 rounded text-xs">${driver.rashDriving}</span></td>
                    <td class="p-4 align-middle">${driver.dutyHours}h</td>
                    <td class="p-4 align-middle">${successRate}%</td>
                </tr>
            `;
        }).join('');
    }
    lucide.createIcons();
}

// --- Route Optimization ---
const routeComparisons = [
    { route: "Route A - Downtown Loop", current: { distance: 25, time: 45, fuel: 8.5, cost: 12.50, trafficScore: 65 }, optimized: { distance: 23, time: 38, fuel: 7.2, cost: 10.80, trafficScore: 85 }, savings: { time: 7, fuel: 1.3, cost: 1.70 }, factors: ["Avoid Main Street", "Use express lanes"] },
    { route: "Route B - Airport Express", current: { distance: 35, time: 60, fuel: 12.0, cost: 18.00, trafficScore: 45 }, optimized: { distance: 32, time: 45, fuel: 9.8, cost: 14.70, trafficScore: 78 }, savings: { time: 15, fuel: 2.2, cost: 3.30 }, factors: ["Highway 101 alternate", "Avoid downtown"] }
];

function initRouteOptimization() {
    const totalTimeSaved = routeComparisons.reduce((sum, r) => sum + r.savings.time, 0);
    const totalFuelSaved = routeComparisons.reduce((sum, r) => sum + r.savings.fuel, 0);
    const totalCostSaved = routeComparisons.reduce((sum, r) => sum + r.savings.cost, 0);

    document.getElementById('ro-time-saved').innerText = `${totalTimeSaved} min`;
    document.getElementById('ro-fuel-saved').innerText = `${totalFuelSaved.toFixed(1)} L`;
    document.getElementById('ro-cost-saved').innerText = `$${totalCostSaved.toFixed(2)}`;

    const container = document.getElementById('ro-comparisons');
    if (container) {
        container.innerHTML = routeComparisons.map(r => `
            <div class="rounded-lg border bg-card text-card-foreground shadow-sm mb-4">
                <div class="p-6">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="font-semibold flex items-center gap-2"><i data-lucide="navigation" class="h-4 w-4"></i> ${r.route}</h3>
                        <button class="bg-primary text-primary-foreground px-4 py-2 rounded-md text-sm hover:bg-primary/90">Apply Optimization</button>
                    </div>
                    <div class="grid md:grid-cols-2 gap-6">
                        <div class="space-y-2">
                            <span class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 border-transparent bg-secondary text-secondary-foreground hover:bg-secondary/80">Current Route</span>
                            <div class="flex justify-between p-2 bg-muted rounded text-sm"><span>Time:</span> <strong>${r.current.time} min</strong></div>
                            <div class="flex justify-between p-2 bg-muted rounded text-sm"><span>Fuel:</span> <strong>${r.current.fuel} L</strong></div>
                        </div>
                        <div class="space-y-2">
                            <span class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 border-transparent bg-green-500 text-white hover:bg-green-600">Optimized Route</span>
                            <div class="flex justify-between p-2 bg-green-50 dark:bg-green-900/20 rounded text-sm border border-green-200">
                                <span>Time:</span> 
                                <div class="flex items-center gap-2"><strong>${r.optimized.time} min</strong> <span class="text-green-600 text-xs">-${r.savings.time} min</span></div>
                            </div>
                            <div class="flex justify-between p-2 bg-green-50 dark:bg-green-900/20 rounded text-sm border border-green-200">
                                <span>Fuel:</span>
                                <div class="flex items-center gap-2"><strong>${r.optimized.fuel} L</strong> <span class="text-green-600 text-xs">-${r.savings.fuel} L</span></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    }
    lucide.createIcons();
}

// --- Assignments Module ---
let mockAssignments = [
    { id: 1, driverId: "1", busId: "1", route: "Route A - Downtown Loop", status: "Active" },
    { id: 2, driverId: "2", busId: "2", route: "Route B - Airport Express", status: "Active" },
    { id: 3, driverId: "3", busId: "3", route: "Route C - University Circle", status: "Scheduled" }
];

function initAssignments() {
    renderAssignmentsTable();
    populateAssignmentDropdowns();
}

function populateAssignmentDropdowns() {
    const driverSelect = document.getElementById('assign-driver');
    const busSelect = document.getElementById('assign-bus');

    // Clear existing options (keep first placeholder)
    if (driverSelect) {
        while (driverSelect.options.length > 1) driverSelect.remove(1);
        mockDriverPerformance.forEach(d => {
            const option = document.createElement('option');
            option.value = d.id;
            option.textContent = d.name;
            driverSelect.appendChild(option);
        });
    }

    if (busSelect) {
        while (busSelect.options.length > 1) busSelect.remove(1);
        mockBusesLive.forEach(b => {
            const option = document.createElement('option');
            option.value = b.id;
            // Show bus number and current route assignment helper
            option.textContent = `${b.busNumber} (${b.route})`;
            busSelect.appendChild(option);
        });
    }
}

function renderAssignmentsTable() {
    const tbody = document.getElementById('assignments-table-body');
    if (!tbody) return;

    tbody.innerHTML = mockAssignments.map(assignment => {
        const driver = mockDriverPerformance.find(d => d.id === assignment.driverId) || { name: 'Unknown' };
        const bus = mockBusesLive.find(b => b.id === assignment.busId) || { busNumber: 'Unknown' };

        const statusColor = assignment.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800';

        return `
            <tr class="border-b transition-colors hover:bg-muted/50">
                <td class="p-4 align-middle font-medium">${driver.name}</td>
                <td class="p-4 align-middle">${bus.busNumber}</td>
                <td class="p-4 align-middle">${assignment.route}</td>
                <td class="p-4 align-middle">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColor}">
                        ${assignment.status}
                    </span>
                </td>
                <td class="p-4 align-middle text-right">
                    <button onclick="deleteAssignment(${assignment.id})" class="text-red-600 hover:text-red-900 text-sm font-medium">Remove</button>
                </td>
            </tr>
        `;
    }).join('');

    lucide.createIcons();
}

function addAssignment() {
    const driverId = document.getElementById('assign-driver').value;
    const busId = document.getElementById('assign-bus').value;
    const route = document.getElementById('assign-route').value;

    if (!driverId || !busId || !route) {
        alert("Please select Driver, Bus, and Route");
        return;
    }

    const newAssignment = {
        id: Date.now(),
        driverId,
        busId,
        route,
        status: "Scheduled"
    };

    mockAssignments.push(newAssignment);
    renderAssignmentsTable();

    // Reset form
    document.getElementById('assign-driver').value = "";
    document.getElementById('assign-bus').value = "";
    document.getElementById('assign-route').value = "";

    // Optional: Show success message/toast
}

function deleteAssignment(id) {
    if (confirm("Are you sure you want to remove this assignment?")) {
        mockAssignments = mockAssignments.filter(a => a.id !== id);
        renderAssignmentsTable();
    }
}

// --- Communication Center ---
let announcements = [
    { id: "1", title: "Service Update: Route B Delay", message: "Due to road construction on Highway 101, expect 15-20 min delays.", target: "route-specific", route: "Route B", timestamp: "2026-01-21 14:30", sentBy: "Admin", recipients: 342 },
    { id: "2", title: "Holiday Schedule Change", message: "Limited bus service on Jan 26. Routes A, B, C will run at 50% capacity.", target: "all", timestamp: "2026-01-21 09:00", sentBy: "Ops Manager", recipients: 2458 }
];

function initCommunication() {
    renderAnnouncements();
}

function renderAnnouncements() {
    const list = document.getElementById('comm-list');
    const totalSent = announcements.length;
    const totalRececipients = announcements.reduce((a, b) => a + b.recipients, 0);

    document.getElementById('comm-total-sent').innerText = totalSent;
    document.getElementById('comm-total-recipients').innerText = totalRececipients.toLocaleString();

    if (list) {
        list.innerHTML = announcements.map(a => `
            < div class="rounded-lg border bg-card text-card-foreground shadow-sm p-4 mb-3" >
                <div class="flex justify-between items-start mb-2">
                    <div>
                        <h4 class="font-semibold flex items-center gap-2">
                            ${a.title}
                            <span class="text-xs px-2 py-0.5 rounded-full ${a.target === 'emergency' ? 'bg-red-500 text-white' : 'bg-blue-500 text-white'}">${a.target}</span>
                        </h4>
                        <p class="text-sm text-muted-foreground">${a.message}</p>
                    </div>
                </div>
                <div class="flex justify-between text-xs text-muted-foreground mt-2">
                    <span>Sent by: ${a.sentBy} • ${a.timestamp}</span>
                    <span>${a.recipients} recipients</span>
                </div>
            </div >
            `).join('');
    }
    lucide.createIcons();
}

// --- Incident Management ---
let incidents = [
    { id: "INC-001", type: "breakdown", busNumber: "BUS-104", driver: "Emily Brown", location: "Oak Street", timestamp: "2026-01-21 14:30", status: "in-progress", severity: "high", description: "Engine overheating" },
    { id: "INC-002", type: "accident", busNumber: "BUS-102", driver: "Sarah Johnson", location: "Terminal 2", timestamp: "2026-01-21 09:15", status: "escalated", severity: "critical", description: "Minor collision" }
];

function initIncidents() {
    renderIncidents();
}

function renderIncidents() {
    const active = incidents.filter(i => i.status !== 'resolved').length;
    const critical = incidents.filter(i => i.severity === 'critical').length;

    document.getElementById('inc-active').innerText = active;
    document.getElementById('inc-critical').innerText = critical;

    const container = document.getElementById('incidents-list');
    const iconMap = { breakdown: "🔧", accident: "🚗", emergency: "🚨", medical: "🏥" };

    if (container) {
        container.innerHTML = incidents.map(inc => `
            < div class="rounded-lg border-2 ${inc.severity === 'critical' ? 'border-red-500' : 'border-border'} bg-card text-card-foreground shadow-sm p-4 mb-4" >
                <div class="flex justify-between items-start">
                    <div class="flex gap-3">
                        <span class="text-2xl">${iconMap[inc.type] || '🚨'}</span>
                        <div>
                            <div class="flex items-center gap-2 mb-1">
                                <h4 class="font-semibold">${inc.id}</h4>
                                <span class="text-xs px-2 py-0.5 border rounded uppercase ${inc.severity === 'critical' ? 'bg-red-100 text-red-800' : ''}">${inc.severity}</span>
                                <span class="text-xs px-2 py-0.5 border rounded uppercase">${inc.status}</span>
                            </div>
                            <p class="text-sm font-medium">${inc.type} - ${inc.busNumber}</p>
                            <p class="text-sm text-muted-foreground">${inc.description}</p>
                        </div>
                    </div>
                </div>
                <div class="mt-4 grid grid-cols-3 gap-2 text-sm text-muted-foreground bg-muted/50 p-2 rounded">
                     <div class="flex items-center gap-2"><i data-lucide="user" class="h-4 w-4"></i> ${inc.driver}</div>
                     <div class="flex items-center gap-2"><i data-lucide="map-pin" class="h-4 w-4"></i> ${inc.location}</div>
                     <div class="flex items-center gap-2"><i data-lucide="clock" class="h-4 w-4"></i> ${inc.timestamp}</div>
                </div>
            </div >
            `).join('');
    }
    lucide.createIcons();
}

// --- Maintenance Tracker ---
const mockMaintenance = [
    { busNumber: "BUS-101", lastService: "2026-01-15", nextService: "2026-02-15", mileage: 45000, serviceDue: 50000, status: "good", downtime: 2 },
    { busNumber: "BUS-102", lastService: "2026-01-10", nextService: "2026-01-25", mileage: 48500, serviceDue: 49000, status: "due-soon", downtime: 8 },
    { busNumber: "BUS-103", lastService: "2025-12-20", nextService: "2026-01-20", mileage: 52000, serviceDue: 50000, status: "overdue", downtime: 12 }
];

function initMaintenance() {
    const dueSoon = mockMaintenance.filter(m => m.status === 'due-soon').length;
    const overdue = mockMaintenance.filter(m => m.status === 'overdue').length;

    document.getElementById('maint-due-soon').innerText = dueSoon;
    document.getElementById('maint-overdue').innerText = overdue;

    const body = document.getElementById('maintenance-table-body');
    if (body) {
        body.innerHTML = mockMaintenance.map(m => `
            < tr class="border-b transition-colors hover:bg-muted/50" >
                <td class="p-4 align-middle font-medium">${m.busNumber}</td>
                <td class="p-4 align-middle"><div class="flex items-center gap-2"><i data-lucide="calendar" class="h-4 w-4 text-muted-foreground"></i> ${m.lastService}</div></td>
                <td class="p-4 align-middle"><div class="flex items-center gap-2"><i data-lucide="calendar" class="h-4 w-4 text-muted-foreground"></i> ${m.nextService}</div></td>
                <td class="p-4 align-middle">${m.mileage.toLocaleString()} km</td>
                <td class="p-4 align-middle">
                    <span class="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold ${m.status === 'good' ? 'bg-green-100 text-green-800' : m.status === 'due-soon' ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'}">
                        ${m.status === 'good' ? 'Good' : m.status === 'due-soon' ? 'Due Soon' : 'Overdue'}
                    </span>
                </td>
                <td class="p-4 align-middle">${m.downtime}h</td>
            </tr >
            `).join('');
    }
    lucide.createIcons();
}

// --- User Management Module ---
let mockUsers = [
    { id: 1, name: "Alice Admin", email: "alice@gosaarthi.com", role: "Admin", status: "Active" },
    { id: 2, name: "Bob OPS", email: "bob@gosaarthi.com", role: "Manager", status: "Active" },
    { id: 3, name: "Charlie Dispatch", email: "charlie@gosaarthi.com", role: "Dispatcher", status: "Active" }
];

function initUsers() {
    renderUsersTable();
}

function renderUsersTable() {
    const tbody = document.getElementById('users-table-body');
    if (!tbody) return;

    tbody.innerHTML = mockUsers.map(user => {
        const badgeColor = user.role === 'Admin' ? 'bg-purple-100 text-purple-800' :
            user.role === 'Manager' ? 'bg-blue-100 text-blue-800' :
                user.role === 'Dispatcher' ? 'bg-orange-100 text-orange-800' : 'bg-gray-100 text-gray-800';

        return `
            <tr class="border-b transition-colors hover:bg-muted/50">
                <td class="p-4 align-middle font-medium">${user.name}</td>
                <td class="p-4 align-middle">${user.email}</td>
                <td class="p-4 align-middle">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${badgeColor}">
                        ${user.role}
                    </span>
                </td>
                <td class="p-4 align-middle">
                    <div class="flex items-center gap-2">
                        <div class="w-2 h-2 rounded-full ${user.status === 'Active' ? 'bg-green-500' : 'bg-red-500'}"></div>
                        ${user.status}
                    </div>
                </td>
                <td class="p-4 align-middle text-right">
                    <button onclick="toggleUserStatus(${user.id})" class="text-sm font-medium hover:underline mr-2">
                        ${user.status === 'Active' ? 'Deactivate' : 'Activate'}
                    </button>
                    <button onclick="deleteUser(${user.id})" class="text-red-600 hover:text-red-900 text-sm font-medium">Delete</button>
                </td>
            </tr>
        `;
    }).join('');

    // lucide.createIcons();
}

function addUser() {
    const name = document.getElementById('user-name').value;
    const email = document.getElementById('user-email').value;
    const role = document.getElementById('user-role').value;

    if (!name || !email) {
        alert("Please enter Name and Email");
        return;
    }

    const newUser = {
        id: Date.now(),
        name,
        email,
        role,
        status: "Active"
    };

    mockUsers.push(newUser);
    renderUsersTable();

    // Reset form
    document.getElementById('user-name').value = "";
    document.getElementById('user-email').value = "";
    document.getElementById('add-user-form').classList.add('hidden');
}

function toggleUserStatus(id) {
    const user = mockUsers.find(u => u.id === id);
    if (user) {
        user.status = user.status === 'Active' ? 'Inactive' : 'Active';
        renderUsersTable();
    }
}

function deleteUser(id) {
    if (confirm("Delete this user?")) {
        mockUsers = mockUsers.filter(u => u.id !== id);
        renderUsersTable();
    }
}

