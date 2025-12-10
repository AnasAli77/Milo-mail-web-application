export interface Attachment {
    fileName: string,
    fileType: string,
    base64Content: string;   // Base64 is just a string in TS
}