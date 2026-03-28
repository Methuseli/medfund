import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="stat-card" [class]="'stat-card-' + color">
      <div class="stat-label">{{ label }}</div>
      <div class="stat-value">{{ value }}</div>
      <div class="stat-subtitle" *ngIf="subtitle">{{ subtitle }}</div>
    </div>
  `,
  styles: [`
    .stat-card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); border-left: 4px solid #1a1a2e; }
    .stat-card-blue { border-left-color: #0d6efd; }
    .stat-card-green { border-left-color: #198754; }
    .stat-card-orange { border-left-color: #fd7e14; }
    .stat-card-red { border-left-color: #dc3545; }
    .stat-label { font-size: 0.85rem; color: #666; margin-bottom: 4px; }
    .stat-value { font-size: 1.8rem; font-weight: bold; }
    .stat-subtitle { font-size: 0.8rem; color: #999; margin-top: 4px; }
  `],
})
export class StatCardComponent {
  @Input() label = '';
  @Input() value: string | number = '--';
  @Input() subtitle = '';
  @Input() color = 'blue';
}
