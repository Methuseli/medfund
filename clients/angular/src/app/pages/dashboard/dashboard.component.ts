import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { ClaimsService, Claim } from '../../core/services/claims.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, StatCardComponent, DataTableComponent],
  template: `
    <h1>Dashboard</h1>
    <div class="stats-grid">
      <app-stat-card label="Total Claims" [value]="totalClaims" color="blue"></app-stat-card>
      <app-stat-card label="Pending Claims" [value]="pendingClaims" color="orange"></app-stat-card>
      <app-stat-card label="Approved Today" [value]="approvedToday" color="green"></app-stat-card>
      <app-stat-card label="Rejected" [value]="rejectedClaims" color="red"></app-stat-card>
    </div>
    <div class="section">
      <app-data-table
        title="Recent Claims"
        [columns]="claimColumns"
        [data]="recentClaims">
      </app-data-table>
    </div>
  `,
  styles: [`
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin: 20px 0; }
    .section { margin-top: 24px; }
  `],
})
export class DashboardComponent implements OnInit {
  totalClaims = 0;
  pendingClaims = 0;
  approvedToday = 0;
  rejectedClaims = 0;
  recentClaims: Claim[] = [];

  claimColumns = [
    { key: 'claimNumber', label: 'Claim #' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'claimedAmount', label: 'Amount', type: 'currency' },
    { key: 'serviceDate', label: 'Service Date', type: 'date' },
    { key: 'createdAt', label: 'Submitted', type: 'date' },
  ];

  constructor(private claimsService: ClaimsService) {}

  ngOnInit(): void {
    this.claimsService.list().subscribe({
      next: (claims) => {
        this.recentClaims = claims.slice(0, 10);
        this.totalClaims = claims.length;
        this.pendingClaims = claims.filter(c => ['SUBMITTED', 'VERIFIED', 'IN_ADJUDICATION', 'PENDING_INFO'].includes(c.status)).length;
        this.approvedToday = claims.filter(c => c.status === 'ADJUDICATED').length;
        this.rejectedClaims = claims.filter(c => c.status === 'REJECTED').length;
      },
      error: () => { /* API not available yet */ }
    });
  }
}
