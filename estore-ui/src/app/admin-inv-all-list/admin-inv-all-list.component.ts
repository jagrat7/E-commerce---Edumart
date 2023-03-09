import { Component, OnInit, Inject, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from  '../product.service'
import { lookupListToken } from '../providers';

@Component({
  selector: 'app-admin-inv-all-list',
  templateUrl: './admin-inv-all-list.component.html',
  styleUrls: ['./admin-inv-all-list.component.css']
})
export class AdminInvAllListComponent implements OnInit {
  products;


  constructor(private storeProductService : ProductService,  
              private route: ActivatedRoute, 
              @Inject(lookupListToken) public lookupLists) {}
  ngOnInit(): void {
    this.getProducts("all");
  }
  getProducts(type): void{
    this.storeProductService.getProducts(type).subscribe(products => this.products = products);
  }
  onDelete(element) {
    this.storeProductService.deleteProduct(element.type,element.id)
      .subscribe(() => {
        this.getProducts("all");
      });
  }
  displayedColumns: string[] = ['Type', 'Name', 'Quantity', 'Update', 'Delete'];
}
