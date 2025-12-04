import {Component, inject, Input} from '@angular/core';
import { CommonModule } from '@angular/common';
import {NavigationEnd, Router, RouterLink, RouterLinkActive} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {EmailService} from '../../Services/email-service';
import {filter} from 'rxjs';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterLinkActive, RouterLink, FormsModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  @Input() collapsed = false;
  emailService = inject(EmailService);
  router = inject(Router);

  newFolderName = '';
  isAddingFolder = false;
  defaultFolders = ['inbox', 'starred', 'sent', 'drafts', 'trash'];


  currentFolder = 'inbox';

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      // Extract folder from URL (e.g. /inbox/compose -> inbox)
      const segments = event.urlAfterRedirects.split('/');
      if (segments.length > 1) {
        // Use the first segment as the folder (ignoring login/signup)
        const potentialFolder = segments[1];
        if (this.defaultFolders.includes(potentialFolder) || this.customFolders.includes(potentialFolder)) {
          this.currentFolder = potentialFolder;
        }
      }
    });
  }

  get customFolders() {
    return this.emailService.folders().filter(f => !this.defaultFolders.includes(f));
  }

  createFolder() {
    if (this.newFolderName.trim()) {
      this.emailService.addFolder(this.newFolderName);
      this.newFolderName = '';
      this.isAddingFolder = false;
    }
  }
}
