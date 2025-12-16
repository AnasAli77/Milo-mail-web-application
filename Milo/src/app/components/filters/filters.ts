import {Component, inject, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {FilterRule} from '../../models/FilterRule';
import {ApiFilterService} from '../../Services/api-filter-service';
import {FilterService} from '../../Services/filter-service';
import {HttpClient} from '@angular/common/http';
import {EmailService} from '../../Services/email-service';


@Component({
  selector: 'app-filters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: 'filters.html',
  styleUrls: ['filters.css']
})
export class FiltersComponent{
  criteriaType: 'SUBJECT' | 'SENDER' = 'SUBJECT';
  criteriaValue: string = '';
  actionType: 'MOVE_TO_FOLDER' | 'STAR' | 'MARK_READ' = 'STAR';
  actionTarget: string = '';

  filterService = inject(FilterService)
  emailservice = inject(EmailService)

  // Data from Service
  filters = this.filterService.getFilters();
  userFolders = this.emailservice.folders();

  constructor() {
  }

  onAdd() {
    if (!this.criteriaValue) return;

    const newRule: FilterRule = {
      criteriaType: this.criteriaType,
      criteriaValue: this.criteriaValue,
      actionType: this.actionType,
      // Only include actionTarget if we are moving to a folder
      actionTarget: this.actionType === 'MOVE_TO_FOLDER' ? this.actionTarget : undefined
    };

    this.filterService.addFilter(newRule);

    // Reset Form (keep dropdowns, clear text)
    this.criteriaValue = '';
  }

  onDelete(id: number | undefined) {
    if (id) this.filterService.deleteFilter(id);
  }
}
