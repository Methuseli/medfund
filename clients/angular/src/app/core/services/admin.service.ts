import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Tenant {
  id: string;
  name: string;
  slug: string;
  status: string;
  contactEmail: string;
  countryCode: string;
  membershipModel: string;
  createdAt: string;
}

export interface Role {
  id: string;
  name: string;
  displayName: string;
  description: string;
  isSystem: boolean;
}

export interface ScheduledJob {
  id: string;
  jobType: string;
  name: string;
  cronExpression: string;
  isEnabled: boolean;
  settings: string;
  lastExecutedAt: string | null;
  nextExecutionAt: string | null;
}

export interface AuditEvent {
  events: any[];
  total: number;
  page: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private api: ApiService) {}

  // Tenants (super admin)
  getTenants(): Observable<Tenant[]> {
    return this.api.get<Tenant[]>('/tenants');
  }

  createTenant(data: any): Observable<Tenant> {
    return this.api.post<Tenant>('/tenants', data);
  }

  suspendTenant(id: string): Observable<Tenant> {
    return this.api.post<Tenant>(`/tenants/${id}/suspend`, {});
  }

  // Roles
  getRoles(): Observable<Role[]> {
    return this.api.get<Role[]>('/roles');
  }

  createRole(data: any): Observable<Role> {
    return this.api.post<Role>('/roles', data);
  }

  // Scheduled Jobs
  getScheduledJobs(): Observable<ScheduledJob[]> {
    return this.api.get<ScheduledJob[]>('/scheduled-jobs');
  }

  updateJob(id: string, data: any): Observable<ScheduledJob> {
    return this.api.put<ScheduledJob>(`/scheduled-jobs/${id}`, data);
  }

  enableJob(id: string): Observable<ScheduledJob> {
    return this.api.post<ScheduledJob>(`/scheduled-jobs/${id}/enable`, {});
  }

  disableJob(id: string): Observable<ScheduledJob> {
    return this.api.post<ScheduledJob>(`/scheduled-jobs/${id}/disable`, {});
  }

  // Audit
  getAuditEvents(params?: Record<string, string>): Observable<AuditEvent> {
    return this.api.get<AuditEvent>('/audit/events', params);
  }
}
