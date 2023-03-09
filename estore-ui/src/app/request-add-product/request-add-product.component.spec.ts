import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestAddProductComponent } from './request-add-product.component';

describe('RequestAddProductComponent', () => {
  let component: RequestAddProductComponent;
  let fixture: ComponentFixture<RequestAddProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestAddProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RequestAddProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
