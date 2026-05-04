// Mock State & Auth
const auth = {
    isLoggedIn: false,
    user: null,
    login: (username, password) => {
        auth.isLoggedIn = true;
        auth.user = { name: 'Subhash Reddy', role: 'Admin' };
        localStorage.setItem('risk_auth', 'true');
        router.navigate('dashboard');
    },
    logout: () => {
        auth.isLoggedIn = false;
        localStorage.removeItem('risk_auth');
        router.navigate('login');
    },
    check: () => {
        return localStorage.getItem('risk_auth') === 'true';
    }
};

// Router
const router = {
    current: 'login',
    navigate: (page) => {
        if (page !== 'login' && !auth.check()) {
            page = 'login';
        }
        router.current = page;
        render();
        window.scrollTo(0, 0);
    }
};

// Data
const mockRisks = Array.from({ length: 15 }, (_, i) => ({
    id: i + 1,
    title: `Data Breach Risk ${i + 1}`,
    category: i % 3 === 0 ? 'TECHNICAL' : (i % 3 === 1 ? 'OPERATIONAL' : 'COMPLIANCE'),
    likelihood: (i % 5) + 1,
    impact: ((i * 2) % 5) + 1,
    status: i % 4 === 0 ? 'OPEN' : (i % 4 === 1 ? 'MITIGATED' : 'IN_PROGRESS'),
    owner: ['Alice', 'Bob', 'Charlie'][i % 3],
    dueDate: new Date(Date.now() + i * 86400000).toLocaleDateString()
}));

// Views
const views = {
    login: () => `
        <div class="animate-fade-in" style="max-width: 400px; margin: 100px auto;">
            <div class="card" style="text-align: center;">
                <div class="logo" style="justify-content: center; margin-bottom: 30px; font-size: 32px;">
                    <i data-lucide="shield-alert" style="width: 40px; height: 40px;"></i>
                    RiskHeatmap
                </div>
                <h2 style="margin-bottom: 10px;">Welcome Back</h2>
                <p style="color: var(--text-muted); margin-bottom: 30px;">Sign in to your risk dashboard</p>
                <div style="text-align: left;">
                    <label style="display: block; margin-bottom: 8px; font-size: 14px;">Email Address</label>
                    <input type="text" placeholder="subhash@example.com" style="width: 100%; padding: 12px; border-radius: 8px; border: 1px solid var(--glass-border); background: rgba(0,0,0,0.2); color: white; margin-bottom: 20px;">
                    
                    <label style="display: block; margin-bottom: 8px; font-size: 14px;">Password</label>
                    <input type="password" placeholder="••••••••" style="width: 100%; padding: 12px; border-radius: 8px; border: 1px solid var(--glass-border); background: rgba(0,0,0,0.2); color: white; margin-bottom: 30px;">
                    
                    <button class="btn btn-primary" style="width: 100%; justify-content: center;" onclick="auth.login()">Sign In</button>
                </div>
                <p style="margin-top: 20px; font-size: 14px; color: var(--text-muted);">
                    Don't have an account? <a href="#" style="color: var(--accent); text-decoration: none;">Contact Admin</a>
                </p>
            </div>
        </div>
    `,
    dashboard: () => `
        <div class="animate-fade-in">
            <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 40px;">
                <div>
                    <h1 style="font-size: 32px; margin-bottom: 8px;">Risk Overview</h1>
                    <p style="color: var(--text-muted);">Real-time monitoring of organizational threat vectors</p>
                </div>
                <div style="display: flex; gap: 12px;">
                    <div class="card" style="padding: 10px 20px; display: flex; align-items: center; gap: 10px;">
                        <div style="width: 8px; height: 8px; border-radius: 50%; background: var(--danger);"></div>
                        <span>12 Critical Risks</span>
                    </div>
                    <button class="btn btn-primary" onclick="router.navigate('analysis')">
                        <i data-lucide="sparkles"></i> AI Insights
                    </button>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 30px;">
                <!-- Heatmap Card -->
                <div class="card">
                    <h3 style="margin-bottom: 20px; display: flex; align-items: center; gap: 10px;">
                        <i data-lucide="grid"></i> Risk Heatmap
                    </h3>
                    <div class="heatmap-container">
                        ${Array.from({ length: 25 }, (_, i) => {
                            const l = 5 - Math.floor(i / 5);
                            const im = (i % 5) + 1;
                            const score = l * im;
                            let riskClass = 'risk-low';
                            if (score > 15) riskClass = 'risk-crit';
                            else if (score > 10) riskClass = 'risk-high';
                            else if (score > 5) riskClass = 'risk-med';
                            return `<div class="heatmap-cell ${riskClass}" title="L:${l}, I:${im}">${score}</div>`;
                        }).join('')}
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-top: 15px; color: var(--text-muted); font-size: 12px;">
                        <span>Likelihood →</span>
                        <span>Impact ↑</span>
                    </div>
                </div>

                <!-- Stats & Recent -->
                <div style="display: flex; flex-direction: column; gap: 30px;">
                    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px;">
                        <div class="card" style="background: linear-gradient(135deg, rgba(27, 79, 138, 0.2), transparent);">
                            <p style="color: var(--text-muted); font-size: 14px;">Total Risks</p>
                            <h2 style="font-size: 28px; margin: 10px 0;">48</h2>
                            <p style="color: var(--success); font-size: 12px;">↑ 12% from last month</p>
                        </div>
                        <div class="card">
                            <p style="color: var(--text-muted); font-size: 14px;">Avg. Mitigation Time</p>
                            <h2 style="font-size: 28px; margin: 10px 0;">4.2d</h2>
                            <p style="color: var(--success); font-size: 12px;">↓ 0.5d improvement</p>
                        </div>
                        <div class="card">
                            <p style="color: var(--text-muted); font-size: 14px;">Security Score</p>
                            <h2 style="font-size: 28px; margin: 10px 0;">84%</h2>
                            <div style="width: 100%; height: 6px; background: rgba(255,255,255,0.1); border-radius: 3px; margin-top: 10px;">
                                <div style="width: 84%; height: 100%; background: var(--accent); border-radius: 3px;"></div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="card">
                        <h3 style="margin-bottom: 20px;">Priority Risks</h3>
                        <table style="margin-top: 0;">
                            <thead>
                                <tr>
                                    <th>Risk Title</th>
                                    <th>Level</th>
                                    <th>Owner</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${mockRisks.slice(0, 4).map(r => `
                                    <tr>
                                        <td>${r.title}</td>
                                        <td><span style="color: ${r.impact * r.likelihood > 10 ? 'var(--danger)' : 'var(--warning)'}">${r.impact * r.likelihood > 10 ? 'CRITICAL' : 'HIGH'}</span></td>
                                        <td>${r.owner}</td>
                                        <td><a href="#" style="color: var(--accent); font-size: 14px;">View</a></td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    `,
    inventory: () => `
        <div class="animate-fade-in">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
                <h1>Risk Inventory</h1>
                <button class="btn btn-primary"><i data-lucide="plus"></i> Add New Risk</button>
            </div>
            
            <div class="card">
                <div style="display: flex; gap: 15px; margin-bottom: 20px;">
                    <input type="text" placeholder="Search risks..." style="flex: 1; padding: 10px; border-radius: 8px; border: 1px solid var(--glass-border); background: rgba(0,0,0,0.2); color: white;">
                    <select style="padding: 10px; border-radius: 8px; border: 1px solid var(--glass-border); background: rgba(0,0,0,0.2); color: white;">
                        <option>All Categories</option>
                        <option>TECHNICAL</option>
                        <option>OPERATIONAL</option>
                    </select>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Risk Title</th>
                            <th>Category</th>
                            <th>Status</th>
                            <th>Score</th>
                            <th>Owner</th>
                            <th>Due Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mockRisks.map(r => `
                            <tr>
                                <td style="color: var(--text-muted);">#${r.id}</td>
                                <td style="font-weight: 500;">${r.title}</td>
                                <td><span style="font-size: 11px; padding: 4px 8px; border-radius: 4px; border: 1px solid var(--glass-border);">${r.category}</span></td>
                                <td><span style="color: ${r.status === 'MITIGATED' ? 'var(--success)' : 'var(--text-muted)'}">${r.status}</span></td>
                                <td>${r.impact * r.likelihood}</td>
                                <td>${r.owner}</td>
                                <td style="color: var(--text-muted);">${r.dueDate}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        </div>
    `,
    analysis: () => `
        <div class="animate-fade-in">
            <div style="margin-bottom: 30px;">
                <h1 style="display: flex; align-items: center; gap: 15px;">
                    <i data-lucide="sparkles" style="color: var(--accent);"></i>
                    AI Risk Analysis
                </h1>
                <p style="color: var(--text-muted);">Intelligent mitigation recommendations powered by Llama 3.1</p>
            </div>

            <div class="card" style="margin-bottom: 30px; border-left: 4px solid var(--accent);">
                <h3>Analysis Prompt</h3>
                <textarea style="width: 100%; height: 100px; margin-top: 15px; background: rgba(0,0,0,0.2); border: 1px solid var(--glass-border); border-radius: 8px; color: white; padding: 15px;" readonly>The project has approaching deadlines with incomplete work and possible delivery risks. Analyze the technical debt and suggest immediate mitigation steps for the engineering team.</textarea>
                <div style="margin-top: 15px; text-align: right;">
                    <button class="btn btn-primary">Regenerate Analysis</button>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 30px;">
                <div class="card">
                    <h3 style="margin-bottom: 20px;">Mitigation Recommendations</h3>
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                        <div style="padding: 15px; border-radius: 8px; background: rgba(16, 185, 129, 0.1); border: 1px solid var(--success);">
                            <h4 style="color: var(--success); margin-bottom: 5px;">Immediate Action</h4>
                            <p>Implement automated smoke tests for the critical path to ensure no regression during rapid deployment cycles.</p>
                        </div>
                        <div style="padding: 15px; border-radius: 8px; background: rgba(56, 189, 248, 0.1); border: 1px solid var(--accent);">
                            <h4 style="color: var(--accent); margin-bottom: 5px;">Strategic Move</h4>
                            <p>Shift to a feature-flag based deployment to decouple release from deployment, reducing the risk of broken features in production.</p>
                        </div>
                        <div style="padding: 15px; border-radius: 8px; background: rgba(245, 158, 11, 0.1); border: 1px solid var(--warning);">
                            <h4 style="color: var(--warning); margin-bottom: 5px;">Monitoring</h4>
                            <p>Set up real-time alerting for API latency spikes as the system scales during the high-load period.</p>
                        </div>
                    </div>
                </div>
                
                <div class="card">
                    <h3 style="margin-bottom: 20px;">AI Confidence</h3>
                    <div style="text-align: center; margin: 30px 0;">
                        <div style="font-size: 48px; font-weight: 800; color: var(--accent);">92%</div>
                        <p style="color: var(--text-muted);">High Confidence Score</p>
                    </div>
                    <div style="display: flex; flex-direction: column; gap: 10px; font-size: 14px;">
                        <div style="display: flex; justify-content: space-between;">
                            <span>Data Freshness</span>
                            <span style="color: var(--success);">Optimal</span>
                        </div>
                        <div style="display: flex; justify-content: space-between;">
                            <span>Historical Context</span>
                            <span style="color: var(--success);">Strong</span>
                        </div>
                        <div style="display: flex; justify-content: space-between;">
                            <span>Model Latency</span>
                            <span style="color: var(--text-muted);">1.2s</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `,
    export: () => `
        <div class="animate-fade-in">
            <div style="margin-bottom: 30px;">
                <h1>Data Export Center</h1>
                <p style="color: var(--text-muted);">Generate professional reports and raw data exports</p>
            </div>

            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px;">
                <div class="card" style="text-align: center; padding: 40px 24px;">
                    <i data-lucide="file-text" style="width: 48px; height: 48px; color: var(--accent); margin-bottom: 20px;"></i>
                    <h3>Executive PDF Report</h3>
                    <p style="color: var(--text-muted); font-size: 14px; margin: 15px 0;">Summary of all critical risks with AI insights and charts.</p>
                    <button class="btn btn-primary" style="width: 100%; justify-content: center;">Download PDF</button>
                </div>
                
                <div class="card" style="text-align: center; padding: 40px 24px;">
                    <i data-lucide="table" style="width: 48px; height: 48px; color: var(--success); margin-bottom: 20px;"></i>
                    <h3>Raw CSV Export</h3>
                    <p style="color: var(--text-muted); font-size: 14px; margin: 15px 0;">Full risk inventory data in CSV format for Excel/BI tools.</p>
                    <button class="btn btn-primary" style="width: 100%; justify-content: center;">Download CSV</button>
                </div>
                
                <div class="card" style="text-align: center; padding: 40px 24px;">
                    <i data-lucide="mail" style="width: 48px; height: 48px; color: var(--warning); margin-bottom: 20px;"></i>
                    <h3>Stakeholder Email</h3>
                    <p style="color: var(--text-muted); font-size: 14px; margin: 15px 0;">Send daily summary directly to registered stakeholders.</p>
                    <button class="btn btn-primary" style="width: 100%; justify-content: center;">Send Summary</button>
                </div>
            </div>

            <div class="card" style="margin-top: 30px;">
                <h3 style="margin-bottom: 20px;">Recent Export Logs</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Report Name</th>
                            <th>Date</th>
                            <th>Generated By</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Quarterly_Risk_Review.pdf</td>
                            <td>May 4, 2026</td>
                            <td>Subhash Reddy</td>
                            <td><span style="color: var(--success);">Success</span></td>
                        </tr>
                        <tr>
                            <td>Monthly_Inventory_Export.csv</td>
                            <td>May 1, 2026</td>
                            <td>Subhash Reddy</td>
                            <td><span style="color: var(--success);">Success</span></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `
};

// Render Engine
function render() {
    const main = document.getElementById('main-content');
    const navbar = document.getElementById('navbar');
    
    if (router.current === 'login') {
        navbar.style.display = 'none';
        main.innerHTML = views.login();
    } else {
        navbar.style.display = 'flex';
        main.innerHTML = views[router.current]();
        
        // Update active link
        document.querySelectorAll('.nav-links a').forEach(a => a.classList.remove('active'));
        const activeLink = document.getElementById(`nav-${router.current}`);
        if (activeLink) activeLink.classList.add('active');
    }
    
    // Refresh icons
    lucide.createIcons();
}

// Initial Check
if (auth.check()) {
    router.navigate('dashboard');
} else {
    router.navigate('login');
}
