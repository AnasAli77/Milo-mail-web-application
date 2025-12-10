import { TestBed } from '@angular/core/testing';

import { FileToBase64Service } from './file-to-base64-service';

describe('FileToBase64Service', () => {
  let service: FileToBase64Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileToBase64Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
