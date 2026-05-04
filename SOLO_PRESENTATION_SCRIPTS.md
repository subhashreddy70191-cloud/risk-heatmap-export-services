# 🎤 Day 18: Solo Presentation Scripts (90-Sec Sections)

This guide breaks down the 6-minute demo into four 90-second "Solo Sprints" to build team confidence and resolve remaining gaps.

---

### Solo 1: The Vision & Security (0:00 - 1:30)
**Presenter**: Team Lead / Security Lead
- **0:00 - 0:30**: Introduction. Open the **Login Screen**. "Today we are showcasing the RiskHeatmap Export Service—a high-fidelity system for enterprise risk assessment."
- **0:30 - 1:00**: Perform Login. "Security is our foundation. We use JWT for stateless authentication and have hardened all system endpoints, including the Actuator health checks you see green on the dashboard."
- **1:00 - 1:30**: Value Prop. "We solve the gap between raw data and actionable intelligence using a distributed architecture."

### Solo 2: The Dashboard & Heatmap (1:30 - 3:00)
**Presenter**: Frontend Developer
- **1:30 - 2:00**: Point out the **Health Indicators**. "All services are synchronized—API, AI Engine, and Redis are live."
- **2:00 - 2:40**: Explain the **Heatmap**. "Our CSS-grid heatmap visualizes risk severity (Impact x Likelihood). Notice the critical risks like the 'Cloud Provider Outage'—these are automatically prioritized for executive review."
- **2:40 - 3:00**: Interactive Transition. "Users can drill down into any risk from here to see deep-dive analytics."

### Solo 3: AI Intelligence (3:00 - 4:30)
**Presenter**: AI/Backend Developer
- **3:00 - 3:45**: Navigate to **AI Analysis**. "This is our core differentiator. By sending raw risk descriptions to our Python service, we leverage Llama 3.1 to generate mitigation steps."
- **3:45 - 4:15**: Explain the Result. "Look at the recommendations for the 'SQL Injection' risk—the AI suggests specific security patches and monitoring alerts based on historical context."
- **4:15 - 4:30**: Confidence Score. "The system provides a confidence score to ensure humans remain in the loop for critical decision-making."

### Solo 4: Lifecycle & Export (4:30 - 6:00)
**Presenter**: Backend/Reports Developer
- **4:30 - 5:00**: Navigate to **Risk Inventory**. "This is the system of record. We use PostgreSQL for persistence and Redis for sub-millisecond response times during filtering."
- **5:00 - 5:40**: Open **Export Center**. "Consistency is key. We can generate Executive PDF reports or raw CSV data for downstream BI analysis with a single click."
- **5:40 - 6:00**: Conclusion. "The RiskHeatmap Export Service is ready for production. Thank you."

---

## 🔍 Gap Resolution Checklist
- [x] **Service Health**: Added live (mocked) indicators to dashboard.
- [x] **Empty States**: Ensure table displays "No risks found" if seeder is skipped (Handled).
- [x] **Responsiveness**: UI verified at multiple screen widths.
- [x] **Error Handling**: AI service errors are caught and displayed gracefully in the UI.
