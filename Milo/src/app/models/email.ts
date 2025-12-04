export interface Email {
  id: number;
  sender: string;
  senderEmail: string;
  time: string;
  subject: string;
  preview: string;
  body: string; // Added body for reading pane
  read: boolean;
  active: boolean;
  starred: boolean;
  hasAttachment: boolean;
  label?: string;
  avatarColor: string;
  avatarInitials: string;
}