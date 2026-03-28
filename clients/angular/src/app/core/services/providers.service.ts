import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Provider {
  id: string;
  name: string;
  practiceNumber: string;
  ahfozNumber: string;
  specialty: string;
  email: string;
  phone: string;
  status: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ProvidersService {
  constructor(private api: ApiService) {}

  list(): Observable<Provider[]> {
    return this.api.get<Provider[]>('/providers');
  }

  getById(id: string): Observable<Provider> {
    return this.api.get<Provider>(`/providers/${id}`);
  }

  search(q: string): Observable<Provider[]> {
    return this.api.get<Provider[]>('/providers/search', { q });
  }

  getByStatus(status: string): Observable<Provider[]> {
    return this.api.get<Provider[]>(`/providers/status/${status}`);
  }

  onboard(data: any): Observable<Provider> {
    return this.api.post<Provider>('/providers', data);
  }

  verify(id: string): Observable<Provider> {
    return this.api.post<Provider>(`/providers/${id}/verify`, {});
  }

  suspend(id: string): Observable<Provider> {
    return this.api.post<Provider>(`/providers/${id}/suspend`, {});
  }
}
