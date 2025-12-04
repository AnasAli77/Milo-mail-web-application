import { Component, EventEmitter, Output, signal } from '@angular/core';
import { Email } from '../../models/email';
import { CommonModule } from '@angular/common';
import { Header } from '../header/header';
import { Sidebar } from '../sidebar/sidebar';
import { EmailList } from '../email-list/email-list';
import { EmailViewComponent } from '../email-viewer/email-viewer';

@Component({
  selector: 'app-main-layout',
  imports: [CommonModule, Header, Sidebar, EmailList, EmailViewComponent],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {
  // @Output() onSignOut = new EventEmitter<void>();
  // isSidebarCollapsed = signal(false);
  // selectedEmail = signal<Email | null>(null);

  // toggleSidebar() {
  //   this.isSidebarCollapsed.update(v => !v);
  // }
  @Output() onSignOut = new EventEmitter<void>();
  
  // This signal tracks if the sidebar is open or closed
  isSidebarCollapsed = signal(false);
  selectedEmail = signal<Email | null>(null);

  // This function is called when the menu button in Header is clicked
  toggleSidebar() {
    this.isSidebarCollapsed.update(value => !value);
  }

  handleEmailSelection(email: Email) {
    this.selectedEmail.set(email);
  }
}
