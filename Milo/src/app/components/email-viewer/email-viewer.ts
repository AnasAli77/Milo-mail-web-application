import { Component, inject, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Email } from '../../models/email';
import { ActivatedRoute, Router } from '@angular/router';
import { EmailService } from '../../Services/email-service';
import { Attachment } from '../../models/attachment';

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
  async downloadFile(attachment: Attachment) {
    
    try {
      //get the attachment data from the backend
      attachment.base64Content = await this.emailService.getAttachmentData(attachment?.id?? 0)
      // 1. Convert Base64 string back to binary data
      const byteCharacters = atob(attachment.base64Content);
      const byteNumbers = new Array(byteCharacters.length);

      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }

      const byteArray = new Uint8Array(byteNumbers);

      // 2. Create a Blob from the binary data
      const blob = new Blob([byteArray], { type: attachment.fileType });

      // 3. Create a temporary URL for the Blob
      const url = window.URL.createObjectURL(blob);

      // 4. Trigger Download
      const a = document.createElement('a');
      a.href = url;
      a.download = attachment.fileName;
      document.body.appendChild(a);
      a.click();

      // 5. Cleanup
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);

    } catch (e) {
      console.error('Download failed', e);
    }
  }

  getFileSizeFromBase64(base64: string): number {
    // Remove data URL prefix if present (e.g., "data:image/png;base64,")
    const base64String = base64.includes(',') ? base64.split(',')[1] : base64;

    // Count padding characters
    let padding = 0;
    if (base64String.endsWith('==')) padding = 2;
    else if (base64String.endsWith('=')) padding = 1;

    // Calculate original size in bytes
    return (base64String.length * 3) / 4 - padding;
  }

  // Format for display
  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }
}
