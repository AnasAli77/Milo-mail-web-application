import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Email } from '../models/email';
import { SearchCriteria } from '../models/searchCriteria';
import { environment } from '../../environments/environment.development';
import { Observable } from 'rxjs';

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // Current page index
  size: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class ApiEmailService {

  constructor(private httpClient: HttpClient) {
  }

  sendEmail(email: Email, files: File[]): Observable<Email> {
    const formData  = new FormData();
    const emailToSend = {
      ...email,
      attachments: (email.attachments || []).map(att => ({
        id: att.id,
        fileName: att.fileName,
        fileType: att.fileType,
        size: att.size
      }))
    };

    const emailBlob = new Blob([JSON.stringify(emailToSend)], { type: 'application/json' });
    formData.append("mail", emailBlob);

    files.forEach(file => formData.append("files", file))

    return this.httpClient.post<Email>(`${environment.baseUrl}/mail/send`, formData);
  }

  // Fetch emails by folder (e.g., /mail/folder/inbox)
  getEmails(folderName: string, page: number = 0, size: number = 9): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.httpClient.get<PageResponse<any>>(`${environment.baseUrl}/mail/${folderName}`, { params });
  }

  removeEmail(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${environment.baseUrl}/mail/delete/${id}`);
  }

  // update draft
  updateEmail(email: Email, files: File[]): Observable<Email> {
    const formData  = new FormData();

    // Strip the 'file' property from attachments before JSON serialization
    const emailToSend = {
      ...email,
      attachments: (email.attachments || []).map(att => ({
        id: att.id,
        fileName: att.fileName,
        fileType: att.fileType,
        size: att.size
      }))
    };

    const emailBlob = new Blob([JSON.stringify(emailToSend)], { type: 'application/json' });
    formData.append("mail", emailBlob);

    files.forEach(file => formData.append("files", file))

    return this.httpClient.put<Email>(`${environment.baseUrl}/mail/update`, formData);
  }

  markAsRead(id: number): Observable<void> {
    return this.httpClient.put<void>(`${environment.baseUrl}/mail/read/${id}`, {});
  }

  toggleStar(id: number): Observable<void> {
    return this.httpClient.put<void>(`${environment.baseUrl}/mail/star/${id}`, {});
  }

  getUserFolders(): Observable<string[]> {
    return this.httpClient.get<string[]>(`${environment.baseUrl}/folders`);
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
  filterEmails(criteria: SearchCriteria, page: number = 0, size: number = 9): Observable<PageResponse<any>> {
    let params = new HttpParams();
    if (criteria.query) params = params.set('body', criteria.query);
    if (criteria.from) params = params.set('sender', criteria.from);
    if (criteria.subject) params = params.set('subject', criteria.subject);
    if (criteria.priority) params = params.set('priority', criteria.priority.toString());
    if(criteria.hasAttachment) params=params.set('hasAttachment',criteria.hasAttachment);
    if (criteria.day) params = params.set('day', criteria.day);
    if (criteria.month) params = params.set('month', criteria.month);
    if (criteria.year) params = params.set('year', criteria.year);

    // Use the backend's sort/filter endpoint
    return this.httpClient.get<PageResponse<any>>(`${environment.baseUrl}/mail/filter`, { params });
  }

  searchEmails(searchBy: string, page: number = 0, size: number = 9): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('pageNumber', page)
      .set('pageSize', size);

    // Use the backend's sort/filter endpoint
    return this.httpClient.get<PageResponse<any>>(`${environment.baseUrl}/mail/search/${searchBy}`, { params });
  }

  sortEmailsBy(sortBy: string, folderName: string, page: number = 0, size: number = 9): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('pageNumber', page)
      .set('pageSize', size);

    return this.httpClient.get<PageResponse<any>>(`${environment.baseUrl}/mail/sort/${folderName}/${sortBy}`, { params });
  }

  getAttachmentContent(id: number): Observable<{data : string}> {
    return this.httpClient.get<{data : string}>(`${environment.baseUrl}/attachment/download/${id}`)
  }

  downloadAttachment(id: number): Observable<Blob> {
  return this.httpClient.get(`${environment.baseUrl}/attachment/download/${id}`, {
    responseType: 'blob'
  });
}
}
