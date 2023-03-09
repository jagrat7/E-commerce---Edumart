import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule} from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductTileComponent } from './product-tile/product-tile.component';
import { BannerComponent } from './banner/banner.component';
import { SearchBarComponent } from './search-bar/search-bar.component';
import { ProductTilesComponent } from './product-tiles/product-tiles.component';
import { HomeComponent } from './home/home.component'; // <-- NgModel lives here
import { lookupListToken, lookupLists } from './providers';
import { ProductSingleViewComponent } from './product-single-view/product-single-view.component';
import { AdminInvAllListComponent } from './admin-inv-all-list/admin-inv-all-list.component';
import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdminEditProductComponent } from './admin-edit-product/admin-edit-product.component';
import { routing } from './app-routing.module';
import { CartComponent } from './cart/cart.component';
import { CartTileComponent } from './cart-tile/cart-tile.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatBadgeModule} from '@angular/material/badge';
import { DasshyComponent } from './dasshy/dasshy.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { LayoutModule } from '@angular/cdk/layout';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import {CarouselModule} from 'primeng/carousel';
import {GalleriaModule} from 'primeng/galleria';
import { RequestAddProductComponent } from './request-add-product/request-add-product.component';
import { AdminAddProductMatComponent } from './admin-add-product-mat/admin-add-product-mat.component';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import { MatTableModule } from '@angular/material/table';
import { AuthorProfileComponent } from './author-profile/author-profile.component';
import { BookedSessionsComponent } from './booked-sessions/booked-sessions.component';
import { AddRequestsComponent } from './add-requests/add-requests.component';
import {MatExpansionModule} from '@angular/material/expansion';
import { PurchaseHistoryComponent } from './purchase-history/purchase-history.component';
import { InventoryHistoryComponent } from './inventory-history/inventory-history.component';
import { RegisterComponent } from './register/register.component';
import { CartTileSesssionComponent } from './cart-tile-sesssion/cart-tile-sesssion.component';
import { NavComponent } from './nav/nav.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductTileComponent,
    BannerComponent,
    SearchBarComponent,
    ProductTilesComponent,
    HomeComponent,
    ProductSingleViewComponent,
    AdminInvAllListComponent,
    LoginComponent,
    AdminEditProductComponent, 
    CartComponent, 
    DasshyComponent, 
    RequestAddProductComponent,
     CartTileComponent, 
     AdminAddProductMatComponent, 
     AuthorProfileComponent, 
     BookedSessionsComponent, 
     PurchaseHistoryComponent,
     AddRequestsComponent,
     InventoryHistoryComponent,
     RegisterComponent,
     CartTileSesssionComponent,
     NavComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    routing,
    BrowserAnimationsModule,
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    MatCardModule,
    MatBadgeModule,
    MatGridListModule,
    MatMenuModule,
    LayoutModule,
    MatSidenavModule,
    MatListModule,
    CarouselModule,
    GalleriaModule,
    MatInputModule,
    MatSelectModule,
    MatRadioModule,
    MatButtonToggleModule,
    MatTableModule,
    MatExpansionModule
  ],
  providers: [{provide: lookupListToken, useValue: lookupLists}],
  bootstrap: [AppComponent]
})
export class AppModule { }

