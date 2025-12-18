import { Component, inject, OnInit, signal, ChangeDetectionStrategy, computed } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { FilterRule } from '../../models/FilterRule';
import { FilterService } from '../../Services/filter-service';

@Component({
  selector: 'app-filters',
  imports: [CommonModule, ReactiveFormsModule, TitleCasePipe],
  templateUrl: './filters.html',
  styleUrls: ['./filters.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FiltersComponent implements OnInit {

  private filterService = inject(FilterService);
  private fb = inject(FormBuilder);

  // Data Sources
  filters = computed(() => this.filterService.getFilters()());

  // Filter out system folders for "Move To" action
  filteredFolders = computed(() => {
    const all = this.filterService.getFolders()();
    const excluded = ['sent', 'drafts', 'starred', 'spam', 'important'];
    return all.filter(f => !excluded.includes(f.toLowerCase()));
  });

  // UI State
  isSearchFilterOpen = signal(false);

  // Tracks which filter is clicked in the list
  selectedFilter = signal<FilterRule | null>(null);

  // Reactive Form
  filterForm: FormGroup;

  constructor() {
    this.filterForm = this.fb.group({
      searchFrom: '',
      searchTo: '',
      searchSubject: '',
      searchQuery: '',
      searchPriority: '',
      searchDay: '',
      searchMonth: '',
      searchYear: '',
      searchHasAttachment: false,

      // Converted to Single Action Selection
      actionType: 'markasread', // default
      actionTarget: ''
    });
  }

  ngOnInit(): void {
    // Initial load handled by service constructor, but we can trigger refresh
    this.filterService.loadFilters();
  }

  // 1. ENABLE FORM WHEN CREATING
  toggleCreateForm() {
    this.selectedFilter.set(null);
    this.isSearchFilterOpen.update(val => !val);

    if (this.isSearchFilterOpen()) {
      this.clearFilter();
      this.filterForm.enable();
    } else {
      this.clearFilter();
    }
  }

  // Handles clicking a row in the list to view/edit
  selectFilter(filter: FilterRule) {
    // 1. Switch UI State
    this.isSearchFilterOpen.set(false); // Turn off "Create Mode"
    this.selectedFilter.set(filter);    // Turn on "View/Edit Mode"

    // 2. Parse the date string (YYYY-MM-DD) into parts
    let y = '', m = '', d = '';
    if (filter.date) {
      const parts = filter.date.split('-');
      if (parts.length === 3) {
        y = parts[0];
        m = parts[1];
        d = parts[2];
      }
    }

    // Determine Action Type for Form
    let actType = 'markasread';
    let actTarget = '';

    if (filter.moveToFolderId) {
      actType = 'move';
      actTarget = filter.moveToFolderId;
    } else if (filter.star) {
      actType = 'star';
    } else if (filter.markAsRead) {
      actType = 'markasread';
    }

    // 3. Fill the form with the selected filter's data
    this.filterForm.patchValue({
      searchFrom: filter.sender || '',
      searchTo: filter.recipient || '',
      searchSubject: filter.subject || '',
      searchQuery: filter.body || '',
      searchPriority: filter.priority || '',
      searchDay: d,
      searchMonth: m,
      searchYear: y,
      searchHasAttachment: filter.hasAttachment || false,
      actionType: actType,
      actionTarget: actTarget
    });
    this.filterForm.disable();
  }

  clearFilter() {
    this.filterForm.reset({
      actionType: 'markasread',
      searchHasAttachment: false
    });
  }

  onAdvancedSearch() {
    const formValue = this.filterForm.value;

    // 1. Construct Date string
    let dateStr: string | undefined;
    if (formValue.searchYear && formValue.searchMonth && formValue.searchDay) {
      dateStr = `${formValue.searchYear}-${formValue.searchMonth}-${formValue.searchDay}`;
    }

    // 2. Map Form Action -> Object Flags
    const actType = formValue.actionType; // 'move', 'star', 'markasread'
    const actTarget = formValue.actionTarget;

    // 3. Build Object
    const newFilter: FilterRule = {
      sender: formValue.searchFrom,
      recipient: formValue.searchTo,
      subject: formValue.searchSubject,
      body: formValue.searchQuery,
      priority: formValue.searchPriority,
      date: dateStr,
      hasAttachment: formValue.searchHasAttachment,

      // Set appropriate flag based on single selection
      moveToFolderId: actType === 'move' ? actTarget : '',
      star: actType === 'star',
      markAsRead: actType === 'markasread'
    };

    // 4. Call Service
    this.filterService.addFilter(newFilter);
    this.toggleCreateForm();
    this.clearFilter();
  }



  deleteFilter(id: number) {
    if (confirm('Are you sure you want to delete this filter?')) {
      this.filterService.deleteFilter(id).subscribe({
        next: () => {
          this.selectedFilter.set(null); // Clear selection after successful delete
        },
        error: (err: any) => {
          console.error('Failed to delete filter:', err);
          alert('Failed to delete filter. Please try again.');
        }
      });
    }
  }
}
