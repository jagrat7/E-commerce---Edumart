import { Component, Input, OnInit, Inject } from '@angular/core';
import { ProductService } from '../product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { cartProduct } from '../cartProduct';
import { SessionService } from '../session.service';
@Component({
  selector: 'app-product-single-view',
  templateUrl: './product-single-view.component.html',
  styleUrls: ['./product-single-view.component.css'],
})
export class ProductSingleViewComponent implements OnInit {
  public isAuthor: Boolean;
  public isAdmin: Boolean;
  product_single;
  images: string[] = [];
  cartProduct_single: cartProduct[];
  constructor(
    private storeProductService: ProductService,
    private route: ActivatedRoute,
    private location: Location,
    private router: Router,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.getProduct();
    this.isAdmin = this.sessionService.getIsAdmin();
    this.isAuthor = this.sessionService.getIsAuthor();
  }

  goBack(): void {
    this.location.back();
  }
  responsiveOptions: any[] = [
    // {
    //   breakpoint: '1024px',
    //   numVisible: 4
    // },
    // {
    //   breakpoint: '768px',
    //   numVisible:4
    // },
    // {
    //   breakpoint: '560px',
    //   numVisible: 4
    // }
  ];

  getProduct(): void {
    const type = String(this.route.snapshot.paramMap.get('type'));
    console.log(type);
    const id = Number(this.route.snapshot.paramMap.get('id'));
    console.log(id);
    this.storeProductService.getProduct(type, id).subscribe({
      next: (products) => {
        this.product_single = products;
        if (this.product_single.image == null) {
          this.images.push('../../assets/images/misc/edumart-logo.png');
        } else if (this.isImageURL(this.product_single)) {
          for (let i = 0; i < this.product_single.image.imageSrc.length; i++) {
            this.images.push(this.product_single.image.imageSrc[i]);
          }
        } else {
          this.updateImage();
        }

        if (
          this.sessionService.getIsLogIn() &&
          !this.sessionService.getIsAdmin()
        ) {
          this.getProductsInCart();
        }
      },
    });
  }

  updateImage(): void {
    if (this.isTypeVideo(this.product_single)) {
      for (let i = 0; i < this.product_single.image.imageSrc.length; i++) {
        this.images.push(
          '../../assets/images/Products/Videos/' +
            this.product_single.image.imageSrc[i]
        );
      }
    } else if (this.isTypeBook(this.product_single)) {
      for (let i = 0; i < this.product_single.image.imageSrc.length; i++) {
        this.images.push(
          '../../assets/images/Products/Books/' +
            this.product_single.image.imageSrc[i]
        );
      }
    }
  }

  isTypeVideo(product_single): boolean {
    if (product_single.type === 'video') {
      return true;
    }
    return false;
  }

  isTypeBook(product_single): boolean {
    if (product_single.type === 'book') {
      return true;
    }
    return false;
  }

  isImageURL(product_single): boolean {
    if (product_single.image.type == 'URL') {
      return true;
    }
    return false;
  }

  onAdd(): void {
    if (!this.sessionService.getIsLogIn()) {
      this.router.navigate(['login']);
    } else {
      this.storeProductService.addCartProduct(this.product_single).subscribe({
        next: (cartProduct) => {
          this.cartProduct_single = cartProduct;
          this.modifyProducts();
          if (
            cartProduct.hasOwnProperty('status') &&
            cartProduct.status == 'failure'
          ) {
            console.log('PRODUCT ALREADY ADDED');
          }
        },
      });
      console.log(this.product_single);
    }
  }

  modifyQuantity(isIncrease: Boolean): void {
    this.storeProductService
      .modifyCartProduct(this.product_single, isIncrease, 1)
      .subscribe({
        next: (currentProductUpdated) => {
          this.product_single.quantityInCart = currentProductUpdated.quantity;
        },
      });
  }

  getProductsInCart(): void {
    this.storeProductService.getCartProducts().subscribe({
      next: (cartProducts) => {
        this.cartProduct_single = cartProducts;
        if (
          this.sessionService.getIsLogIn() &&
          !this.sessionService.getIsAdmin()
        ) {
          this.modifyProducts();
        }
      },
    });
  }

  modifyProducts(): void {
    let isProductFound: Boolean = false;
    this.cartProduct_single.forEach((cartProduct) => {
      if (
        cartProduct.productDetails.id === this.product_single.id &&
        cartProduct.type === this.product_single.type
      ) {
        this.product_single.quantityInCart = cartProduct.quantity;
        isProductFound = true;
      }
    });
    if (!isProductFound) {
      this.product_single.quantityInCart = -1;
    }
  }
}
