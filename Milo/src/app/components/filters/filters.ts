import { Component, inject, OnInit, signal, ChangeDetectionStrategy, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { FilterRule } from '../../models/FilterRule';
import { FilterService } from '../../Services/filter-service';

@Component({
  selector: 'app-filters',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './filters.html',
  styleUrls: ['./filters.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FiltersComponent implements OnInit {

  private filterService = inject(FilterService);
  private fb = inject(FormBuilder);

  // Data Sources
  // filters = computed(() => this.filterService.filters());
  filters = signal<FilterRule[]>([]);
  folders = computed(() => this.filterService.folders());

  // UI State
  isSearchFilterOpen = signal(false);

  // NEW: Tracks which filter is clicked in the list
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
      actionMoveToFolderId: '',
      actionMarkAsRead: false,
      actionStar: false
    });
  }

  ngOnInit(): void {
    this.loadFilters();
  }

  loadFilters() {
    this.filters.set([
      { id: 1, sender: 'boss@company.com', subject: 'Urgent', priority: '5', moveToFolderId: 'Work', star: true, markAsRead: false },
      { id: 2, sender: 'newsletter@shop.com', body: 'discount 50%', hasAttachment: true, moveToFolderId: 'Promotions', markAsRead: true },
      { id: 3, sender: 'bank@alert.com', subject: 'Statement', moveToFolderId: 'Finance', star: true },
      { id: 4, recipient: 'team@dev.com', subject: 'Pull Request', moveToFolderId: 'Work' }
    ]);
  }

  // 1. ENABLE FORM WHEN CREATING
  toggleCreateForm() {
    this.selectedFilter.set(null);
    this.isSearchFilterOpen.update(val => !val);

    if (this.isSearchFilterOpen()) {
      this.clearFilter();
      this.filterForm.enable(); // <--- Allow editing for new filter
    } else {
      this.clearFilter();
    }
  }
  // NEW: Handles clicking a row in the list to view/edit
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
      actionMoveToFolderId: filter.moveToFolderId || '',
      actionMarkAsRead: filter.markAsRead || false,
      actionStar: filter.star || false
    });
    this.filterForm.disable();
  }

  clearFilter() {
    this.filterForm.reset();
  }

  onAdvancedSearch() {
    const formValue = this.filterForm.value;

    // 1. Construct Date string
    let dateStr: string | undefined;
    if (formValue.searchYear && formValue.searchMonth && formValue.searchDay) {
      dateStr = `${formValue.searchYear}-${formValue.searchMonth}-${formValue.searchDay}`;
    }

    // 2. Build Object
    const newFilter: FilterRule = {
      sender: formValue.searchFrom,
      recipient: formValue.searchTo,
      subject: formValue.searchSubject,
      body: formValue.searchQuery,
      priority: formValue.searchPriority,
      date: dateStr,
      hasAttachment: formValue.searchHasAttachment,
      moveToFolderId: formValue.actionMoveToFolderId,
      markAsRead: formValue.actionMarkAsRead,
      star: formValue.actionStar
    };

    // 3. Call Service
    this.filterService.addFilter(newFilter);
    this.loadFilters();
    this.toggleCreateForm();
    this.clearFilter();
  }

  deleteFilter(id: number) {
    if (confirm('Are you sure you want to delete this filter?')) {
      this.filterService.deleteFilter(id);
      this.loadFilters();
    }
  }
}