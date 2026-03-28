import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { ContributionsService, Scheme, Contribution } from '../../core/services/contributions.service';

@Component({
  selector: 'app-contributions',
  standalone: true,
  imports: [CommonModule, DataTableComponent, StatCardComponent],
  template: `
    <h1>Contributions</h1>
    <div class="stats-grid">
      <app-stat-card label="Active Schemes" [value]="schemeCount" color="blue"></app-stat-card>
      <app-stat-card label="Pending Contributions" [value]="pendingCount" color="orange"></app-stat-card>
      <app-stat-card label="Paid" [value]="paidCount" color="green"></app-stat-card>
      <app-stat-card label="Overdue" [value]="overdueCount" color="red"></app-stat-card>
    </div>
    <app-data-table title="Schemes" [columns]="schemeColumns" [data]="schemes"></app-data-table>
  `,
  styles: [`
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; margin: 20px 0; }
  `],
})
export class ContributionsComponent implements OnInit {
  schemes: Scheme[] = [];
  schemeCount = 0; pendingCount = 0; paidCount = 0; overdueCount = 0;

  schemeColumns = [
    { key: 'name', label: 'Scheme Name' },
    { key: 'schemeType', label: 'Type' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'effectiveDate', label: 'Effective Date', type: 'date' },
  ];

  constructor(private contribService: ContributionsService) {}

  ngOnInit(): void {
    this.contribService.getSchemes().subscribe({
      next: (schemes) => { this.schemes = schemes; this.schemeCount = schemes.filter(s => s.status === 'active').length; },
      error: () => {}
    });
  }
}
