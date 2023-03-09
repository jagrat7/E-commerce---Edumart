import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CartTileComponent } from './cart-tile.component';

describe('CartTileComponent', () => {
  let component: CartTileComponent;
  let fixture: ComponentFixture<CartTileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CartTileComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CartTileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
