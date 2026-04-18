import { Component, Input } from '@angular/core';
import { NgxChartsModule, Color } from '@swimlane/ngx-charts';
import { OCEAN_BREEZE_SCHEME } from '../chart-colors';

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [NgxChartsModule],
  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.scss',
})
export class BarChartComponent {
  @Input() data: any[] = [];
  @Input() xAxisLabel = '';
  @Input() yAxisLabel = '';
  @Input() legend = false;
  @Input() legendTitle = '';
  @Input() gradient = true;
  @Input() colorScheme: Color = OCEAN_BREEZE_SCHEME;
}
