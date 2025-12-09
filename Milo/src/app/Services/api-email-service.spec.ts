import { TestBed } from '@angular/core/testing';

import { ApiEmailService } from './api-email-service';

describe('ApiEmailService', () => {
  let service: ApiEmailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiEmailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
