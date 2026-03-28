import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Claim {
  id: string;
  claimNumber: string;
  memberId: string;
  providerId: string;
  status: string;
  serviceDate: string;
  claimedAmount: number;
  approvedAmount: number | null;
  currencyCode: string;
  rejectionReason: string | null;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ClaimsService {
  constructor(private api: ApiService) {}

  list(): Observable<Claim[]> {
    return this.api.get<Claim[]>('/claims');
  }

  getById(id: string): Observable<Claim> {
    return this.api.get<Claim>(`/claims/${id}`);
  }

  getByStatus(status: string): Observable<Claim[]> {
    return this.api.get<Claim[]>(`/claims/status/${status}`);
  }

  submit(data: any): Observable<Claim> {
    return this.api.post<Claim>('/claims', data);
  }

  adjudicate(id: string): Observable<Claim> {
    return this.api.post<Claim>(`/claims/${id}/adjudicate`, {});
  }
}
