import { Component, Input } from '@angular/core';
import { NgxChartsModule, Color } from '@swimlane/ngx-charts';
import { OCEAN_BREEZE_SCHEME } from '../chart-colors';

@Component({
  selector: 'app-pie-chart',
  standalone: true,
  imports: [NgxChartsModule],
  templateUrl: './pie-chart.component.html',
  styleUrl: './pie-chart.component.scss',
})
export class PieChartComponent {
  @Input() data: any[] = [];
  @Input() legend = true;
  @Input() legendTitle = '';
  @Input() labels = false;
  @Input() doughnut = true;
  @Input() arcWidth = 0.4;
  @Input() gradient = false;
  @Input() colorScheme: Color = OCEAN_BREEZE_SCHEME;
  @Input() view: [number, number] | undefined = undefined;
}
