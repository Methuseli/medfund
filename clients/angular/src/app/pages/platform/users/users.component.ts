import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatCardComponent } from '../../../shared/components/stat-card/stat-card.component';
import { DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { IconComponent } from '../../../shared/components/icon/icon.component';
import { AdminService, Role } from '../../../core/services/admin.service';

interface PlatformUser {
  id: string;
  name: string;
  email: string;
  role: string;
  status: string;
  lastLogin: string;
  createdAt: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, StatCardComponent, DataTableComponent, IconComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  users: PlatformUser[] = [];
  roles: Role[] = [];

  userColumns = [
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'role', label: 'Role' },
    { key: 'status', label: 'Status', type: 'status' },
    { key: 'lastLogin', label: 'Last Login', type: 'date' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ];

  roleColumns = [
    { key: 'name', label: 'Role Key' },
    { key: 'displayName', label: 'Display Name' },
    { key: 'description', label: 'Description' },
    { key: 'isSystem', label: 'System', type: 'boolean' },
  ];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getRoles().subscribe({
      next: (r) => (this.roles = r),
      error: () => {},
    });

    this.adminService.getUsers().subscribe({
      next: (u) => (this.users = u),
      error: () => {},
    });
  }

  get activeUsers(): number {
    return this.users.filter((u) => u.status === 'ACTIVE').length;
  }
}
