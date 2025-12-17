import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FilterRule } from '../models/FilterRule';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class ApiFilterService {

  private readonly baseUrl = `${environment.baseUrl}/filter`;

  constructor(private http: HttpClient) { }

  getRules(): Observable<FilterRule[]> {
    return this.http.get<FilterRule[]>(`${this.baseUrl}/all`);
  }

  addRule(rule: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/add`, rule);
  }

  deleteRule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }
}
