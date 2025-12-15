import {Component, EventEmitter, inject, Output, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Header } from '../header/header';
import { Sidebar } from '../sidebar/sidebar';
import {RouterOutlet} from '@angular/router';
import {EmailService} from '../../Services/email-service';

@Component({
  selector: 'app-main-layout',
  imports: [CommonModule, Header, Sidebar, RouterOutlet],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {
  @Output() onSignOut = new EventEmitter<void>();
  emailService = inject(EmailService);

  // This signal tracks if the sidebar is open or closed
  isSidebarCollapsed = signal(false);
  selectedEmail = this.emailService.selectedEmail;

  // This function is called when the menu button in Header is clicked
  toggleSidebar() {
    this.isSidebarCollapsed.update(value => !value);
  }

}
