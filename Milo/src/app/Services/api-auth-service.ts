import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ClientUser } from '../models/ClientUser';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiAuthService {
  constructor(private httpClient: HttpClient) {
  }

  login(user: ClientUser): Observable<HttpResponse<ClientUser>> {
    return this.httpClient.post<HttpResponse<ClientUser>>(`${environment.baseUrl}/auth/login`, user);
  }
  register(user: ClientUser): Observable<HttpResponse<ClientUser>> {
    return this.httpClient.post<HttpResponse<ClientUser>>(`${environment.baseUrl}/auth/register`, user);
  }
}