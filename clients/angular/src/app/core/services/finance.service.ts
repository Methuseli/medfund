import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Payment {
  id: string;
  paymentNumber: string;
  providerId: string;
  amount: number;
  currencyCode: string;
  status: string;
  paymentType: string;
  createdAt: string;
}

export interface PaymentRun {
  id: string;
  runNumber: string;
  status: string;
  totalAmount: number;
  currencyCode: string;
  paymentCount: number;
  createdAt: string;
}

export interface ProviderBalance {
  id: string;
  providerId: string;
  totalClaimed: number;
  totalApproved: number;
  totalPaid: number;
  outstandingBalance: number;
  currencyCode: string;
}

export interface Adjustment {
  id: string;
  adjustmentNumber: string;
  providerId: string;
  adjustmentType: string;
  amount: number;
  currencyCode: string;
  status: string;
  reason: string;
}

@Injectable({ providedIn: 'root' })
export class FinanceService {
  constructor(private api: ApiService) {}

  getPayments(): Observable<Payment[]> {
    return this.api.get<Payment[]>('/payments');
  }

  createPayment(data: any): Observable<Payment> {
    return this.api.post<Payment>('/payments', data);
  }

  getPaymentRuns(): Observable<PaymentRun[]> {
    return this.api.get<PaymentRun[]>('/payment-runs');
  }

  createPaymentRun(data: any): Observable<PaymentRun> {
    return this.api.post<PaymentRun>('/payment-runs', data);
  }

  executePaymentRun(id: string): Observable<PaymentRun> {
    return this.api.post<PaymentRun>(`/payment-runs/${id}/execute`, {});
  }

  getProviderBalances(): Observable<ProviderBalance[]> {
    return this.api.get<ProviderBalance[]>('/provider-balances');
  }

  getAdjustments(status: string): Observable<Adjustment[]> {
    return this.api.get<Adjustment[]>(`/adjustments/status/${status}`);
  }

  createAdjustment(data: any): Observable<Adjustment> {
    return this.api.post<Adjustment>('/adjustments', data);
  }
}
