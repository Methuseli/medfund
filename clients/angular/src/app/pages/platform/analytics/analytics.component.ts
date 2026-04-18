import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { LineChartComponent } from '../../../shared/components/charts/line-chart/line-chart.component';
import { BarChartComponent } from '../../../shared/components/charts/bar-chart/bar-chart.component';
import { AreaChartComponent } from '../../../shared/components/charts/area-chart/area-chart.component';
import { PlatformDashboardService, PlatformStats, SystemHealthItem } from '../../../core/services/platform-dashboard.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    StatCardComponent,
    LineChartComponent,
    BarChartComponent,
    AreaChartComponent,
  ],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.scss',
})
export class AnalyticsComponent implements OnInit {
  selectedPeriod = '12m';
  claimsProcessed = 0;
  avgProcessingTime = 0;
  totalRevenue = 0;
  memberGrowthPct = 0;

  claimsOverTime: any[] = [];
  revenueByTenant: any[] = [];
  memberGrowthData: any[] = [];

  perfMetrics: { service: string; avgResponse: number; p95Response: number; uptime: number; errors: number }[] = [];

  constructor(private dashboardService: PlatformDashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getPlatformStats().subscribe((stats) => {
      this.claimsProcessed = stats.totalClaims;
      this.totalRevenue = stats.monthlyRevenue;
      this.memberGrowthPct = stats.userGrowth;
    });

    this.dashboardService.getSystemHealth().subscribe((health) => {
      this.perfMetrics = health.map((h) => ({
        service: h.service,
        avgResponse: h.latency ?? 0,
        p95Response: 0,
        uptime: 0,
        errors: 0,
      }));
    });

    this.dashboardService.getClaimsOverTime().subscribe((d) => (this.claimsOverTime = d));
    this.dashboardService.getRevenueByTenant().subscribe((d) => (this.revenueByTenant = d));
    this.dashboardService.getMemberGrowth().subscribe((d) => (this.memberGrowthData = d));
  }
}
