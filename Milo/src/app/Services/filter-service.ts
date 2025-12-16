import {Injectable, signal} from '@angular/core';
import {FilterRule} from '../models/FilterRule';
import {ApiFilterService} from './api-filter-service';

@Injectable({
  providedIn: 'root',
})
export class FilterService {
  private filters = signal<FilterRule[]>([]);

  // Keep folders local or fetch from FolderService if you have one.
  // For now, keeping the list here so the dropdown works.
  private folders = signal<string[]>(['Work', 'Personal', 'Finance', 'Travel', 'University']);

  constructor(private api: ApiFilterService) {
    this.loadFilters();
  }

  // Expose signals to the component
  getFilters() {
    return this.filters;
  }

  getFolders() {
    return this.folders;
  }

  // Load from Backend
  loadFilters() {
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
        // Add the rule returned from server (which includes the real DB ID) to the signal
        this.filters.update(list => [...list, savedRule]);
      },
      error: (err) => console.error('Error adding filter:', err)
    });
  }

  // Delete from Backend -> Update Signal
  deleteFilter(id: number) {
    this.api.deleteRule(id).subscribe({
      next: () => {
        // Remove from local list upon success
        this.filters.update(list => list.filter(f => f.id !== id));
      },
      error: (err) => console.error('Error deleting filter:', err)
    });
  }
}
