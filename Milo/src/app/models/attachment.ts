export interface Attachment {
    id?: number;           // ID from database (present for existing attachments)
    fileName: string;
    fileType: string;
    size?: number;         // File size in bytes (from backend or File object)
    file?: File;           // The actual File object (present for new attachments to upload)
}