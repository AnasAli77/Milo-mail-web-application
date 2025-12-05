import {Component, inject, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Email } from '../../models/email';
import {ActivatedRoute, Router} from '@angular/router';
import {EmailService} from '../../Services/email-service';

@Component({
  selector: 'app-email-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './email-viewer.html',
  styleUrl: './email-viewer.css',
})
export class EmailViewComponent implements OnInit {
  @Input() email: Email | null = null;

  // 2. Services needed ONLY if loading via URL
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private emailService = inject(EmailService);

  currentFolder = 'inbox';

  ngOnInit() {
    // Check if we are inside the Router (no Input provided)
    if (!this.email) {

      // Get folder context (optional, for "move to" logic)
      this.route.parent?.params.subscribe(params => {
        this.currentFolder = params['folderId'] || 'inbox';
      });

      // Get ID from URL
      this.route.params.subscribe(params => {
        const id = +params['id'];
        if (id) {
          this.loadEmailFromService(id);
        }
      });
    }
  }

  // Helper to fetch data if we are using Routing
  private loadEmailFromService(id: number) {
    // Note: We search in 'all' folders or specifically the current one
    const found = this.emailService.filterEmails(this.currentFolder).find(e => e.id === id);
    if (found) {
      this.email = found;
      // Mark as active/read in service
      this.emailService.setSelectedEmail(found);
    }
  }

    // --- Download Logic (Restored) ---
  downloadFile(file: File) {
    // Create a temporary URL for the file
    const url = window.URL.createObjectURL(file);
    
    // Create a temporary anchor tag to trigger the download
    const a = document.createElement('a');
    a.href = url;
    a.download = file.name; // This forces the browser to download
    document.body.appendChild(a);
    a.click();
    
    // Cleanup
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
