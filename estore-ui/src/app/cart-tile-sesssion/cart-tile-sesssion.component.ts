import { Component, OnInit, Input, Output } from '@angular/core';
import { ProductService } from '../product.service';
import { Location } from '@angular/common';
import { cartProduct } from '../cartProduct';
import { EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CartComponent } from '../cart/cart.component';
import { Status } from '../cartProduct';
import { cartSession } from '../cartSession';
@Component({
  selector: 'app-cart-tile-sesssion',
  templateUrl: './cart-tile-sesssion.component.html',
  styleUrls: ['./cart-tile-sesssion.component.css'],
})
export class CartTileSesssionComponent implements OnInit {
  @Input() cartSession: cartSession;
  @Output() delete = new EventEmitter();
  Status = Status;
  constructor(
    private route: ActivatedRoute,
    private storeService: ProductService,
    private location: Location,
    private cart: CartComponent
  ) {}
  ngOnInit(): void {
    //does nothing  on initialization
  }
  onDelete() {
    this.delete.emit(this.cartSession);
  }
}
