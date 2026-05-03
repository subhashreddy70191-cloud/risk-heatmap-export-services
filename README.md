# Risk Heatmap Export Service

A professional-grade system for identifying, scoring, and notifying stakeholders about operational and technical risks. The system leverages AI for risk analysis and automated scheduling for notifications.

## 🏗️ Architecture

```mermaid
graph TD
    Client[Browser / Frontend] <--> Gateway[Spring Boot Backend]
    Gateway <--> DB[(PostgreSQL / H2)]
    Gateway <--> Cache[(Redis)]
    Gateway <--> AI[Python AI Service]
    AI <--> LLM[External LLM / Internal Model]
    
    subgraph "Core Components"
        Gateway
        DB
        Cache
    end
    
    subgraph "AI Integration"
        AI
    end
    
    subgraph "Automation"
        Scheduler[@Scheduled Tasks]
        Email[JavaMailSender]
    end
    
    Gateway --> Scheduler
    Scheduler --> Email
```

## 🚀 Overview

The **Risk Heatmap Export Service** is designed to centralize risk management workflows. It provides:
- **Risk Scoring**: Automated calculation based on Likelihood and Impact.
- **AI Insights**: Automated risk descriptions and mitigation recommendations generated via the Python AI service.
- **Notifications**: Daily summaries and deadline alerts sent via email.
- **Reporting**: Export functionality for risk data.

## 📋 Prerequisites

Ensure you have the following installed:
- **Java**: JDK 17+
- **Python**: 3.9+
- **Database**: PostgreSQL (or H2 for local dev)
- **Cache**: Redis 7+
- **Build Tool**: Maven 3.8+
- **Containerization**: Docker & Docker Compose (optional but recommended)

## 🛠️ Setup Steps

### 1. Backend Setup (Spring Boot)
```bash
cd backend
# Create .env or update application.yml
mvn clean install
mvn spring-boot:run
```
- **API Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### 2. AI Service Setup (Python/Flask)
```bash
cd python-ai
python -m venv venv
source venv/bin/activate  # On Windows: .\venv\Scripts\activate
pip install -r requirements.txt
python app.py
```

### 3. Docker Deployment (Recommended)
```bash
docker-compose up --build
```

## ⚙️ Environment (.env) Reference

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port for the backend service | `8080` |
| `DB_URL` | JDBC Database URL | `jdbc:postgresql://db:5432/riskmap` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `password` |
| `REDIS_HOST` | Redis cache hostname | `localhost` / `cache` |
| `JWT_SECRET` | Secret key for JWT signing | `RiskHeatmap2024...` |
| `AI_SERVICE_URL` | URL of the Python AI service | `http://localhost:5000` |
| `MAIL_USERNAME` | SMTP server username | `subhashreddy70191@gmail.com` |
| `MAIL_PASSWORD` | SMTP server password | `testpass` |
| `ADMIN_EMAIL` | Email for daily summaries | `subhashreddy70191@gmail.com` |

## 🧪 Testing

To run the full test suite (including unit and integration tests):
```bash
cd backend
mvn test
```

---
*Created as part of the Java Developer Training Program - Week 3.*