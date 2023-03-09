import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookedSessionsComponent } from './booked-sessions.component';

describe('BookedSessionsComponent', () => {
  let component: BookedSessionsComponent;
  let fixture: ComponentFixture<BookedSessionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BookedSessionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookedSessionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
