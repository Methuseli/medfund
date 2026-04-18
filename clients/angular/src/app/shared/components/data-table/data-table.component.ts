import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IconComponent } from '../icon/icon.component';

export interface TableColumn {
  key: string;
  label: string;
  type?: string;
  class?: string;
  sortable?: boolean;
}

export interface TableAction {
  label: string;
  icon?: string;
  color?: string;
  handler: (row: any) => void;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.scss',
})
export class DataTableComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() actions: TableAction[] = [];
  @Input() showActions = false;
  @Input() emptyMessage = 'No data found';
  @Input() searchable = true;
  @Input() searchPlaceholder = 'Search...';
  @Input() pageSize = 10;
  @Output() rowClick = new EventEmitter<any>();

  searchTerm = '';
  sortKey = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  currentPage = 1;

  Math = Math;

  get filteredData(): any[] {
    let result = [...this.data];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter((row) =>
        this.columns.some((col) => {
          const val = row[col.key];
          return val != null && val.toString().toLowerCase().includes(term);
        })
      );
    }

    if (this.sortKey) {
      result.sort((a, b) => {
        const va = a[this.sortKey] ?? '';
        const vb = b[this.sortKey] ?? '';
        const cmp = va < vb ? -1 : va > vb ? 1 : 0;
        return this.sortDirection === 'asc' ? cmp : -cmp;
      });
    }

    return result;
  }

  get totalPages(): number {
    return Math.ceil(this.filteredData.length / this.pageSize);
  }

  get paginatedData(): any[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.filteredData.slice(start, start + this.pageSize);
  }

  get visiblePages(): number[] {
    const pages: number[] = [];
    const total = this.totalPages;
    const current = this.currentPage;
    const start = Math.max(1, current - 2);
    const end = Math.min(total, start + 4);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  }

  onSearch(): void {
    this.currentPage = 1;
  }

  sort(key: string): void {
    if (this.sortKey === key) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortKey = key;
      this.sortDirection = 'asc';
    }
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
}
