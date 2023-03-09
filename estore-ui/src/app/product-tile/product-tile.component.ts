import { Component, OnInit, Input} from '@angular/core';
import { ProductService } from '../product.service';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../session.service';

@Component({
  selector: 'app-product-tile',
  templateUrl: './product-tile.component.html',
  styleUrls: ['./product-tile.component.css']
})
export class ProductTileComponent  {

  isAdmin : Boolean;
  public isAuthor: Boolean;

  constructor(private storeProductService : ProductService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    protected sessionService: SessionService) { }
    
  @Input() product;

  ngOnInit(): void {
    this.isAdmin = this.sessionService.getIsAdmin();
    this.isAuthor = this.sessionService.getIsAuthor();

  }

  goBack(): void{
    this.location.back();
  }

  onAdd(currentProduct : any): void{
    if(!this.sessionService.getIsLogIn()){
      this.router.navigate(['login'])
    }else{
        this.storeProductService.addCartProduct(this.product).subscribe({
          next: (cartProducts) => {
              let isProductFound : Boolean = false;
              cartProducts.forEach((cartProduct) => {
                if( (cartProduct.productDetails.id === currentProduct.id) && (cartProduct.type === currentProduct.type) ){
                  currentProduct.quantityInCart = cartProduct.quantity;
                  isProductFound = true;
                }
              });
              if(!isProductFound){
                currentProduct.quantityInCart = -1;
              }
          }
        });
        console.log(this.product);                    
    }
  }

  modifyQuantity(currentProduct : any, isIncrease : boolean) : void{
    this.storeProductService.modifyCartProduct(currentProduct, isIncrease, 1).subscribe({
      next: (currentProductUpdated) => {
        currentProduct.quantityInCart = currentProductUpdated.quantity;
      }
    });

  }

}
