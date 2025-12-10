import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Contact } from '../models/contact';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiContactService {
  constructor(private httpClient: HttpClient) { }

  getContacts(): Observable<Contact[]> {
    return this.httpClient.get<Contact[]>(`${environment.baseUrl}/contact/get`);
  }

  sortContacts(sort: string): Observable<Contact[]> {
    return this.httpClient.get<Contact[]>(`${environment.baseUrl}/contact/sort/${sort}`);
  }

  searchContacts(search: string) {
    return this.httpClient.get<Contact[]>(`${environment.baseUrl}/contact/search/${search}`);
  }
  addContact(contact: Contact): Observable<Contact> {
    return this.httpClient.post<Contact>(`${environment.baseUrl}/contact/add`, contact);
  }

  editContact(id: number,contact: Contact): Observable<Contact> {
    return this.httpClient.put<Contact>(`${environment.baseUrl}/contact/update/${id}`, contact);
  }

  removeContact(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${environment.baseUrl}/contact/delete/${id}`);
  }

}