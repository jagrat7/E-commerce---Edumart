import { Component, OnInit } from '@angular/core';
import { Product } from '../Product';
import { ProductService } from '../product.service';
import { ActivatedRoute } from '@angular/router';
import { cartProduct } from '../cartProduct';
import { SessionService } from '../session.service';

@Component({
  selector: 'app-product-tiles',
  templateUrl: './product-tiles.component.html',
  styleUrls: ['./product-tiles.component.css']
})
export class ProductTilesComponent implements OnInit {
  products;
  more;
  cartProducts: cartProduct[];
  constructor(private productService: ProductService,
    private route: ActivatedRoute, private sessionService : SessionService) { }

  ngOnInit(): void {

    this.getProducts("all");
  }

  getProducts(type): void {
    this.productService.getProducts(type)
        .subscribe({
          next: (products) => {
            this.products = products;
            this.more = !this.products.length;
            if(this.sessionService.getIsLogIn() && !this.sessionService.getIsAdmin()){
              this.getProductsInCart();
          }
          }
        });
  }

  getProductsInCart(): void{
    this.productService.getCartProducts().subscribe({
      next: (cartProducts) => {
        this.cartProducts = cartProducts;
        if(this.sessionService.getIsLogIn() && !this.sessionService.getIsAdmin()){
          this.modifyProducts();
      }
      }
    });
  }

  modifyProducts(): void{
     this.products.forEach((arrayValue) => {
      let isProductFound : Boolean = false;
      this.cartProducts.forEach((cartProduct) => {
        if( (cartProduct.productDetails.id === arrayValue.id) && (cartProduct.type === arrayValue.type) ){
          arrayValue.quantityInCart = cartProduct.quantity;
          isProductFound = true;
        }
      });
      if(!isProductFound){
        arrayValue.quantityInCart = -1;
      }
     });
    
  }

}
