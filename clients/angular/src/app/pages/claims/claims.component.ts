import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { ClaimsService, Claim } from '../../core/services/claims.service';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, StatCardComponent],
  template: `
    <h1>Claims Management</h1>
    <div class="stats-grid">
      <app-stat-card label="Submitted" [value]="submitted" color="blue"></app-stat-card>
      <app-stat-card label="In Adjudication" [value]="inAdjudication" color="orange"></app-stat-card>
      <app-stat-card label="Adjudicated" [value]="adjudicated" color="green"></app-stat-card>
      <app-stat-card label="Rejected" [value]="rejected" color="red"></app-stat-card>
    </div>
    <div class="filters">
      <select [(ngModel)]="statusFilter" (change)="filterClaims()">
        <option value="">All Statuses</option>
        <option value="SUBMITTED">Submitted</option>
        <option value="VERIFIED">Verified</option>
        <option value="IN_ADJUDICATION">In Adjudication</option>
        <option value="ADJUDICATED">Adjudicated</option>
        <option value="REJECTED">Rejected</option>
        <option value="COMMITTED">Committed</option>
        <option value="PAID">Paid</option>
      </select>
    </div>
    <app-data-table
      title="Claims Queue"
      [columns]="columns"
      [data]="filteredClaims"
      (rowClick)="onClaimClick($event)">
    </app-data-table>
  `,
  styles: [`
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin: 20px 0; }
    .filters { margin-bottom: 16px; }
    select { padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem; }
  `],
})
export class ClaimsComponent implements OnInit {
  claims: Claim[] = [];
  filteredClaims: Claim[] = [];
  statusFilter = '';
  submitted = 0; inAdjudication = 0; adjudicated = 0; rejected = 0;

  columns = [
    { key: 'claimNumber', label: 'Claim #' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'claimedAmount', label: 'Claimed', type: 'currency' },
    { key: 'approvedAmount', label: 'Approved', type: 'currency' },
    { key: 'serviceDate', label: 'Service Date', type: 'date' },
    { key: 'createdAt', label: 'Submitted', type: 'date' },
  ];

  constructor(private claimsService: ClaimsService) {}

  ngOnInit(): void {
    this.claimsService.list().subscribe({
      next: (claims) => {
        this.claims = claims;
        this.filteredClaims = claims;
        this.submitted = claims.filter(c => c.status === 'SUBMITTED').length;
        this.inAdjudication = claims.filter(c => c.status === 'IN_ADJUDICATION').length;
        this.adjudicated = claims.filter(c => c.status === 'ADJUDICATED').length;
        this.rejected = claims.filter(c => c.status === 'REJECTED').length;
      },
      error: () => {}
    });
  }

  filterClaims(): void {
    this.filteredClaims = this.statusFilter
      ? this.claims.filter(c => c.status === this.statusFilter)
      : this.claims;
  }

  onClaimClick(claim: Claim): void {
    console.log('Claim clicked:', claim);
  }
}
