import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Email } from '../models/email';
import { SearchCriteria } from '../models/searchCriteria';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiEmailService {

  constructor(private httpClient: HttpClient) {
  }
  sendEmail(email: Email): Observable<Email> {
    return this.httpClient.post<Email>(`${environment.baseUrl}/mail`, email);
  }

  // Fetch emails by folder (e.g., /mail/folder/inbox)
  getEmails(folderName: string): Observable<Email[]> {
    return this.httpClient.get<Email[]>(`${environment.baseUrl}/mail/folder/${folderName}`);
  }

  getAllMails(): Observable<Email[]> {
    // return this.httpClient.get<Email[]>(`${environment.baseUrl}`);
    return this.httpClient.get<Email[]>(`${environment.baseUrl}/mail`);
  }

  removeEmail(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${environment.baseUrl}/mail/delete/${id}`);
  }

  // NEW: Mark as read
  markAsRead(id: number): Observable<void> {
    return this.httpClient.put<void>(`${environment.baseUrl}/mail/read/${id}`, {});
  }

  // NEW: Toggle Star
  toggleStar(id: number): Observable<void> {
    return this.httpClient.put<void>(`${environment.baseUrl}/mail/star/${id}`, {});
  }

  // Batch move
  moveToFolder(folderName: string, emailIds: number[]): Observable<void> {
    return this.httpClient.put<void>(`${environment.baseUrl}/mail/move`, { folder: folderName, ids: emailIds });
  }

  // Folder Operations
  addFolder(folderName: string): Observable<any> {
    return this.httpClient.post(`${environment.baseUrl}/folders`, { name: folderName });
  }

  removeFolder(folderName: string): Observable<void> {
    return this.httpClient.delete<void>(`${environment.baseUrl}/folders/${folderName}`);
  }

  renameFolder(oldName: string, newName: string): Observable<void> {
    return this.httpClient.put<void>(`${environment.baseUrl}/folders/${oldName}`, { newName });
  }

  // Search/Filter
  filterEmails(criteria: SearchCriteria): Observable<Email[]> {
    let params = new HttpParams();
    if (criteria.query) params = params.set('query', criteria.query);
    if (criteria.from) params = params.set('sender', criteria.from);
    if (criteria.subject) params = params.set('subject', criteria.subject);
    if (criteria.priority) params = params.set('priority', criteria.priority.toString());

    // Use the backend's sort/filter endpoint
    return this.httpClient.get<Email[]>(`${environment.baseUrl}/mail/search`, { params });
  }
}