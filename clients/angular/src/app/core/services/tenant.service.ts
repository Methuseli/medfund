import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Tenant {
  id: string;
  name: string;
  slug: string;
  status: string;
  branding: string;
  timezone: string;
}

@Injectable({ providedIn: 'root' })
export class TenantService {
  private currentTenant = new BehaviorSubject<Tenant | null>(null);
  tenant$ = this.currentTenant.asObservable();

  constructor(private http: HttpClient) {}

  setTenant(tenant: Tenant): void {
    this.currentTenant.next(tenant);
  }

  getTenant(): Tenant | null {
    return this.currentTenant.getValue();
  }

  getTenantId(): string {
    return this.currentTenant.getValue()?.id || '';
  }
}
