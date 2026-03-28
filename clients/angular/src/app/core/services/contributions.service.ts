import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Scheme {
  id: string;
  name: string;
  description: string;
  schemeType: string;
  status: string;
  effectiveDate: string;
}

export interface Contribution {
  id: string;
  memberId: string;
  groupId: string | null;
  schemeId: string;
  amount: number;
  currencyCode: string;
  periodStart: string;
  periodEnd: string;
  status: string;
}

export interface Invoice {
  id: string;
  invoiceNumber: string;
  groupId: string | null;
  totalAmount: number;
  currencyCode: string;
  status: string;
  dueDate: string;
}

@Injectable({ providedIn: 'root' })
export class ContributionsService {
  constructor(private api: ApiService) {}

  getSchemes(): Observable<Scheme[]> {
    return this.api.get<Scheme[]>('/schemes');
  }

  getSchemeById(id: string): Observable<Scheme> {
    return this.api.get<Scheme>(`/schemes/${id}`);
  }

  createScheme(data: any): Observable<Scheme> {
    return this.api.post<Scheme>('/schemes', data);
  }

  getContributions(memberId: string): Observable<Contribution[]> {
    return this.api.get<Contribution[]>(`/contributions/member/${memberId}`);
  }

  getContributionsByStatus(status: string): Observable<Contribution[]> {
    return this.api.get<Contribution[]>(`/contributions/status/${status}`);
  }

  generateBilling(data: any): Observable<number> {
    return this.api.post<number>('/contributions/generate-billing', data);
  }

  getInvoicesByGroup(groupId: string): Observable<Invoice[]> {
    return this.api.get<Invoice[]>(`/invoices/group/${groupId}`);
  }

  getInvoicesByStatus(status: string): Observable<Invoice[]> {
    return this.api.get<Invoice[]>(`/invoices/status/${status}`);
  }
}
