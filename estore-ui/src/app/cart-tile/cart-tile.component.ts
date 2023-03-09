import { Component, OnInit, Input, Output } from '@angular/core';
import { ProductService } from '../product.service';
import { Location } from '@angular/common';
import { cartProduct } from '../cartProduct';
import { EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CartComponent } from '../cart/cart.component';
import { Status } from '../cartProduct';

@Component({
  selector: 'app-cart-tile',
  templateUrl: './cart-tile.component.html',
  styleUrls: ['./cart-tile.component.css'],
})
export class CartTileComponent implements OnInit {
  @Input() cartProduct: cartProduct;
  @Output() delete = new EventEmitter();
  Status = Status;

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private storeProductService: ProductService,
    private cart: CartComponent
  ) {}

  ngOnInit(): void {
    //does nothing  on initialization
  }

  onDelete() {
    this.delete.emit(this.cartProduct);
  }

  modifyQuantity(cartProduct: cartProduct, isIncrease: Boolean): void {
    if (!isIncrease && cartProduct.quantity == 1) {
      this.onDelete();
    } else {
      this.storeProductService
        .modifyCartProduct(
          { id: cartProduct.productDetails.id, type: cartProduct.type },
          isIncrease,
          1
        )
        .subscribe({
          next: (currentProductUpdated) => {
            this.cartProduct = currentProductUpdated;
            this.cart.cartProducts = this.modifyProducts(
              this.cart.cartProducts,
              currentProductUpdated
            );
            this.cart.total();
          },
        });
    }
  }

  modifyProducts(
    cartProducts: cartProduct[],
    updatedCartProduct: cartProduct
  ): cartProduct[] {
    let isProductFound: Boolean = false;
    cartProducts.forEach((cartProduct) => {
      if (
        cartProduct.productDetails.id ===
          updatedCartProduct.productDetails.id &&
        cartProduct.type === updatedCartProduct.type
      ) {
        cartProduct.quantity = updatedCartProduct.quantity;
      }
    });
    return cartProducts;
  }
}
