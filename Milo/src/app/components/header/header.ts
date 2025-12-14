import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SearchCriteria } from '../../models/searchCriteria';
import { FormsModule } from '@angular/forms';
import { EmailService } from '../../Services/email-service';
import { UserService } from '../../Services/user-service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [CommonModule, FormsModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit{

  @Input() collapsed = false;
  @Output() onToggleSidebar = new EventEmitter<void>();

  route = inject(Router);
  emailService = inject(EmailService);
  user = inject(UserService);
  isProfileOpen = signal(false);
  isSearchFilterOpen = signal(false); // Track search filter state

  // Search Data
  searchQuery = '';
  searchFrom = '';
  searchTo = '';
  searchSubject = '';
  searchHasAttachment = false;
  searchPriority = ''; // NEW: Bind to string for <select>
  // NEW: Separate date fields
  searchDay = '';
  searchMonth = '';
  searchYear = '';

  // NEW: Subject to handle debounce
  private searchDebouncer = new Subject<string>();

  ngOnInit() {
    // Setup the subscription for live search
    this.searchDebouncer.pipe(
      debounceTime(300),        // Wait 300ms after user stops typing
      distinctUntilChanged()    // Only search if the text is different from last time
    ).subscribe((term) => {
      if (term.trim()) {
        this.emailService.performSearch(term);
      }
    });
  }

  // NEW: Triggered on every keystroke via (ngModelChange)
  onSearchInput(value: string) {
    this.searchDebouncer.next(value);
  }

  signOut() {
    console.log('signOut  lollollool');
    this.emailService.selectedEmail.set(null);

    sessionStorage.clear();

    console.log("TEEEEEEEEZK YA ANAAAAAAAAAAAS");
    this.route.navigateByUrl("");
  }

  toggleProfile() {
    this.isProfileOpen.update(v => !v);
  }

  toggleSearchFilter() {
    this.isSearchFilterOpen.update(v => !v);
    this.isProfileOpen.set(false); // Close other dropdown
  }

  // General Search: Uses the new Simple String Search endpoint
  onQuickSearch() {
    if (this.searchQuery.trim()) {
      this.emailService.performSearch(this.searchQuery);
    }
  }

  // Advanced Search: Uses the Filter endpoint with criteria
  onAdvancedSearch() {
    const criteria: SearchCriteria = {
      query: this.searchQuery,
      from: this.searchFrom,
      to: this.searchTo,
      subject: this.searchSubject,
      hasAttachment: this.searchHasAttachment,
      priority: this.searchPriority ? parseInt(this.searchPriority) : undefined,
      day: this.searchDay,
      month: this.searchMonth,
      year: this.searchYear
    };

    this.emailService.executeSearch(criteria);
    this.isSearchFilterOpen.set(false);
  }

  clearFilter() {
    this.searchQuery = '';
    this.searchFrom = '';
    this.searchTo = '';
    this.searchSubject = '';
    this.searchHasAttachment = false;
    this.searchPriority = ''; // Clear priority
    this.searchDay = '';
    this.searchMonth = '';
    this.searchYear = '';
  }
}
