import { Component, OnInit, Inject } from '@angular/core';
import { ProductService } from '../product.service';
import { ActivatedRoute } from '@angular/router';
import { lookupListToken } from '../providers';

@Component({
  selector: 'app-add-requests',
  templateUrl: './add-requests.component.html',
  styleUrls: ['./add-requests.component.css']
})
export class AddRequestsComponent implements OnInit {
  products;


  constructor(private storeProductService : ProductService,  
              private route: ActivatedRoute, 
              @Inject(lookupListToken) public lookupLists) {}
  ngOnInit(): void {
    this.getProducts("all");
  }
  getProducts(type): void{
    this.storeProductService.getProductsFilter(type).subscribe(products => this.products = products);
  }
  onDelete(element) {
    this.storeProductService.deleteProduct(element.type,element.id)
      .subscribe(() => {
        this.getProducts("all");
      });
  }
  onApproveProduct(element){
    this.storeProductService.updateProductRequest(element).subscribe(() => {
      this.getProducts("all");
    });
  }
  displayedColumns: string[] = ['Type', 'Name', 'Quantity', 'Update', 'Delete'];


}
