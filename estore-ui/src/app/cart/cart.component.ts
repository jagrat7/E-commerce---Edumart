import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../product';
import { ProductService } from '../product.service';
import { Location } from '@angular/common';
import { cartProduct, Status } from '../cartProduct';
import { cartSession } from '../cartSession';
@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  @Output() delete = new EventEmitter();
  @Input() cartProduct: cartProduct;
  public isCartProductsEmpty : Boolean;
  public isCartSessionsEmpty : Boolean;
  cartProducts: cartProduct[];
  cartSessions: cartSession[];
  totalPrice: number;
  noOfProductsDeleted: number;

  constructor(
    private route: ActivatedRoute,
    private storeService: ProductService,
    private location: Location,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getCartProducts();
    this.getCartSessions();
  }

  checkIfAnyProductDeleted(cartProducts: cartProduct[]): void{
    this.noOfProductsDeleted = 0;
    cartProducts.forEach((cartProduct) => {
      if(cartProduct.productDetails.status === Status.TO_BE_DELETED){
        this.noOfProductsDeleted++;
      }
    });
  }

  getCartProducts(): void {
    this.storeService.getCartProducts().subscribe({
      next: (cartProducts) => {
        this.cartProducts = cartProducts;
        this.checkIfAnyProductDeleted(cartProducts);
        this.total();
        if(this.cartProducts.length > 0){
          this.isCartProductsEmpty = false;
        }else{
          this.isCartProductsEmpty = true;
        }
      },
    });
  }

  getCartSessions(): void {
    this.storeService.getCartSessions().subscribe({
      next: (cartSessions) => {
        this.cartSessions = cartSessions;
        if(this.cartSessions.length > 0){
          this.isCartSessionsEmpty = false;
        }else{
          this.isCartSessionsEmpty = true;
        }
      },
    });
  }

  total(): void {
    this.totalPrice = 0;
    if (this.cartProducts != undefined) {
      this.cartProducts.forEach((product) => {
        this.totalPrice += ( product.quantity * product.productDetails.price );
      });
    }
  }

  onDeleteCartProduct(cartProduct): void {
    this.storeService.deleteCartProduct(cartProduct).subscribe(() => {
      this.getCartProducts();
    });
  }
  onDeleteCartSession(cartSessions): void {
    this.storeService.deleteCartSession(cartSessions).subscribe(() => {
      this.getCartSessions();
    });
  }

  checkout(): void{
    this.storeService.checkout().subscribe(() => {
      this.router.navigate(['cart'])
      .then(() => {
              window.location.reload();
           });
    });
  }
}
