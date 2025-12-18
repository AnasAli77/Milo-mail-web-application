import { inject, Injectable, OnInit, signal, NgZone } from '@angular/core';
import { Alert } from './alert';
import { Email } from '../models/email'
import { Router } from '@angular/router';
import { SearchCriteria } from '../models/searchCriteria';
import { ApiEmailService } from './api-email-service';
import { UserService } from './user-service';
import { HttpResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmailService implements OnInit {

  private api = inject(ApiEmailService);
  private router = inject(Router);
  private user = inject(UserService);
  private zone = inject(NgZone);
  private alert = inject(Alert);

  isLoading = signal<boolean>(false);
  invalidEmails = signal<string[]>([]);

  readonly systemFolders = ['inbox', 'starred', 'sent', 'drafts', 'trash'];
  // All folders signal
  folders = signal<string[]>([...this.systemFolders]);

  currentFolder = signal<string>('inbox');

  selectedEmail = signal<Email | null>(null);

  draftToEdit = signal<Email | null>(null);

  // Current Search State
  searchCriteria = signal<SearchCriteria>({});

  // State for Simple String Search
  currentSearchTerm = signal<string>('');

  // Sorting State (Default: Date)
  currentSortBy = signal<string>('Date');

  emailsSignal = signal<Email[]>([]);

  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  totalElements = signal<number>(0);
  readonly pageSize = 9;

  constructor() {
    // Load folders on service initialization
    this.loadFolders();
  }

  ngOnInit() {

  }


  loadEmailsForFolder(folder: string, page: number = 0) {
    this.currentSortBy.set('');

    if (folder === 'search') {
      if (this.currentSearchTerm()) {
        // Simple Search with Pagination
        this.api.searchEmails(this.currentSearchTerm(), page, this.pageSize).subscribe({
          next: (response) => {
            this.emailsSignal.set(response.content);
            this.currentPage.set(response.number);
            this.totalPages.set(response.totalPages);
            this.totalElements.set(response.totalElements);
          },
          error: (err) => console.error('Failed to search emails', err)
        });
      } else {
        // Advanced Filter with Pagination
        this.api.filterEmails(this.searchCriteria(), page, this.pageSize).subscribe({
          next: (response) => {
            this.emailsSignal.set(response.content);
            this.currentPage.set(response.number);
            this.totalPages.set(response.totalPages);
            this.totalElements.set(response.totalElements);
          },
          error: (err) => console.error('Failed to filter emails', err)
        });
      }
    } else {
      // Standard Folder Load
      this.api.getEmails(folder, page, this.pageSize).subscribe({
        next: (response) => {
          this.emailsSignal.set(response.content);
          this.currentPage.set(response.number);
          this.totalPages.set(response.totalPages);
          this.totalElements.set(response.totalElements);
        },
        error: (err) => console.error(`Failed to load ${folder}`, err)
      });
    }
  }

  // process string[] directly
  loadFolders() {
    this.api.getUserFolders().subscribe({
      next: (folderNames) => {
        const uniqueBackendFolders = folderNames
          .filter(f => !this.systemFolders.includes(f));

        this.folders.set([...this.systemFolders, ...uniqueBackendFolders]);

        console.log("LOL" + this.folders);
      },
      error: (err) => console.error('Failed to load user folders', err)
    });
  }

  // Sort Action
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

  performSearch(query: string) {
    if (!query) return;
    this.currentSearchTerm.set(query);
    this.searchCriteria.set({});
    this.router.navigate(['/layout/search']);
    this.loadEmailsForFolder('search', 0); // Load page 0
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
    if (this.systemFolders.includes(folderName)) return;
    this.api.removeFolder(folderName).subscribe(() => {
      this.folders.update(list => list.filter(f => f !== folderName));
      this.loadFolders();
    });
  }


  filterEmails(folder: string) {
    // We assume loadEmailsForFolder(folder) is called by the component when route changes
    return this.emailsSignal();
  }


  setSelectedEmail(email: Email | null) {
    this.selectedEmail.set(email);

    // Logic: If unread, mark as read locally AND on backend
    if (email != null) {
      if (!email.read) {
        // 1. Optimistic Update (UI updates immediately)
        this.emailsSignal.update(all =>
          all.map(e => e.id === email.id ? { ...e, read: true } : e)
        );

        // 2. API Call
        if (email.id != null) {
          this.api.markAsRead(email.id).subscribe({
            error: (err) => console.error('Failed to mark as read', err)
          });
        }
      }
    }
  }

  getAdjacentEmailId(currentId: number, folder: string, direction: 'next' | 'prev'): number | null {
    const emailsInFolder = this.filterEmails(folder);
    const currentIndex = emailsInFolder.findIndex(e => e.id === currentId);

    if (currentIndex === -1) return null;

    const adjacentIndex = direction === 'next' ? currentIndex + 1 : currentIndex - 1;

    if (adjacentIndex >= 0 && adjacentIndex < emailsInFolder.length) {
      if (emailsInFolder[adjacentIndex].id != null)
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
    if (email.id != null) {
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
  }

  moveEmails(emailIds: number[], targetFolder: string) {
    this.api.moveToFolder(targetFolder, emailIds).subscribe(() => {
      // Optimistic UI update: remove from current view
      this.loadEmailsForFolder(this.currentFolder(), this.currentPage());
      this.emailsSignal.update(emails => emails.filter(e => !emailIds.includes(<number>e.id)));

      if (this.selectedEmail()!.id != null && emailIds.includes(<number>this.selectedEmail()!.id) && this.selectedEmail()) {
        this.selectedEmail.set(null);
      }
    });
  }

  editDraft(email: Email) {
    this.draftToEdit.set(email);
    this.router.navigate(['/layout/drafts/compose']);
  }


  saveDraft(data: any, onSuccess?: () => void) {
    if (this.isLoading()) return;
    this.isLoading.set(true);

    const currentDraft = this.draftToEdit();
    const draftEmail: Email = {
      id: currentDraft ? currentDraft.id : 0,
      folder: 'drafts',
      sender: this.user.getName(), // Current user
      senderEmail: this.user.getEmail(),
      receiverEmails: data.email,
      time: new Date().toISOString(),
      subject: data.subject || '(No Subject)',
      body: data.body,
      attachments: data.attachments || [],
      read: true,
      active: false,
      starred: false,
      hasAttachment: false,
      priority: data.priority || 3
    };



    console.log("email sent: ")
    console.log(draftEmail)

    const files: File[] = (data.attachments || [])
      .filter((a: any) => a.file)
      .map((a: any) => a.file);
    // hna ana lma bdoos (x) w elback 3ndo eldraft da be3ml draft gded elmafrood elback
    // DONE NEED TO TESTED

    if (draftEmail.id == 0) {
      this.api.sendEmail(draftEmail, files).subscribe({
        next: (savedEmail) => {
          this.isLoading.set(false);
          this.alert.draftSaved();
          this.draftToEdit.set(null);
          // If we are currently viewing Drafts, update the list
          if (this.router.url.includes('/drafts')) {
            this.loadEmailsForFolder("drafts", 0);
            this.selectedEmail.set(null);
            this.router.navigate(['/layout/drafts']);
          }
          if (onSuccess) onSuccess();
        },
        error: (err) => {
          this.isLoading.set(false);
          console.error('Failed to save draft', err);
          this.alert.error('Failed to save draft');
        }
      });
    } else {
      this.api.updateEmail(draftEmail, files).subscribe({
        next: (savedEmail) => {
          this.isLoading.set(false);
          this.alert.draftSaved();
          this.draftToEdit.set(null);
          // If we are currently viewing Drafts, update the list
          if (this.router.url.includes('/drafts')) {
            this.loadEmailsForFolder("drafts", 0)
            this.selectedEmail.set(null);
            this.router.navigate(['/layout/drafts']);
          }
          if (onSuccess) onSuccess();
        },
        error: (err) => {
          this.isLoading.set(false);
          console.error('Failed to update draft', err);
          this.alert.error('Failed to update draft');
        }
      })
    }
  }


  sendEmail(data: any, onSuccess?: () => void) {
    if (this.isLoading()) return;
    this.isLoading.set(true);

    const currentDraft = this.draftToEdit();

    // Map form data to Email model
    // If editing a draft, include its ID so backend updates instead of creates
    const emailToSend: Email = {
      id: currentDraft ? currentDraft.id : 0,
      folder: 'sent',
      sender: this.user.getName(), // Current user
      senderEmail: this.user.getEmail(),
      receiverEmails: data.email,
      time: new Date().toISOString(),
      subject: data.subject || '(No Subject)',
      body: data.body,
      attachments: data.attachments || [],
      read: true,
      active: false,
      starred: false,
      hasAttachment: data.attachments && data.attachments.length > 0,
      priority: data.priority || 3
    };


    console.log("emailToSend: ")
    console.log(emailToSend)
    // Extract files from attachments that have embedded File objects (new attachments)
    const files: File[] = (data.attachments || [])
      .filter((a: any) => a.file)
      .map((a: any) => a.file);

    // Always use sendEmail - backend saveMail handles both new and existing mails
    this.api.sendEmail(emailToSend, files).subscribe({
      next: (savedEmail) => {
        this.isLoading.set(false);
        this.alert.emailSent();

        this.draftToEdit.set(null);
        // If we are in 'sent' folder, add to list, otherwise just navigate
        if (this.router.url.includes('/sent')) {
          this.loadEmailsForFolder("sent", 0);
          this.selectedEmail.set(null);
          this.router.navigate(['/layout/sent']);
        }
        if (onSuccess) onSuccess();
      },
      error: (err) => {
        this.isLoading.set(false);
        console.log('Send email error:', err);

        const response = err?.error;
        const invalidEmailList = response?.invalidEmails;

        if (invalidEmailList && invalidEmailList.length > 0) {
          this.invalidEmails.set(invalidEmailList);
          const emailsDisplay = invalidEmailList.join(', ');
          this.alert.error(`Could not send email. The following recipient(s) do not exist: ${emailsDisplay}`);
        } else {
          const errorMessage = response?.message || 'Failed to send email. Please try again.';
          this.alert.error(errorMessage);
        }
      }
    });
  }

  deleteEmail(id: number) {
    this.api.removeEmail(id).subscribe({
      next: () => {
        // Remove from local list if present
        this.emailsSignal.update(emails => emails.filter(e => e.id !== id));
        this.selectedEmail.set(null);
        this.router.navigate(['/layout/drafts']);
      },
      error: (err) => console.error('Failed to delete email', err)
    });
  }

  executeSearch(criteria: SearchCriteria) {
    this.searchCriteria.set(criteria);
    this.currentSearchTerm.set('');
    this.router.navigate(['/layout/search']);
    this.loadEmailsForFolder('search', 0); // Load page 0
  }

  // Convert Observable to Promise so we can use async/await
  downloadAttachment(id: number, fileName: string) {
    try {
      this.api.downloadAttachment(id).subscribe(blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        URL.revokeObjectURL(url);
      })
      // const response = await firstValueFrom(this.api.getAttachmentContent(id));
      // return response.data;
    } catch (err) {
      console.error('Failed to get attachment data', err);
    }
  }

}
