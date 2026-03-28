import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <h1>Dashboard</h1>
    <div class="stats-grid">
      <div class="stat-card"><h3>Claims Today</h3><p class="stat-value">--</p></div>
      <div class="stat-card"><h3>Pending Claims</h3><p class="stat-value">--</p></div>
      <div class="stat-card"><h3>Active Members</h3><p class="stat-value">--</p></div>
      <div class="stat-card"><h3>Payments Due</h3><p class="stat-value">--</p></div>
    </div>
  `,
  styles: [`
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-top: 20px; }
    .stat-card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .stat-value { font-size: 2rem; font-weight: bold; margin: 8px 0 0; }
  `],
})
export class DashboardComponent {}
