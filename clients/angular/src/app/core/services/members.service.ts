import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Member {
  id: string;
  memberNumber: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  email: string;
  phone: string;
  status: string;
  groupId: string | null;
  schemeId: string | null;
  enrollmentDate: string;
  createdAt: string;
}

export interface Dependant {
  id: string;
  memberId: string;
  firstName: string;
  lastName: string;
  relationship: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class MembersService {
  constructor(private api: ApiService) {}

  list(): Observable<Member[]> {
    return this.api.get<Member[]>('/members');
  }

  getById(id: string): Observable<Member> {
    return this.api.get<Member>(`/members/${id}`);
  }

  search(q: string): Observable<Member[]> {
    return this.api.get<Member[]>('/members/search', { q });
  }

  getByStatus(status: string): Observable<Member[]> {
    return this.api.get<Member[]>(`/members/status/${status}`);
  }

  enroll(data: any): Observable<Member> {
    return this.api.post<Member>('/members', data);
  }

  activate(id: string): Observable<Member> {
    return this.api.post<Member>(`/members/${id}/activate`, {});
  }

  suspend(id: string): Observable<Member> {
    return this.api.post<Member>(`/members/${id}/suspend`, {});
  }

  terminate(id: string): Observable<Member> {
    return this.api.post<Member>(`/members/${id}/terminate`, {});
  }

  getDependants(memberId: string): Observable<Dependant[]> {
    return this.api.get<Dependant[]>(`/dependants/member/${memberId}`);
  }

  addDependant(data: any): Observable<Dependant> {
    return this.api.post<Dependant>('/dependants', data);
  }
}
