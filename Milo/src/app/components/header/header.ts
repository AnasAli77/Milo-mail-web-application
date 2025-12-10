import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SearchCriteria } from '../../models/searchCriteria';
import { FormsModule } from '@angular/forms';
import { EmailService } from '../../Services/email-service';
import { UserService } from '../../Services/user-service';

@Component({
  selector: 'app-header',
  imports: [CommonModule, FormsModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

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
  

  signOut() {
    sessionStorage.clear();
    this.route.navigateByUrl("");
  }
  toggleProfile() {
    this.isProfileOpen.update(v => !v);
  }
  toggleSearchFilter() {
    this.isSearchFilterOpen.update(v => !v);
    this.isProfileOpen.set(false); // Close other dropdown
  }

  // General Search (Enter in main input)
  onQuickSearch() {
    this.performSearch();
  }

  // Advanced Search (Button in dropdown)
  onAdvancedSearch() {
    this.performSearch();
    this.isSearchFilterOpen.set(false); // Close dropdown
  }

  performSearch() {
    const criteria: SearchCriteria = {
      query: this.searchQuery,
      from: this.searchFrom,
      to: this.searchTo,
      subject: this.searchSubject,
      hasAttachment: this.searchHasAttachment,
      priority: this.searchPriority ? parseInt(this.searchPriority) : undefined
    };
    
    this.emailService.executeSearch(criteria);
  }

  clearFilter() {
    this.searchQuery = '';
    this.searchFrom = '';
    this.searchTo = '';
    this.searchSubject = '';
    this.searchHasAttachment = false;
    this.searchPriority = ''; // Clear priority
  }
}