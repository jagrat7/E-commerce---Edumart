import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryHistoryComponent } from './inventory-history.component';

describe('InventoryHistoryComponent', () => {
  let component: InventoryHistoryComponent;
  let fixture: ComponentFixture<InventoryHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InventoryHistoryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InventoryHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
