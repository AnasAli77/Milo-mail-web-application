import { Component, EventEmitter, Output, signal } from '@angular/core';
import { Email } from '../../models/email';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-email-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './email-list.html',
  styleUrl: './email-list.css',
})
export class EmailList {
  // @Output() onEmailSelected = new EventEmitter<Email>();
  // sortBy = signal('Date');
  
  // emails: Email[] = [
  //   { 
  //     id: 1, 
  //     sender: 'Sarah Johnson', 
  //     senderEmail: 'sarah.j@milo.com',
  //     time: '7:24 PM', 
  //     subject: 'Q4 Project Updates', 
  //     preview: 'Hi team, I wanted to share some updates on our Q4 progress...', 
  //     body: 'Hi team,\n\nI wanted to share some updates on our Q4 progress and what to expect in the coming weeks. We have hit all our milestones for October and are on track for a successful year-end close.\n\nPlease review the attached slide deck for the detailed breakdown.\n\nBest,\nSarah',
  //     read: false, 
  //     active: true, 
  //     starred: false, 
  //     hasAttachment: false,
  //     avatarColor: 'bg-emerald-100 text-emerald-600',
  //     avatarInitials: 'SJ'
  //   },
  //   { 
  //     id: 2, 
  //     sender: 'Marketing Team', 
  //     senderEmail: 'newsletter@milo.com',
  //     time: '5:58 PM', 
  //     subject: 'Newsletter: January Edition', 
  //     preview: 'Hello! Check out our latest newsletter featuring: - New product...', 
  //     body: 'Hello!\n\nCheck out our latest newsletter featuring:\n- New product launch dates\n- Employee of the month\n- Upcoming holiday schedule\n\nClick here to read more.',
  //     read: false, 
  //     active: false, 
  //     starred: true, 
  //     hasAttachment: false,
  //     avatarColor: 'bg-blue-100 text-blue-600',
  //     avatarInitials: 'MT'
  //   },
  //   { 
  //     id: 3, 
  //     sender: 'Michael Chen', 
  //     senderEmail: 'm.chen@design.com',
  //     time: '2:58 PM', 
  //     subject: 'Meeting Request: Design Review', 
  //     preview: 'Hi, Could we schedule a design review meeting for next week?', 
  //     body: 'Hi,\n\nCould we schedule a design review meeting for next week? I have some mockups ready for the new landing page.\n\nLet me know your availability.\n\nThanks,\nMichael',
  //     read: true, 
  //     active: false, 
  //     starred: false, 
  //     hasAttachment: true,
  //     avatarColor: 'bg-purple-100 text-purple-600',
  //     avatarInitials: 'MC'
  //   }
  // ];

  // updateSort(value: string) {
  //   this.sortBy.set(value);
  // }

  // selectEmail(email: Email) {
  //   this.emails.forEach(e => e.active = false);
  //   email.active = true;
  //   email.read = true;
  //   this.onEmailSelected.emit(email);
  // }

  // toggleStar(email: Email) {
  //   email.starred = !email.starred;
  // }
    @Output() onEmailSelected = new EventEmitter<Email>();
  sortBy = signal('Date');
  
  emails: Email[] = [
    { 
      id: 1, 
      sender: 'Sarah Johnson', 
      senderEmail: 'sarah.j@milo.com',
      time: '7:24 PM', 
      subject: 'Q4 Project Updates', 
      preview: 'Hi team, I wanted to share some updates on our Q4 progress...', 
      body: 'Hi team,\n\nI wanted to share some updates on our Q4 progress and what to expect in the coming weeks. We have hit all our milestones for October and are on track for a successful year-end close.\n\nPlease review the attached slide deck for the detailed breakdown.\n\nBest,\nSarah',
      read: false, 
      active: false, 
      starred: false, 
      hasAttachment: false,
      avatarColor: 'bg-emerald-100',
      avatarInitials: 'SJ'
    },
    { 
      id: 2, 
      sender: 'Marketing Team', 
      senderEmail: 'marketing@milo.com',
      time: '5:58 PM', 
      subject: 'Newsletter: January Edition', 
      preview: 'Hello! Check out our latest newsletter featuring: - New product...', 
      body: 'Hello!\n\nHere is the January edition of our newsletter. We have a lot of exciting updates to share with you regarding our product roadmap.\n\nEnjoy!',
      read: false, 
      active: false, 
      starred: true, 
      hasAttachment: false,
      avatarColor: 'bg-blue-100',
      avatarInitials: 'MT'
    },
    { 
      id: 3, 
      sender: 'Michael Chen', 
      senderEmail: 'm.chen@design.com',
      time: '2:58 PM', 
      subject: 'Meeting Request: Design Review', 
      preview: 'Hi, Could we schedule a design review meeting for next week?', 
      body: 'Hi,\n\nCould we schedule a design review meeting for next week? I have the mockups ready for the new landing page and would love your feedback.\n\nThanks,\nMichael',
      read: true, 
      active: false, 
      starred: false, 
      hasAttachment: true,
      avatarColor: 'bg-purple-100',
      avatarInitials: 'MC'
    }
  ];

  updateSort(value: string) {
    this.sortBy.set(value);
  }

  email() :Email
  {
    return this.emails[0];
  }
  selectEmail(email: Email) {
    // Reset active state for all
    this.emails.forEach(e => e.active = false);
    // Set active for clicked
    email.active = true;
    email.read = true; // Mark as read
    this.onEmailSelected.emit(email);
  }
}