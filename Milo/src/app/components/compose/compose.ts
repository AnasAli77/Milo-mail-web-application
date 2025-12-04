import {ChangeDetectorRef, Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import {EmailService} from '../../Services/email-service';

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

  private emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$/;

  constructor(
    private location: Location,
    private emailService: EmailService
  ) {}

  // Load draft data if we are editing one
  ngOnInit() {
    const draft = this.emailService.draftToEdit();
    if (draft) {
      // Populate receivers
      if (draft.receiverEmail && draft.receiverEmail.length > 0) {
        this.receivers = draft.receiverEmail.map(email => ({ email }));
      }
      this.subject = draft.subject;
      this.message = draft.body;
    }
  }

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

  close() {
    // Only save if there is content to save
    const hasContent = this.subject || this.message || this.receivers.some(r => r.email);

    if (hasContent) {
      const emailList = this.receivers.map(r => r.email).filter(e => !!e);
      const emailData = {
        to: emailList.join(', '),
        email: emailList,
        subject: this.subject,
        body: this.message
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
    // Clear the draft reference without saving
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
        body: this.message
      };

      this.emailService.sendEmail(emailData);
      this.discard(); // Note: close() might try to save draft again if not careful, but sendEmail clears the draft ref, so it's safe.
    }
  }
}
