import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './home/home.component';
import { ProductSingleViewComponent } from './product-single-view/product-single-view.component';
import { ProductTileComponent } from './product-tile/product-tile.component';
import { ProductTilesComponent } from './product-tiles/product-tiles.component';
import { AdminInvAllListComponent } from './admin-inv-all-list/admin-inv-all-list.component';
import { LoginComponent } from './login/login.component';
import { AdminEditProductComponent } from './admin-edit-product/admin-edit-product.component';
import { CartComponent } from './cart/cart.component';
import { RequestAddProductComponent } from './request-add-product/request-add-product.component';
import { AdminAddProductMatComponent } from './admin-add-product-mat/admin-add-product-mat.component';
import { AuthorProfileComponent } from './author-profile/author-profile.component';
import { BookedSessionsComponent } from './booked-sessions/booked-sessions.component';
import { AddRequestsComponent } from './add-requests/add-requests.component';
import { PurchaseHistoryComponent } from './purchase-history/purchase-history.component';
import { InventoryHistoryComponent } from './inventory-history/inventory-history.component';
import { RegisterComponent } from './register/register.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'cart', component: CartComponent },
  { path: 'product', component: ProductTileComponent },
  { path: 'products/:medium', component: ProductTilesComponent },
  { path: 'single_product/:type/:id', component: ProductSingleViewComponent },
  { path: 'admin_edit/:type/:id', component: AdminEditProductComponent},
  // 'add' is the newest add product page.
  { path: 'add', component:AdminAddProductMatComponent},
  { path: 'admin_item_list', component: AdminInvAllListComponent },
  { path: 'reqAddProd', component: RequestAddProductComponent},
  { path: 'login', component: LoginComponent },
  { path: 'author', component: AuthorProfileComponent },
  { path: 'addRequests', component: AddRequestsComponent },
  { path: 'history/purchase', component: PurchaseHistoryComponent },
  { path: 'history/inventory', component: InventoryHistoryComponent },
  { path: 'booked_sessions', component: BookedSessionsComponent},
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'register', component: RegisterComponent }
];

export const routing = RouterModule.forRoot(routes);

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
