import { HttpClient } from '@angular/common/http';
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

  login(user: ClientUser) :Observable<ClientUser> {
    return this.httpClient.post<ClientUser>(`${environment.baseUrl}/login`, user);
  }
  register(user: ClientUser) :Observable<ClientUser> {
    return this.httpClient.post<ClientUser>(`${environment.baseUrl}/register`, user);
  }
}