import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { ContributionsService, Scheme, Contribution } from '../../core/services/contributions.service';

@Component({
  selector: 'app-contributions',
  standalone: true,
  imports: [CommonModule, DataTableComponent, StatCardComponent],
  templateUrl: './contributions.component.html',
  styleUrl: './contributions.component.scss',
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
