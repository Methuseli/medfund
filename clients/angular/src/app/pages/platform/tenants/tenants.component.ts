import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { DataTableComponent, TableAction } from '../../../shared/components/data-table/data-table.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { AdminService, Tenant } from '../../../core/services/admin.service';
import { TenantService } from '../../../core/services/tenant.service';

@Component({
  selector: 'app-tenants',
  standalone: true,
  imports: [CommonModule, StatCardComponent, DataTableComponent, IconComponent],
  templateUrl: './tenants.component.html',
  styleUrl: './tenants.component.scss',
})
export class TenantsComponent implements OnInit {
  tenants: Tenant[] = [];
  showCreateModal = false;

  columns = [
    { key: 'name', label: 'Name' },
    { key: 'slug', label: 'Slug' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'contactEmail', label: 'Contact' },
    { key: 'countryCode', label: 'Country' },
    { key: 'membershipModel', label: 'Model' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ];

  tableActions: TableAction[] = [
    {
      label: 'Enter Admin',
      icon: 'external-link',
      handler: (row: Tenant) => this.enterTenantAdmin(row),
    },
    {
      label: 'Suspend',
      icon: 'alert-circle',
      handler: (row: Tenant) => this.suspendTenant(row),
    },
  ];

  constructor(
    private adminService: AdminService,
    private tenantService: TenantService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadTenants();
  }

  get activeTenants(): number {
    return this.tenants.filter((t) => t.status?.toLowerCase() === 'active').length;
  }

  get suspendedTenants(): number {
    return this.tenants.filter((t) => t.status?.toLowerCase() === 'suspended').length;
  }

  loadTenants(): void {
    this.adminService.getTenants().subscribe({
      next: (t) => (this.tenants = t),
      error: () => {},
    });
  }

  onTenantClick(tenant: Tenant): void {
    this.enterTenantAdmin(tenant);
  }

  enterTenantAdmin(tenant: Tenant): void {
    this.tenantService.setTenant({
      id: tenant.id,
      name: tenant.name,
      slug: tenant.slug,
      status: tenant.status,
      branding: undefined as any,
      timezone: '',
    });
    this.router.navigate(['/tenant/dashboard']);
  }

  suspendTenant(tenant: Tenant): void {
    if (tenant.status?.toLowerCase() === 'suspended') return;
    this.adminService.suspendTenant(tenant.id).subscribe({
      next: () => this.loadTenants(),
      error: () => {},
    });
  }

  createTenant(name: string, slug: string, email: string, country: string, model: string): void {
    if (!name || !slug) return;
    this.adminService
      .createTenant({ name, slug, contactEmail: email, countryCode: country, membershipModel: model })
      .subscribe({
        next: () => {
          this.showCreateModal = false;
          this.loadTenants();
        },
        error: () => {},
      });
  }
}
