import { Component, computed, EventEmitter, inject, OnInit, Output, signal } from '@angular/core';
import { Email } from '../../models/email';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { EmailService } from '../../Services/email-service';
import { EmailViewComponent } from '../email-viewer/email-viewer';

@Component({
  selector: 'app-email-list',
  imports: [CommonModule, FormsModule, RouterOutlet, EmailViewComponent],
  templateUrl: './email-list.html',
  styleUrl: './email-list.css',
})


export class EmailList implements OnInit {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  public emailService = inject(EmailService);

  currentFolder = signal<string>('inbox');
  sortBy = signal('Date');
  checkedEmailIds = signal<Set<number>>(new Set());

  // Tracks if a child route (Compose or Email Viewer) is active
  isRouterActive = false;

  filteredEmails = this.emailService.emailsSignal;

  moveableFolders = computed(() => {
    const excluded = ['starred', 'sent', 'drafts'];
    return this.emailService.folders().filter(f => !excluded.includes(f));
  });

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const folderId = params.get('folderId') || 'inbox';
      this.currentFolder.set(folderId);
      this.checkedEmailIds.set(new Set());

      this.emailService.loadEmailsForFolder(folderId, 0);
    });
  }

  // NEW: Refresh Action
  loadData() {
    this.emailService.loadEmailsForFolder(this.currentFolder());
  }

  refresh() {
    this.loadData();
  }

  // Helper for Priority Color
  getPriorityColor(priority?: number): string {
    const p = priority || 3;
    switch (p) {
      case 5: return '#ef4444'; // Extreme (Red)
      case 4: return '#f97316'; // High (Orange)
      case 3: return '#3b82f6'; // Normal (Blue)
      case 2: return '#84cc16'; // Low (Green)
      case 1: return '#22c55e'; // Very Low (Dark Green)
      default: return '#3b82f6';
    }
  }

  selectEmail(email: Email) {
    this.emailService.setSelectedEmail(email);
    this.router.navigate(['email', email.id], { relativeTo: this.route });
  }

  navigateEmail(direction: 'next' | 'prev') {
    const selected = this.emailService.selectedEmail();
    if (!selected) return;

    const list = this.filteredEmails();
    const currentIndex = list.findIndex(e => e.id === selected.id);

    if (currentIndex === -1) return;

    const targetIndex = direction === 'next' ? currentIndex + 1 : currentIndex - 1;

    if (targetIndex >= 0 && targetIndex < list.length) {
      this.selectEmail(list[targetIndex]);
    }
  }

  // FOR DISABLED
  get hasNext() {
    const selected = this.emailService.selectedEmail();
    if (!selected) return false;
    const list = this.filteredEmails();
    const idx = list.findIndex(e => e.id === selected.id);
    return idx >= 0 && idx < list.length - 1;
  }

  get hasPrev() {
    const selected = this.emailService.selectedEmail();
    if (!selected) return false;
    const list = this.filteredEmails();
    const idx = list.findIndex(e => e.id === selected.id);
    return idx > 0;
  }


  staremail(email: Email) {
    this.emailService.setStarredEmail(email);
  }

  updateSort(value: string) {
    this.sortBy.set(value);
  }

  onRouterActivate() {
    this.isRouterActive = true;
  }

  onRouterDeactivate() {
    this.isRouterActive = false;
  }

  toggleCheck(emailId: number, event: Event) {
    event.stopPropagation();
    const current = new Set(this.checkedEmailIds());
    if (current.has(emailId)) current.delete(emailId);
    else current.add(emailId);
    this.checkedEmailIds.set(current);
  }

  isChecked(emailId: number) {
    return this.checkedEmailIds().has(emailId);
  }

  onBulkMove(event: Event) {
    const select = event.target as HTMLSelectElement;
    if (select.value && this.checkedEmailIds().size > 0) {
      this.emailService.moveEmails(Array.from(this.checkedEmailIds()), select.value);
      this.checkedEmailIds.set(new Set());
      select.value = "";
    }
  }

  get selectionCount() {
    return this.checkedEmailIds().size;
  }

/* --------------PAGINATION OF LISTS ---------*/
  nextPage() {
    const current = this.emailService.currentPage();
    const folder = this.currentFolder();
    this.emailService.changePage(folder, current + 1);
  }

  prevPage() {
    const current = this.emailService.currentPage();
    const folder = this.currentFolder();
    this.emailService.changePage(folder, current - 1);
  }


  get currentPage() { return this.emailService.currentPage(); }
  get totalPages() { return this.emailService.totalPages(); }
  get isFirstPage() { return this.currentPage === 0; }
  get isLastPage() { return this.currentPage === this.totalPages - 1 || this.totalPages === 0; }


}
