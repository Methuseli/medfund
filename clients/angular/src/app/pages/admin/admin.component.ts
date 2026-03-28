import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { AdminService, Tenant, Role, ScheduledJob } from '../../core/services/admin.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, DataTableComponent, StatCardComponent],
  template: `
    <h1>Administration</h1>

    <h2>Tenants</h2>
    <app-data-table [columns]="tenantColumns" [data]="tenants"></app-data-table>

    <h2 style="margin-top:32px">Roles</h2>
    <app-data-table [columns]="roleColumns" [data]="roles"></app-data-table>

    <h2 style="margin-top:32px">Scheduled Jobs</h2>
    <app-data-table [columns]="jobColumns" [data]="jobs"></app-data-table>

    <h2 style="margin-top:32px">Audit Log</h2>
    <app-data-table [columns]="auditColumns" [data]="auditEvents"></app-data-table>
  `,
})
export class AdminComponent implements OnInit {
  tenants: Tenant[] = [];
  roles: Role[] = [];
  jobs: ScheduledJob[] = [];
  auditEvents: any[] = [];

  tenantColumns = [
    { key: 'name', label: 'Name' },
    { key: 'slug', label: 'Slug' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'contactEmail', label: 'Contact' },
    { key: 'countryCode', label: 'Country' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ];

  roleColumns = [
    { key: 'name', label: 'Name' },
    { key: 'displayName', label: 'Display Name' },
    { key: 'description', label: 'Description' },
  ];

  jobColumns = [
    { key: 'jobType', label: 'Type' },
    { key: 'name', label: 'Name' },
    { key: 'cronExpression', label: 'Schedule' },
    { key: 'isEnabled', label: 'Enabled' },
    { key: 'lastExecutedAt', label: 'Last Run', type: 'date' },
    { key: 'nextExecutionAt', label: 'Next Run', type: 'date' },
  ];

  auditColumns = [
    { key: 'entityType', label: 'Entity' },
    { key: 'action', label: 'Action' },
    { key: 'actorId', label: 'Actor' },
    { key: 'timestamp', label: 'Time', type: 'date' },
  ];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getTenants().subscribe({ next: (t) => this.tenants = t, error: () => {} });
    this.adminService.getRoles().subscribe({ next: (r) => this.roles = r, error: () => {} });
    this.adminService.getScheduledJobs().subscribe({ next: (j) => this.jobs = j, error: () => {} });
    this.adminService.getAuditEvents().subscribe({ next: (a) => this.auditEvents = a.events || [], error: () => {} });
  }
}
