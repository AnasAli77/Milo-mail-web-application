export interface Email {
  id: number;
  folder: string;
  sender: string;
  senderEmail: string;
  receiverEmail: string[];
  time: string;
  subject: string;
  body: string;
  attachments?: File[];
  read: boolean;
  active: boolean;
  starred: boolean;
  hasAttachment?: boolean;
  priority?: number;
}
