import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { ProvidersService, Provider } from '../../core/services/providers.service';

@Component({
  selector: 'app-providers',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, StatCardComponent],
  template: `
    <h1>Provider Network</h1>
    <div class="stats-grid">
      <app-stat-card label="Active Providers" [value]="activeCount" color="green"></app-stat-card>
      <app-stat-card label="Pending Verification" [value]="pendingCount" color="orange"></app-stat-card>
      <app-stat-card label="Suspended" [value]="suspendedCount" color="red"></app-stat-card>
    </div>
    <div class="search-bar">
      <input type="text" [(ngModel)]="searchQuery" (input)="onSearch()" placeholder="Search providers..." />
    </div>
    <app-data-table title="Providers" [columns]="columns" [data]="filteredProviders"></app-data-table>
  `,
  styles: [`
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin: 20px 0; }
    .search-bar { margin-bottom: 16px; }
    .search-bar input { width: 100%; max-width: 400px; padding: 10px 14px; border: 1px solid #ddd; border-radius: 4px; }
  `],
})
export class ProvidersComponent implements OnInit {
  providers: Provider[] = [];
  filteredProviders: Provider[] = [];
  searchQuery = '';
  activeCount = 0; pendingCount = 0; suspendedCount = 0;

  columns = [
    { key: 'name', label: 'Name' },
    { key: 'practiceNumber', label: 'Practice #' },
    { key: 'ahfozNumber', label: 'AHFOZ #' },
    { key: 'specialty', label: 'Specialty' },
    { key: 'status', label: 'Status', type: 'status' },
  ];

  constructor(private providersService: ProvidersService) {}

  ngOnInit(): void {
    this.providersService.list().subscribe({
      next: (p) => {
        this.providers = p; this.filteredProviders = p;
        this.activeCount = p.filter(x => x.status === 'active').length;
        this.pendingCount = p.filter(x => x.status === 'pending_verification').length;
        this.suspendedCount = p.filter(x => x.status === 'suspended').length;
      },
      error: () => {}
    });
  }

  onSearch(): void {
    const q = this.searchQuery.toLowerCase();
    this.filteredProviders = q ? this.providers.filter(p => p.name.toLowerCase().includes(q) || (p.practiceNumber || '').toLowerCase().includes(q)) : this.providers;
  }
}
