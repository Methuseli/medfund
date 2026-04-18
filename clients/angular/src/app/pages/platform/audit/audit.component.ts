import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule, FormsModule, DataTableComponent, IconComponent],
  templateUrl: './audit.component.html',
  styleUrl: './audit.component.scss',
})
export class AuditComponent implements OnInit {
  allEvents: any[] = [];
  filteredEvents: any[] = [];
  entityFilter = '';
  actionFilter = '';
  dateFrom = '';
  dateTo = '';

  columns = [
    { key: 'entityType', label: 'Entity' },
    { key: 'entityId', label: 'Entity ID' },
    { key: 'action', label: 'Action', type: 'status' },
    { key: 'actorId', label: 'Actor' },
    { key: 'changes', label: 'Details' },
    { key: 'timestamp', label: 'Timestamp', type: 'date' },
  ];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getAuditEvents().subscribe({
      next: (data) => {
        this.allEvents = data.events || [];
        this.filteredEvents = this.allEvents;
      },
      error: () => {
        this.allEvents = [];
        this.filteredEvents = [];
      },
    });
  }

  applyFilters(): void {
    this.filteredEvents = this.allEvents.filter((e) => {
      if (this.entityFilter && e.entityType !== this.entityFilter) return false;
      if (this.actionFilter && e.action !== this.actionFilter) return false;
      if (this.dateFrom && new Date(e.timestamp) < new Date(this.dateFrom)) return false;
      if (this.dateTo && new Date(e.timestamp) > new Date(this.dateTo + 'T23:59:59')) return false;
      return true;
    });
  }

  exportCsv(): void {
    const headers = this.columns.map((c) => c.label).join(',');
    const rows = this.filteredEvents.map((e) =>
      this.columns.map((c) => `"${e[c.key] ?? ''}"`).join(',')
    );
    const csv = [headers, ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `audit-log-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }
}
