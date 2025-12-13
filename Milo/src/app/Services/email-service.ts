import {inject, Injectable, OnInit, signal} from '@angular/core';
import {Email} from '../models/email'
import {Router} from '@angular/router';
import {SearchCriteria} from '../models/searchCriteria';
import {ApiEmailService} from './api-email-service';
import {UserService} from './user-service';

@Injectable({
  providedIn: 'root'
})
export class EmailService implements OnInit{

  private api = inject(ApiEmailService);
  private router = inject(Router);
  private user = inject(UserService);

  // readonly systemFolders = ['inbox', 'starred', 'sent', 'drafts', 'trash'];
  // All folders signal
  folders = signal<string[]>([]);

  selectedEmail = signal<Email | null>(null);

  draftToEdit = signal<Email | null>(null);

  // Current Search State
  searchCriteria = signal<SearchCriteria>({});

  // NEW: State for Simple String Search
  currentSearchTerm = signal<string>('');

  // Sorting State (Default: Date)
  currentSortBy = signal<string>('Date');

  emailsSignal = signal<Email[]>([]);

  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  totalElements = signal<number>(0);
  readonly pageSize = 9;

  constructor() {
  }

  ngOnInit() {
    this.loadFolders();
  }

  // LOAD DATA FROM BACKEND
  loadEmailsForFolder(folder: string, page: number = 0) {
    if (folder === 'search') {
      this.api.filterEmails(this.searchCriteria()).subscribe({
        next: (data) => this.emailsSignal.set(data),
        error: (err) => console.error('Failed to search', err)
      });
    } else {
      this.api.getEmails(folder, page, this.pageSize).subscribe({
        next: (response) => {
          // Update Data
          this.emailsSignal.set(response.content);

          this.currentPage.set(response.number);
          this.totalPages.set(response.totalPages);
          this.totalElements.set(response.totalElements);
        },
        error: (err) => console.error(`Failed to load ${folder}`, err)
      });
    }
  }

  // UPDATED: Now processes string[] directly
  loadFolders() {
    this.api.getUserFolders().subscribe({
      next: (folderNames) => {
        this.folders.set([...(folderNames)]);

        console.log("LOL" + this.folders);
      },
      error: (err) => console.error('Failed to load user folders', err)
    });
  }

  // NEW: Sort Action
  sortEmails(folder: string, sortBy: string, page: number = 0) {
    this.currentSortBy.set(sortBy);

    this.api.sortEmailsBy(sortBy, folder, page, this.pageSize).subscribe({
      next: (response) => {
        this.emailsSignal.set(response.content);
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
      },
      error: (err) => console.error(`Failed to sort by ${sortBy}`, err)
    });
  }

  // NEW: Trigger Simple Search
  performSearch(query: string) {
    if (!query) return;

    this.currentSearchTerm.set(query);
    this.searchCriteria.set({}); // Clear advanced criteria to avoid confusion

    this.router.navigate(['/layout/search']);
    // If we are already on the search route, manual reload might be needed depending on router config,
    // but usually setting the signal and calling load handles it if the component reacts to params.
    // Explicitly calling load here guarantees update:
    this.loadEmailsForFolder('search');
  }

  changePage(folder: string, newPage: number) {
    if (newPage >= 0 && newPage < this.totalPages()) {
      this.loadEmailsForFolder(folder, newPage);
    }
  }

  // ACTIONS
  addFolder(folderName: string) {
    this.api.addFolder(folderName).subscribe(() => {
      this.folders.update(list => [...list, folderName.toLowerCase()]);
      this.loadFolders();
    });
  }

  renameFolder(oldName: string, newName: string) {
    this.api.renameFolder(oldName, newName).subscribe(() => {
      this.folders.update(list => list.map(f => f === oldName ? newName.toLowerCase() : f));
      this.loadFolders(); // Reload to update list
      // Refresh current list if we are in that folder
      this.loadEmailsForFolder(newName);
    });
  }

  deleteFolder(folderName: string) {
    // if (this.systemFolders.includes(folderName)) return;
    this.api.removeFolder(folderName).subscribe(() => {
      this.folders.update(list => list.filter(f => f !== folderName));
      this.loadFolders();
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
      sender: this.user.getName(), // Current user
      senderEmail: this.user.getEmail(),
      receiverEmails: data.email, // Array that backend converts to Queue
      time: new Date().toISOString(),
      subject: data.subject || '(No Subject)',
      body: data.body,
      attachments: data.attachments, // Handle attachments upload separately if needed
      read: true,
      active: false,
      starred: false,
      hasAttachment: false,
      priority: data.priority || 3
    };


    // hna ana lma bdoos (x) w elback 3ndo eldraft da be3ml draft gded elmafrood elback
    // DONE NEED TO TESTED

    if (draftEmail.id == 0) {
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
    } else {
      this.api.updateEmail(draftEmail).subscribe({
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
        error: (err) => console.error('Failed to update draft', err)
      })
    }
  }


  sendEmail(data: any) {
    // Map form data to Email model
    const newEmail: Email = {
      id: 0, // Backend generates ID
      folder: 'sent',
      sender: this.user.getName(), // Current user
      senderEmail: this.user.getEmail(),
      receiverEmails: data.email, // Array that backend converts to Queue
      time: new Date().toISOString(),
      subject: data.subject || '(No Subject)',
      body: data.body,
      attachments: data.attachments, // Handle attachments upload separately if needed
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

  deleteEmail(id: number) {
    this.api.removeEmail(id).subscribe({
      next: () => {
        // Remove from local list if present
        this.emailsSignal.update(emails => emails.filter(e => e.id !== id));
      },
      error: (err) => console.error('Failed to delete email', err)
    });
  }

  executeSearch(criteria: SearchCriteria) {
    this.searchCriteria.set(criteria);
    this.router.navigate(['/layout/search']);
    // The component will trigger loadEmailsForFolder('search')
  }

}
