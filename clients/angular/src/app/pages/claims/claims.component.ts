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
  templateUrl: './claims.component.html',
  styleUrl: './claims.component.scss',
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
