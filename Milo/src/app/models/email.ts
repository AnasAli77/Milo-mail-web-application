import { Attachment } from "./attachment";

export interface Email {
  id: number;
  folder: string;
  sender: string;
  senderEmail: string;
  receiverEmails: string[]; // Array that will be converted to Queue in backend
  time: string;
  subject: string;
  body: string;
  attachments?: Attachment[];
  read: boolean;
  active: boolean;
  starred: boolean;
  hasAttachment?: boolean;
  priority?: number;
}
