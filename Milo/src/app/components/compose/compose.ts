import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { EmailService } from '../../Services/email-service';
import { Attachment } from '../../models/attachment';
import { FileToBase64Service } from '../../Services/file-to-base64-service';
import { UserService } from '../../Services/user-service';
import { ApiEmailService } from '../../Services/api-email-service';
import { Alert } from '../../Services/alert';

interface ReceiverInput {
  email: string;
}

@Component({
  selector: 'app-compose',
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './compose.html',
  styleUrl: './compose.css',
})
export class Compose {
  receivers: ReceiverInput[] = [{ email: '' }];
  subject: string = '';
  message: string = '';
  priority: number = 3;

  // Array to store attached files
  attachments: Attachment[] = [];

  private emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$/;

  constructor(
    private location: Location,
    private emailService: EmailService,
    private fileService: FileToBase64Service,
    private apiemailservice: ApiEmailService,
  ) { }

  // Load draft data if we are editing one
  ngOnInit() {
    const draft = this.emailService.draftToEdit();
    if (draft) {
      // Populate receivers
      if (draft.receiverEmails && draft.receiverEmails.length > 0) {
        this.receivers = draft.receiverEmails.map(email => ({ email }));
      }
      this.subject = draft.subject;
      this.message = draft.body;
      this.attachments = draft.attachments || [];
      this.priority = draft.priority || 3;
    }
  }

  // Helper to get text label for UI
  getPriorityLabel(): string {
    switch (Number(this.priority)) {
      case 1: return 'Very Low';
      case 2: return 'Low';
      case 3: return 'Normal';
      case 4: return 'High';
      case 5: return 'Extreme';
      default: return 'Normal';
    }
  }

  async onFilesSelected(event: any) {
    const files: FileList = event.target.files;
    if (files) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];

        try {
          // Convert to Attachment object
          const attachment: Attachment = await this.fileService.fileToAttachment(file);

          // Push to your attachments array (ensure your array accepts Attachment objects now)
          console.log(attachment);
          this.attachments.push(attachment);

        } catch (error) {
          console.error("Error reading file:", error);
        }
      }
    }
    // Reset input so the same file can be selected again if needed
    event.target.value = '';
  }

  // Remove a specific attachment
  removeAttachment(index: number) {
    this.attachments.splice(index, 1);
  }
  // Format file size (e.g., 1.2 MB)
  // formatFileSize(bytes: number): string {
  //   if (bytes === 0) return '0 B';
  //   const k = 1024;
  //   const sizes = ['B', 'KB', 'MB', 'GB'];
  //   const i = Math.floor(Math.log(bytes) / Math.log(k));
  //   return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  // }

  // ------------------------

  addReceiver() {
    this.receivers = [...this.receivers, { email: '' }];
  }

  removeReceiver(index: number) {
    if (this.receivers.length > 1) {
      this.receivers = this.receivers.filter((_, i) => i !== index);
    }
  }

  trackByIndex(index: number, obj: any): any {
    return index;
  }

  get isFormValid(): boolean {
    if (!this.message || this.message.trim() === '') return false;
    if (this.receivers.length === 0) return false;

    return this.receivers.every(r =>
      r.email && this.emailPattern.test(r.email)
    );
  }

  // GO TO DRAFTS
  close() {
    // Only save if there is content to save
    const hasContent = this.subject || this.message || this.receivers.some(r => r.email) || this.attachments.length > 0;;

    if (hasContent) {
      const emailList = this.receivers.map(r => r.email).filter(e => !!e);
      const emailData = {
        to: emailList.join(', '),
        email: emailList,
        subject: this.subject,
        body: this.message,
        attachments: this.attachments,
        priority: this.priority
      };

      this.emailService.saveDraft(emailData);
    } else {
      // If empty, just clear any draft ref
      this.emailService.draftToEdit.set(null);
    }

    this.location.back();
  }

  // Discard Button - Does NOT Save
  discard() {
    const draft = this.emailService.draftToEdit();

    // If the draft exists and has a real ID (not 0), delete it from backend
    if (draft && draft.id && draft.id !== 0) {
      this.emailService.deleteEmail(draft.id);
    }

    // Clear the reference and go back
    this.emailService.draftToEdit.set(null);
    this.location.back();
  }

  send() {
    if (this.isFormValid) {
      const emailList = this.receivers.map(r => r.email);
      const emailData = {
        to: emailList.join(', '),
        email: emailList,
        subject: this.subject,
        body: this.message,
        attachments: this.attachments,
        priority: this.priority
      };

      this.emailService.sendEmail(emailData);
      this.discard(); // NOT CLOSE BECAUSE CLOSE REDIRECT EMAIL TO DRAFTS
    }
  }
}