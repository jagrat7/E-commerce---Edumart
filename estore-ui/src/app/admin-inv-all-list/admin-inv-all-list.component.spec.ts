import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminInvAllListComponent } from './admin-inv-all-list.component';

describe('AdminInvAllListComponent', () => {
  let component: AdminInvAllListComponent;
  let fixture: ComponentFixture<AdminInvAllListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminInvAllListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminInvAllListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
