import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Safty } from './safty';

describe('Safty', () => {
  let component: Safty;
  let fixture: ComponentFixture<Safty>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Safty]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Safty);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
