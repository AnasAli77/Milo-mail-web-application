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
  public loadFilters() {
    this.api.getRules().subscribe({
      next: (data: any[]) => {
        // Map Backend DTOs -> Frontend Models
        const mappedFilters = data.map(dto => this.mapFromDto(dto));
        this.filters.set(mappedFilters);
      },
      error: (err) => console.error('Error loading filters:', err)
    });
  }

  // Add to Backend -> Update Signal
  addFilter(rule: FilterRule) {
    const dto = this.mapToDto(rule);
    this.api.addRule(dto).subscribe({
      next: () => {
        // Reload all to get IDs and consistent state
        this.loadFilters();
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

  // --- MAPPING LOGIC ---

  private mapToDto(rule: FilterRule): any {
    const criteriaTypesValues: { [key: string]: string } = {};

    // Map Criteria
    if (rule.sender) criteriaTypesValues['sender'] = rule.sender;
    if (rule.recipient) criteriaTypesValues['receiver'] = rule.recipient; // Backend uses 'receiver' usually
    if (rule.subject) criteriaTypesValues['subject'] = rule.subject;
    if (rule.body) criteriaTypesValues['body'] = rule.body;
    if (rule.priority) criteriaTypesValues['priority'] = rule.priority;

    // Split Date (YYYY-MM-DD) -> Year, Month, Day
    if (rule.date) {
      const parts = rule.date.split('-');
      if (parts.length === 3) {
        criteriaTypesValues['year'] = parts[0];
        criteriaTypesValues['month'] = parts[1];
        criteriaTypesValues['day'] = parts[2];
      }
    }

    if (rule.hasAttachment) criteriaTypesValues['hasAttachment'] = 'true';

    // Map Action (Priority: Move > Star > Read)
    // Backend ActionFactory expects lowercase: "move", "star", "markasread"
    let actionType = 'markasread'; // Default safe fallback
    let actionTarget = '';

    if (rule.moveToFolderId) {
      actionType = 'move';
      actionTarget = rule.moveToFolderId;
    } else if (rule.star) {
      actionType = 'star';
    } else if (rule.markAsRead) {
      actionType = 'markasread';
    }

    return {
      criteriaTypesValues,
      actionType,
      actionTarget
    };
  }

  private mapFromDto(dto: any): FilterRule {
    const criteria = dto.criteriaTypesValues || {};
    const actionType = dto.actionType;
    const actionTarget = dto.actionTarget;

    // Reconstruct Date
    let dateStr = '';
    if (criteria['year'] && criteria['month'] && criteria['day']) {
      dateStr = `${criteria['year']}-${criteria['month']}-${criteria['day']}`;
    }

    const rule: FilterRule = {
      id: dto.id,
      sender: criteria['sender'] || '',
      recipient: criteria['receiver'] || '',
      subject: criteria['subject'] || '',
      body: criteria['body'] || '',
      priority: criteria['priority'] || '',
      date: dateStr,
      hasAttachment: criteria['hasAttachment'] === 'true',

      // Map Actions back
      moveToFolderId: actionType === 'move' ? actionTarget : '',
      star: actionType === 'star',
      markAsRead: actionType === 'markasread'
    };
    return rule;
  }
}
