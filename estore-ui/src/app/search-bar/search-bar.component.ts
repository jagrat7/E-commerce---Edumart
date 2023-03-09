import { Component, OnInit } from '@angular/core';
import { ProductService } from '../product.service';
import { Observable, Subject } from 'rxjs';
import { Product } from '../Product';
import {
  debounceTime, distinctUntilChanged, switchMap
} from 'rxjs/operators';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent implements OnInit {

  product$!: Observable<Product[]>;
   Sproducts;
  private searchTerms = new Subject<string>();
  constructor(private productService: ProductService) { }


  search(term: string): void {
    this.searchTerms.next(term);
    console.log(this.product$)
  }
  getSearchedProducts(term:string): void {
    this.productService.searchProducts(term).subscribe(products => this.Sproducts= products);
    console.log(this.Sproducts)

  }

  ngOnInit(): void {
    // this.product$!=this.searchTerms.pipe(
    //   // wait 300ms after each keystroke before considering the term
    //   debounceTime(300),

    //   // ignore new term if same as previous term
    //   distinctUntilChanged(),

    //   // switch to new search observable each time the term changes
    //   switchMap((term: string) => this.productService.searchProducts(term)),
    // );
    // console.log(this.product$)
  }



}
