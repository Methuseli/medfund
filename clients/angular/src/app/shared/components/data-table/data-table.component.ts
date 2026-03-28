import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="table-container">
      <div class="table-header" *ngIf="title">
        <h3>{{ title }}</h3>
        <div class="table-actions">
          <ng-content select="[table-actions]"></ng-content>
        </div>
      </div>
      <table>
        <thead>
          <tr>
            <th *ngFor="let col of columns">{{ col.label }}</th>
            <th *ngIf="showActions">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let row of data" (click)="rowClick.emit(row)">
            <td *ngFor="let col of columns">
              <span *ngIf="!col.type" [class]="col.class">{{ row[col.key] }}</span>
              <span *ngIf="col.type === 'status'" class="badge" [class]="'badge-' + row[col.key]?.toLowerCase()">
                {{ row[col.key] }}
              </span>
              <span *ngIf="col.type === 'currency'">{{ row[col.key] | number:'1.2-2' }} {{ row['currencyCode'] || 'USD' }}</span>
              <span *ngIf="col.type === 'date'">{{ row[col.key] | date:'mediumDate' }}</span>
            </td>
            <td *ngIf="showActions">
              <ng-content select="[row-actions]"></ng-content>
            </td>
          </tr>
          <tr *ngIf="data.length === 0">
            <td [attr.colspan]="columns.length + (showActions ? 1 : 0)" class="empty">
              {{ emptyMessage }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .table-container { background: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }
    .table-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #eee; }
    .table-header h3 { margin: 0; }
    table { width: 100%; border-collapse: collapse; }
    th { text-align: left; padding: 12px 16px; background: #fafafa; font-weight: 600; font-size: 0.85rem; color: #666; border-bottom: 1px solid #eee; }
    td { padding: 12px 16px; border-bottom: 1px solid #f0f0f0; }
    tr:hover { background: #f9f9f9; cursor: pointer; }
    .empty { text-align: center; color: #999; padding: 40px; }
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; }
    .badge-active, .badge-adjudicated, .badge-paid, .badge-approved { background: #e6f4ea; color: #1e7e34; }
    .badge-pending, .badge-submitted, .badge-draft { background: #fff3cd; color: #856404; }
    .badge-rejected, .badge-suspended, .badge-overdue { background: #f8d7da; color: #721c24; }
    .badge-enrolled, .badge-verified { background: #d1ecf1; color: #0c5460; }
  `],
})
export class DataTableComponent {
  @Input() title = '';
  @Input() columns: { key: string; label: string; type?: string; class?: string }[] = [];
  @Input() data: any[] = [];
  @Input() showActions = false;
  @Input() emptyMessage = 'No data found';
  @Output() rowClick = new EventEmitter<any>();
}
