import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {FilterRule} from '../models/FilterRule';
import {environment} from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class ApiFilterService {


  constructor(private http: HttpClient) {}

  getRules(): Observable<FilterRule[]> {
    return this.http.get<FilterRule[]>(`${environment.baseUrl}`);
  }

  addRule(rule: FilterRule): Observable<FilterRule> {
    return this.http.post<FilterRule>(`${environment.baseUrl}`, rule);
  }

  deleteRule(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/${id}`);
  }
}
