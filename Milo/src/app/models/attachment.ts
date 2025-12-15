export interface Attachment {
    id?: number,
    fileName: string,
    fileType: string,
    base64Content: string;   // Base64 is just a string in TS
    size?: number;           // File size in bytes (from backend)
}