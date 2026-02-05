# 🚌 Smart Public Transport Optimization System

A real-time smart public transport monitoring and optimization system designed to improve efficiency, safety, and reliability of public bus transportation using mobile-based solutions.

---

## 📌 Project Overview

This project was developed during **PU Code Hackathon 3.0** organized by **Parul University** in collaboration with **Vadodara Municipal Corporation**.

The system provides a complete digital ecosystem to track buses, monitor crowd levels, and help transport authorities make data-driven decisions.

---

## 🚀 Problem Statement

Public bus commuters often face:

- ❌ Uncertainty in bus arrival timings  
- ❌ Overcrowded buses without prior information  
- ❌ Safety concerns, especially for women passengers  
- ❌ Lack of monitoring tools for transport authorities  
- ❌ Absence of ticket machines in many buses  

---

## 💡 Our Solution

We developed a **multi-module smart transport system** using mobile phones instead of expensive hardware.

The system includes:

### 📍 Driver App
- Uses smartphone GPS
- Sends real-time bus location
- Updates location every few seconds

---

### 👥 Conductor App
- Works as a digital passenger counter
- Replaces ticket machines
- Updates passenger crowd count manually
- Sends real-time crowd data to backend

---

### 🗺 Passenger App
- Shows live bus location on map
- Displays crowd status (Low / Medium / High)
- Helps passengers plan travel efficiently

---

### 📊 Authority Panel (Admin Dashboard)
Allows authorities to:

- Track buses in real time
- Monitor crowd levels
- Analyze routes and performance
- Improve safety monitoring
- Make operational decisions

---



---

## 🛠 Tech Stack

### Mobile Apps
- Kotlin
- Android SDK
- Google Maps SDK

### Backend
- Node.js
- Express.js
- REST APIs

### Tools & Services
- OkHttp
- JSON APIs
- GitHub

---

## 👥 Crowd Calculation Logic

Since ticket machines are unavailable, crowd is calculated using the Conductor App.

### Process:

- Conductor updates passenger boarding using:
  - ➕ +1 / +5 buttons
- Updates passenger exit using:
  - ➖ -1 / -5 buttons

### Crowd Levels:

| Passenger Count | Crowd Level |
|---------------|-------------|
| 0 – 20        | Low         |
| 21 – 40       | Medium      |
| 41+           | High        |

---

## 🌍 Target Implementation

This solution is designed specifically for:

📍 Vadodara, Gujarat

Bus stops can be geo-marked and integrated into the passenger application.

---

## 🏆 Achievements

- Recognized under **Innovative Minds Category**
- Won **₹5000 Cash Prize**
- Successfully demonstrated working prototype

---

## ⚙️ Installation & Setup

### Backend Setup

```bash
git clone <repo-link>
cd backend
npm install
node index.js


## 🧠 System Architecture

