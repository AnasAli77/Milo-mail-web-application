import { Injectable } from '@angular/core';
import { Attachment } from '../models/attachment';

@Injectable({
  providedIn: 'root',
})
export class FileToBase64Service {

  fileToAttachment(file: File): Attachment {
    // return new Promise((resolve, reject) => {
    //   const reader = new FileReader();

    //   reader.onload = () => {
    //     // reader.result is a Data URL: "data:image/png;base64,iVBORw0KGgo..."
    //     const result = reader.result as string;

    //     // We usually want just the Base64 part, so we split at the comma
    //     // const base64Part = result.split(',')[1];

    //     resolve({
    //       fileName: file.name,
    //       fileType: file.type,
    //       // base64Content: base64Part
    //     });
    //   };

    //   reader.onerror = (error) => {
    //     reject(error);
    //   };

    //   // Read the file as a Data URL (which contains the Base64 string)
    //   reader.readAsDataURL(file);
    // });
    return {
      fileName: file.name,
      fileType: file.type,
      size: file.size
    };
  }
}