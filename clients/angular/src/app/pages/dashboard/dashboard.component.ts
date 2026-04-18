import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { ClaimsService, Claim } from '../../core/services/claims.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, StatCardComponent, DataTableComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
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
