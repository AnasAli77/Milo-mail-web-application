import { inject, Injectable, signal } from '@angular/core';
import { Email } from '../models/email'
import { Router } from '@angular/router';
import { SearchCriteria } from '../models/searchCriteria';
import { ApiEmailService } from './api-email-service';

@Injectable({
  providedIn: 'root'
})
export class EmailService {

  private api = inject(ApiEmailService);
  private router = inject(Router);

  readonly systemFolders = ['inbox', 'starred', 'sent', 'drafts', 'trash'];
  // All folders signal
  folders = signal<string[]>([...this.systemFolders]);

  selectedEmail = signal<Email | null>(null);

  draftToEdit = signal<Email | null>(null);

  // Current Search State
  searchCriteria = signal<SearchCriteria>({});

  emailsSignal = signal<Email[]>([]);

  // LOAD DATA FROM BACKEND
  loadEmailsForFolder(folder: string) {
    if (folder === 'search') {
      this.api.filterEmails(this.searchCriteria()).subscribe({
        next: (data) => this.emailsSignal.set(data),
        error: (err) => console.error('Failed to search', err)
      });
    } else {
      this.api.getAllMails().subscribe({
        next: (data) => this.emailsSignal.set(data),
        error: (err) => console.error(`Failed to load ${folder}`, err)
      });
    }
  }

  // ACTIONS
  addFolder(folderName: string) {
    this.api.addFolder(folderName).subscribe(() => {
      this.folders.update(list => [...list, folderName.toLowerCase()]);
    });
  }

  renameFolder(oldName: string, newName: string) {
    this.api.renameFolder(oldName, newName).subscribe(() => {
      this.folders.update(list => list.map(f => f === oldName ? newName.toLowerCase() : f));
      // Refresh current list if we are in that folder
      this.loadEmailsForFolder(newName);
    });
  }

  deleteFolder(folderName: string) {
    if (this.systemFolders.includes(folderName)) return;
    this.api.removeFolder(folderName).subscribe(() => {
      this.folders.update(list => list.filter(f => f !== folderName));
    });
  }

  // --- EMAIL ACTIONS ---

  // Used by EmailList to get data (now just returns signal, triggering is separate)
  filterEmails(folder: string) {
    // We assume loadEmailsForFolder(folder) is called by the component when route changes
    return this.emailsSignal();
  }


  setSelectedEmail(email: Email) {
    this.selectedEmail.set(email);

    // Logic: If unread, mark as read locally AND on backend
    if (!email.read) {
      // 1. Optimistic Update (UI updates immediately)
      this.emailsSignal.update(all =>
        all.map(e => e.id === email.id ? { ...e, read: true } : e)
      );

      // 2. API Call
      this.api.markAsRead(email.id).subscribe({
        error: (err) => console.error('Failed to mark as read', err)
      });
    }
  }

  getAdjacentEmailId(currentId: number, folder: string, direction: 'next' | 'prev'): number | null {
    const emailsInFolder = this.filterEmails(folder);
    const currentIndex = emailsInFolder.findIndex(e => e.id === currentId);

    // If current email isn't in the list, we can't determine next/prev
    if (currentIndex === -1) return null;

    // Next = Down the list (Index + 1)
    // Prev = Up the list (Index - 1)
    const adjacentIndex = direction === 'next' ? currentIndex + 1 : currentIndex - 1;

    if (adjacentIndex >= 0 && adjacentIndex < emailsInFolder.length) {
      return emailsInFolder[adjacentIndex].id;
    }

    return null;
  }

  setStarredEmail(email: Email) {
    // 1. Optimistic Update
    this.emailsSignal.update(emails =>
      emails.map(e => e.id === email.id ? { ...e, starred: !e.starred } : e)
    );

    // 2. API Call
    this.api.toggleStar(email.id).subscribe({
      error: (err) => {
        console.error('Failed to toggle star', err);
        // Revert on error
        this.emailsSignal.update(emails =>
          emails.map(e => e.id === email.id ? { ...e, starred: !e.starred } : e)
        );
      }
    });
  }

  moveEmails(emailIds: number[], targetFolder: string) { // Updated ID type to string[]
    this.api.moveToFolder(targetFolder, emailIds).subscribe(() => {
      // Optimistic UI update: remove from current view
      this.emailsSignal.update(emails => emails.filter(e => !emailIds.includes(e.id)));
      if (this.selectedEmail() && emailIds.includes(this.selectedEmail()!.id)) {
        this.selectedEmail.set(null);
      }
    });
  }

  // --- Helper methods that might need adjustment ---
  editDraft(email: Email) {
    this.draftToEdit.set(email);
    this.router.navigate(['/layout/drafts/compose']);
  }


  saveDraft(data: any) {
    const currentDraft = this.draftToEdit();
    const draftEmail: Email = {
      id: currentDraft ? currentDraft.id : 0,
      folder: 'drafts',
      sender: 'me', // Current user
      senderEmail: 'me@milo.com',
      receiverEmail: data.email, // Ensure array
      time: new Date().toISOString(),
      subject: data.subject || '(No Subject)',
      body: data.body,
      attachments: [], // Handle attachments upload separately if needed
      read: true,
      active: false,
      starred: false,
      hasAttachment: false,
      priority: data.priority || 3
    };

    this.api.sendEmail(draftEmail).subscribe({
      next: (savedEmail) => {
        this.draftToEdit.set(null);
        // If we are currently viewing Drafts, update the list
        if (this.router.url.includes('/drafts')) {
          // If it was an edit, update it; if new, add it
          this.emailsSignal.update(list => {
            const exists = list.find(e => e.id === savedEmail.id);
            return exists 
              ? list.map(e => e.id === savedEmail.id ? savedEmail : e)
              : [savedEmail, ...list];
          });
        }
      },
      error: (err) => console.error('Failed to save draft', err)
    });
  }

  sendEmail(data: any) {
    // Map form data to Email model
    const newEmail: Email = {
      id: 0, // Backend generates ID
      folder: 'sent',
      sender: 'me', // Current user
      senderEmail: 'me@milo.com',
      receiverEmail: data.email, // Ensure array
      time: new Date().toISOString(),
      subject: data.subject || '(No Subject)',
      body: data.body,
      attachments: [], // Handle attachments upload separately if needed
      read: true,
      active: false,
      starred: false,
      hasAttachment: false,
      priority: data.priority || 3
    };

    this.api.sendEmail(newEmail).subscribe(savedEmail => {
      this.draftToEdit.set(null);
      // If we are in 'sent' folder, add to list, otherwise just navigate
      if (this.router.url.includes('/sent')) {
        this.emailsSignal.update(list => [savedEmail, ...list]);
      }
    });
  }

  executeSearch(criteria: SearchCriteria) {
    this.searchCriteria.set(criteria);
    this.router.navigate(['/layout/search']);
    // The component will trigger loadEmailsForFolder('search')
  }

}