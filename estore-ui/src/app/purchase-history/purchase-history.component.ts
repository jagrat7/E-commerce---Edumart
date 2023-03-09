import { Component, OnInit } from '@angular/core';
import { ProductService } from '../product.service';
import { Location } from '@angular/common';
import { EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { purchaseHistory } from '../purchaseHistory';

@Component({
  selector: 'app-purchase-history',
  templateUrl: './purchase-history.component.html',
  styleUrls: ['./purchase-history.component.css'],
})
export class PurchaseHistoryComponent implements OnInit {
  purchaseHistoryProducts: purchaseHistory[];

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private storeProductService: ProductService
  ) {}

  ngOnInit(): void {
    this.getPurchaseHistory();
  }

  getPurchaseHistory(): void {
    this.storeProductService.getPurchaseHistory().subscribe({
      next: (historyProducts) => {
        this.purchaseHistoryProducts = historyProducts;
      },
    });
  }
}
