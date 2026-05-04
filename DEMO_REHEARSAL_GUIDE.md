# 🚀 Day 17: Demo Rehearsal 2 Guide

This document serves as the master script and Q&A guide for the 6-minute team demonstration.

## ⏱️ 6-Minute Presentation Script

| Time | Segment | Presenter | Key Focus |
| :--- | :--- | :--- | :--- |
| **0:00 - 1:00** | **Introduction & Problem** | Team Lead | Project goals: Centralizing risk management, AI-driven insights, and automated reporting. |
| **1:00 - 2:00** | **Authentication & Security** | Security Lead | Secure login (JWT), stateless architecture, and hardened Actuator endpoints. |
| **2:00 - 3:30** | **Risk Heatmap & Inventory** | Frontend Dev | Interactive dashboard, Severity vs Likelihood visualization, and risk management workflow. |
| **3:30 - 5:00** | **AI Analysis Integration** | AI/Backend Dev | How Llama 3.1 analyzes risk descriptions to provide mitigation recommendations. |
| **5:00 - 6:00** | **Notifications & Export** | Backend Dev | Automated email alerts (Thymeleaf) and multi-format data export center. |

---

## ❓ 5 Key Questions (No Notes Required)

Every team member must be ready to answer these without referring to documentation:

### 1. How is the Risk Score calculated in the system?
> **Answer**: The Risk Score is a product of **Likelihood** (1-5) and **Impact** (1-5). Scores above 15 are classified as **CRITICAL**, 10-15 as **HIGH**, and so on. This allows for objective prioritization on the Heatmap.

### 2. How does the system ensure secure communication between the Frontend and Backend?
> **Answer**: We use **JWT (JSON Web Tokens)** for stateless authentication. Every request includes a Bearer token in the header, which is validated by a custom security filter. We've also hardened the backend by disabling unauthorized Actuator access.

### 3. What is the role of the Python AI service in this architecture?
> **Answer**: The Python service acts as an intelligent processing layer. It uses a **Llama 3.1** model via the Groq API to analyze raw risk descriptions and generate actionable **Mitigation Recommendations**, reducing the manual workload for risk officers.

### 4. How does the system handle data consistency and caching?
> **Answer**: We use **PostgreSQL** for persistent storage and **Redis** for caching. This ensures high performance for frequently accessed data like the Risk Heatmap, while maintaining data integrity for the risk inventory.

### 5. How are stakeholders notified about approaching deadlines?
> **Answer**: We implemented a **)Notification Scheduler** in Spring Boot that runs daily. It identifies risks nearing their due date and uses **JavaMailSender** with **Thymeleaf templates** to send professional HTML alerts to stakeholders.

---

## 🛠️ Rehearsal Checklist
- [ ] Backend is running (`mvn spring-boot:run`)
- [ ] Python AI service is running (`python app.py`)
- [ ] Frontend is live (`http://localhost:8000`)
- [ ] Database is seeded with diverse risks (Cloud outages, SQLi vulnerabilities, etc.)
- [ ] Screen sharing is tested and transitions are smooth
