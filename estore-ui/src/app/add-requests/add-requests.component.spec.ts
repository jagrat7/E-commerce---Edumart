import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRequestsComponent } from './add-requests.component';

describe('AddRequestsComponent', () => {
  let component: AddRequestsComponent;
  let fixture: ComponentFixture<AddRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddRequestsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
