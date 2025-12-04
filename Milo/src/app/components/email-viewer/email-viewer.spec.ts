import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailViewer } from './email-viewer';

describe('EmailViewer', () => {
  let component: EmailViewer;
  let fixture: ComponentFixture<EmailViewer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailViewer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailViewer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
