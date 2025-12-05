import {inject, Injectable, signal} from '@angular/core';
import {Email} from '../models/email'
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  folders = signal<string[]>(['inbox', 'starred', 'sent', 'drafts', 'trash']);
  selectedEmail = signal<Email | null>(null);
  private router = inject(Router);

  draftToEdit = signal<Email | null>(null);

  emailsSignal = signal<Email[]>([
    {
      id: 1,
      sender: 'Sarah Johnson',
      senderEmail: 'sarah.j@milo.com',
      receiverEmail: ['tofyfathy12@gmail.com'],
      time: '7:24 PM',
      subject: 'Q4 Project Updates',
      body: 'Hi team,\n\nI wanted to share some updates on our Q4 progress and what to expect in the coming weeks. We have hit all our milestones for October and are on track for a successful year-end close.\n\nPlease review the attached slide deck for the detailed breakdown.\n\nBest,\nSarah',
      read: false, active: false, starred: false, hasAttachment: true, folder: 'inbox'
    },
    {
      id: 2,
      sender: 'Marketing Team',
      senderEmail: 'newsletter@milo.com',
      receiverEmail: ['tofyfathy12@gmail.com'],
      time: '5:58 PM',
      subject: 'Newsletter: January Edition',
      body: 'Hello!\n\nCheck out our latest newsletter featuring:\n- New product launch dates\n- Employee of the month\n- Upcoming holiday schedule\n\nClick here to read more.',
      read: false, active: false, starred: true, hasAttachment: false,folder: 'inbox'
    },
    {
      id: 3,
      sender: 'Michael Chen',
      senderEmail: 'm.chen@design.com',
      receiverEmail: ['tofyfathy12@gmail.com'],
      time: '2:58 PM',
      subject: 'Meeting Request: Design Review',
      body: 'Hi,\n\nCould we schedule a design review meeting for next week? I have some mockups ready for the new landing page.\n\nLet me know your availability.\n\nThanks,\nMichael',
      read: true, active: false, starred: false, hasAttachment: true, folder: 'inbox'
    },
    {
      id: 4,
      sender: 'Emma Wilson',
      senderEmail: 'emma.w@studio.com',
      receiverEmail: ['tofyfathy12@gmail.com'],
      time: 'Yesterday',
      subject: 'Final Assets for Campaign',
      body: 'Hey,\n\nAttached are the final exported assets for the social media campaign. Let me know if you need any other formats.\n\nCheers,\nEmma',
      read: true, active: false, starred: true, hasAttachment: true, folder: 'inbox'
    },
    {
      id: 7,
      sender: 'Tofy Fathy',
      senderEmail: 'tofyfathy12@gmail.com',
      receiverEmail: ['client@company.com'],
      time: '10:00 AM',
      subject: 'Project Proposal v2',
      body: 'Hi Client,\n\nPlease find attached the revised proposal based on our discussion yesterday. I have updated the budget section.\n\nBest,\nTofy',
      read: true, active: false, starred: false, hasAttachment: true, folder: 'sent'
    }
  ]);


  addFolder(folderName: string) {
    const normalize = folderName.toLowerCase();
    if (!this.folders().includes(normalize)) {
      this.folders.update(list => [...list, normalize]);
    }
  }

  // WHEN I PReSS TO ANY FOLDER THIS DISPLAY ONLY EMAILS OF THIS FOLDER
  filterEmails(folder: string) {
    const all = this.emailsSignal();
    if (folder === 'starred') return all.filter(e => e.starred);
    return all.filter(e => e.folder === folder);
  }


  setSelectedEmail(email: Email) {
    this.selectedEmail.set(email);
    this.emailsSignal.update(all =>
      all.map(e => e.id === email.id ? {...e, read: true, active: true} : {...e, active: false})
    );
  }

  getAdjacentEmailId(currentId: number, folder: string, direction: 'next' | 'prev'): number | null {
    const emailsInFolder = this.filterEmails(folder);
    const currentIndex = emailsInFolder.findIndex(e => e.id === currentId);

    // If current email isn't in the list, we can't determine next/prev
    if (currentIndex === -1) return null;

    // Next = Down the list (Index + 1)
    // Prev = Up the list (Index - 1)
    const adjacentIndex = direction === 'next' ? currentIndex + 1 : currentIndex - 1;

    if (adjacentIndex >= 0 && adjacentIndex < emailsInFolder.length) {
      return emailsInFolder[adjacentIndex].id;
    }

    return null;
  }

  setStarredEmail(email: Email) {
    this.emailsSignal.update(emails =>
      emails.map(e => e.id === email.id ? {...e, starred: !e.starred} : e)
    );
  }

  moveEmails(emailIds: number[], targetFolder: string) {
    this.emailsSignal.update(emails =>
      emails.map(e => emailIds.includes(e.id) ? {...e, folder: targetFolder, active: false} : e)
    );

    if (this.selectedEmail() && emailIds.includes(this.selectedEmail()!.id)) {
      this.selectedEmail.set(null);
    }
  }

  editDraft(email: Email) {
    this.draftToEdit.set(email);

    // Remove the email from the list immediately so it is no longer in "Drafts" view while editing
    this.emailsSignal.update(emails => emails.filter(e => e.id !== email.id));

    // Automatically navigate to compose page
    this.router.navigate(['/layout/drafts/compose']);
  }


  saveDraft(data: { to: string; email: string[]; subject: string; body: string }) {
    const currentDraft = this.draftToEdit();
    const timestamp = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    const draftToSave: Email = {
      id: currentDraft ? currentDraft.id : Date.now(),
      sender: 'Tofy Fathy',
      senderEmail: 'tofyfathy12@gmail.com',
      receiverEmail: data.email,
      time: timestamp,
      subject: data.subject || '(Draft)',
      body: data.body,
      read: true,
      active: false,
      starred: false,
      hasAttachment: false,
      folder: 'drafts'
    };
    this.emailsSignal.update(emails => [draftToSave, ...emails.filter(e => e.id !== draftToSave.id)]);

    this.draftToEdit.set(null);
  }

  sendEmail(data: { to: string; email :string[] ; subject: string; body: string }) {

    this.draftToEdit.set(null);

    const newEmail: Email = {
      id: Date.now(),
      sender: 'Tofy Fathy',
      senderEmail: 'tofyfathy12@gmail.com',
      receiverEmail: data.email,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      subject: data.subject || '(No Subject)',
      body: data.body,
      read: true,
      active: false,
      starred: false,
      hasAttachment: false,
      folder: 'sent'
    };

    this.emailsSignal.update(emails => [newEmail, ...emails]);
  }
}
