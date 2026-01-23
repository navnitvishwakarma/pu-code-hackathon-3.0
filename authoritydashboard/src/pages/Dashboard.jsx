import React, { useState } from 'react';
import { APIProvider, Map, AdvancedMarker, Pin } from '@vis.gl/react-google-maps';

const Dashboard = () => {
    // State for Fleet
    const [selectedBus, setSelectedBus] = useState(null);
    const [busData, setBusData] = useState([]);

    // Fetch Live Data
    React.useEffect(() => {
        const fetchBuses = async () => {
            try {
                const response = await fetch('/api/buses');
                const data = await response.json();
                if (data.success) {
                    setBusData(data.buses.map(bus => ({
                        id: bus.busId,
                        lat: parseFloat(bus.lat),
                        lng: parseFloat(bus.lng),
                        status: getStatusFromCrowd(bus.crowdLevel),
                        label: `${bus.busId}`,
                        speed: bus.speed || 0,
                        crowd: bus.crowdLevel || 'Low',
                        lastUpdated: bus.lastUpdated
                    })));
                }
            } catch (error) {
                console.error("Error fetching buses:", error);
            }
        };

        fetchBuses();
        const interval = setInterval(fetchBuses, 5000); // Poll every 5s
        return () => clearInterval(interval);
    }, []);

    const getStatusFromCrowd = (level) => {
        switch (level) {
            case 'High': return 'emergency';
            case 'Medium': return 'busy';
            default: return 'safe';
        }
    };

    // Dark Mode Map Style
    const darkMapStyle = [
        { elementType: "geometry", stylers: [{ color: "#242f3e" }] },
        { elementType: "labels.text.stroke", stylers: [{ color: "#242f3e" }] },
        { elementType: "labels.text.fill", stylers: [{ color: "#746855" }] },
        {
            featureType: "administrative.locality",
            elementType: "labels.text.fill",
            stylers: [{ color: "#d59563" }],
        },
        {
            featureType: "poi",
            elementType: "labels.text.fill",
            stylers: [{ color: "#d59563" }],
        },
        {
            featureType: "poi.park",
            elementType: "geometry",
            stylers: [{ color: "#263c3f" }],
        },
        {
            featureType: "poi.park",
            elementType: "labels.text.fill",
            stylers: [{ color: "#6b9a76" }],
        },
        {
            featureType: "road",
            elementType: "geometry",
            stylers: [{ color: "#38414e" }],
        },
        {
            featureType: "road",
            elementType: "geometry.stroke",
            stylers: [{ color: "#212a37" }],
        },
        {
            featureType: "road",
            elementType: "labels.text.fill",
            stylers: [{ color: "#9ca5b3" }],
        },
        {
            featureType: "road.highway",
            elementType: "geometry",
            stylers: [{ color: "#746855" }],
        },
        {
            featureType: "road.highway",
            elementType: "geometry.stroke",
            stylers: [{ color: "#1f2835" }],
        },
        {
            featureType: "road.highway",
            elementType: "labels.text.fill",
            stylers: [{ color: "#f3d19c" }],
        },
        {
            featureType: "transit",
            elementType: "geometry",
            stylers: [{ color: "#2f3948" }],
        },
        {
            featureType: "transit.station",
            elementType: "labels.text.fill",
            stylers: [{ color: "#d59563" }],
        },
        {
            featureType: "water",
            elementType: "geometry",
            stylers: [{ color: "#17263c" }],
        },
        {
            featureType: "water",
            elementType: "labels.text.fill",
            stylers: [{ color: "#515c6d" }],
        },
        {
            featureType: "water",
            elementType: "labels.text.stroke",
            stylers: [{ color: "#17263c" }],
        },
    ];

    return (
        <main className="relative flex-1 w-full h-full bg-[#0a150f] overflow-hidden group/map">
            <APIProvider apiKey="AIzaSyD4Vxm_SiP4hFORseRBrajX3vwgqIxjZjI">
                <Map
                    defaultCenter={{ lat: 22.3072, lng: 73.1812 }}
                    defaultZoom={13}
                    mapId="4504f8b37365c3d0"
                    styles={darkMapStyle}
                    disableDefaultUI={true}
                    gestureHandling={'greedy'}
                    className="w-full h-full"
                >
                    {busData.map(m => (
                        <AdvancedMarker
                            key={m.id}
                            position={{ lat: m.lat, lng: m.lng }}
                            onClick={() => setSelectedBus(m)}
                        >
                            <div className="relative flex items-center justify-center size-8 -translate-x-1/2 -translate-y-1/2">
                                <div className={`absolute size-3 ${m.status === 'safe' ? 'bg-primary' : m.status === 'busy' ? 'bg-amber-500' : 'bg-red-500'} rounded-full animate-ping opacity-75`}></div>
                                <div className={`relative size-8 bg-[#112217] border-2 border-white rounded-full flex items-center justify-center shadow-lg`}>
                                    <span className="material-symbols-outlined text-white text-[18px]">directions_bus</span>
                                </div>
                            </div>
                        </AdvancedMarker>
                    ))}
                </Map>
            </APIProvider>

            {/* Map Controls */}
            <div className="absolute bottom-6 right-6 flex flex-col gap-2 z-10 pointer-events-none">
                {/* Note: Google Maps has built-in controls, but we keep these consistent styling if needed */}
            </div>

            {/* Selected Bus Floating Card */}
            {selectedBus && (
                <div className="absolute left-6 top-1/2 -translate-y-1/2 w-72 glass-panel rounded-2xl border border-[#326744] shadow-2xl p-0 overflow-hidden backdrop-blur-xl animate-[fadeIn_0.3s_ease-out] z-50">
                    <div className="bg-[#193322]/80 p-4 border-b border-[#326744] flex justify-between items-start">
                        <div>
                            <div className="flex items-center gap-2 mb-1">
                                <h3 className="text-white font-bold text-lg leading-none">{selectedBus.label.split('(')[0]}</h3>
                                <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded uppercase tracking-wide border ${selectedBus.status === 'safe' ? 'bg-primary/20 text-primary border-primary/30' : 'bg-red-500/20 text-red-500 border-red-500/30'}`}>
                                    {selectedBus.status.toUpperCase()}
                                </span>
                            </div>
                            <p className="text-[#92c9a4] text-xs">Route 534 • Toward Nehru Place</p>
                        </div>
                        <button onClick={() => setSelectedBus(null)} className="text-[#92c9a4] hover:text-white transition-colors">
                            <span className="material-symbols-outlined text-lg">close</span>
                        </button>
                    </div>
                    <div className="grid grid-cols-2 gap-px bg-[#23482f]">
                        <StatBox label="Speed" value="32" unit="km/h" />
                        <StatBox label="Next Stop" value="02" unit="min" sub="Station" />
                    </div>
                    <div className="p-4 flex items-center justify-between">
                        <div className="flex items-center gap-2">
                            <div className="size-8 rounded-full bg-[#23482f] flex items-center justify-center text-[#92c9a4]">
                                <span className="material-symbols-outlined text-sm">person</span>
                            </div>
                            <div className="flex flex-col">
                                <span className="text-xs text-white font-medium">Ramesh K.</span>
                                <span className="text-[10px] text-[#92c9a4]">ID: 8821</span>
                            </div>
                        </div>
                        <div className="flex gap-2">
                            <IconButton icon="call" />
                            <IconButton icon="videocam" />
                        </div>
                    </div>
                    <button className="w-full bg-primary hover:bg-green-400 text-black font-bold py-2.5 text-xs uppercase tracking-wider transition-colors">
                        View Full Details
                    </button>
                </div>
            )}

            {/* Legend */}
            <div className="absolute top-6 right-6 bg-surface-dark/90 backdrop-blur border border-[#23482f] p-3 rounded-lg shadow-lg flex flex-col gap-2 z-10">
                <LegendItem color="bg-primary" label="Low Crowd" />
                <LegendItem color="bg-amber-500" label="Med Crowd" />
                <LegendItem color="bg-red-500" label="High / Alert" />
            </div>
        </main>
    )
}

const StatBox = ({ label, value, unit, sub }) => (
    <div className="bg-[#112217]/80 p-3 flex flex-col gap-1">
        <span className="text-[#92c9a4] text-[10px] uppercase tracking-wider">{label}</span>
        <div className="flex items-baseline gap-1">
            <span className="text-white text-lg font-bold font-mono">{value}</span>
            <span className="text-[#92c9a4] text-xs">{unit}</span>
        </div>
        {sub && <span className="text-[10px] text-[#92c9a4] truncate">{sub}</span>}
    </div>
)

const IconButton = ({ icon }) => (
    <button className="size-8 rounded-full border border-[#326744] hover:bg-[#23482f] flex items-center justify-center text-[#92c9a4] transition-colors">
        <span className="material-symbols-outlined text-sm">{icon}</span>
    </button>
)

const LegendItem = ({ color, label }) => (
    <div className="flex items-center gap-2">
        <div className={`size-2.5 ${color} rounded-full`}></div>
        <span className="text-[10px] text-[#92c9a4] font-medium">{label}</span>
    </div>
)

export default Dashboard;
