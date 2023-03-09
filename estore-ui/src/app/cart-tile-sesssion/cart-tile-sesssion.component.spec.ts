import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CartTileSesssionComponent } from './cart-tile-sesssion.component';

describe('CartTileSesssionComponent', () => {
  let component: CartTileSesssionComponent;
  let fixture: ComponentFixture<CartTileSesssionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CartTileSesssionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CartTileSesssionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
