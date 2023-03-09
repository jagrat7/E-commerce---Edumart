import { Component, OnInit } from '@angular/core';
import { ProductService } from '../product.service';
import { Location } from '@angular/common';
import { EventEmitter } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { inventoryHistory } from '../inventoryHistory';

@Component({
  selector: 'app-inventory-history',
  templateUrl: './inventory-history.component.html',
  styleUrls: ['./inventory-history.component.css'],
})
export class InventoryHistoryComponent implements OnInit {
  inventoryHistoryProducts: inventoryHistory[];

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private storeProductService: ProductService
  ) {}

  ngOnInit(): void {
    this.getInventoryHistory();
  }

  approveRequestData(): void {
    this.inventoryHistoryProducts.forEach((inventory) => {
      if (inventory.actions != null) {
        inventory.actions.forEach((action) => {
          if (action.field_changes != null) {
            action.field_changes.forEach((actionData, index) => {
              if (actionData.field == 'status') {
                if (this.isFieldStatus(actionData)) {
                  delete action.field_changes[index];
                  action.type = 'Approved Author Request';
                }
              }
            });
          }
        });
      }
    });
  }

  isFieldStatus(actionData): boolean {
    if (
      String(actionData.old_value) == 'TO_BE_ADDED' &&
      String(actionData.new_value) == 'IN_LIVE'
    ) {
      return true;
    }
    return false;
  }

  getInventoryHistory(): void {
    this.storeProductService.getInventoryHistory().subscribe({
      next: (historyProducts) => {
        this.inventoryHistoryProducts = historyProducts;
        this.approveRequestData();
      },
    });
  }
}
