import { Injectable, inject } from '@angular/core';
import { signal, computed } from '@angular/core';
import { FilterRule } from '../models/FilterRule';
import { ApiFilterService } from './api-filter-service';
import { EmailService } from './email-service';

@Injectable({
  providedIn: 'root',
})
export class FilterService {
  private api = inject(ApiFilterService);
  private emailService = inject(EmailService);

  public filters = signal<FilterRule[]>([]);
  public folders = computed(() => this.emailService.folders());

  constructor() {
    this.loadFilters();
  }

  // Expose signals to the component
  getFilters() {
    return this.filters.asReadonly();
  }

  getFolders() {
    return this.folders;
  }

  // Load from Backend
  private loadFilters() {
    this.api.getRules().subscribe({
      next: (data) => {
        this.filters.set(data);
      },
      error: (err) => console.error('Error loading filters:', err)
    });
  }

  // Add to Backend -> Update Signal
  addFilter(rule: FilterRule) {
    this.api.addRule(rule).subscribe({
      next: (savedRule) => {
        this.filters.update(list => [...list, savedRule]);
      },
      error: (err) => console.error('Error adding filter:', err)
    });
  }

  // Delete from Backend -> Update Signal
  deleteFilter(id: number) {
    this.api.deleteRule(id).subscribe({
      next: () => {
        this.filters.update(list => list.filter(f => f.id !== id));
      },
      error: (err) => console.error('Error deleting filter:', err)
    });
  }
}