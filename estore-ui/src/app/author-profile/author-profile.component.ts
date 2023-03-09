import { Component, OnInit } from '@angular/core';
import { ProductService } from  '../product.service';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../session.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-author-profile',
  templateUrl: './author-profile.component.html',
  styleUrls: ['./author-profile.component.css']
})
export class AuthorProfileComponent implements OnInit {
  author = null;
  author_id = 0;
  author_name = "";
  not_found = true;

  

  constructor(private storeProductService : ProductService,
    private route: ActivatedRoute,
    private sessionService: SessionService,
    private router: Router) { }

  ngOnInit(): void {

    let id = 0;
    this.getAuthor();
    

    
  }

  getAuthor(): void {
    debugger;

    this.author_id = Number(this.route.snapshot.queryParams.id);
    if(this.author_id > 400){ 
    this.author_name = String(this.route.snapshot.queryParams.name);
    this.storeProductService.getAuthor(this.author_id).subscribe({next: (author) => {
      debugger;
      this.author = author;
      this.not_found = false;
    }
  });
}
  console.log("attention!")
    console.log(this.not_found);
  }

  addSessionToCart(session): void{
    if(!this.sessionService.getIsLogIn()){
      this.router.navigate(['login'])
    }else{
      this.storeProductService.addSessionToCart(this.author.username, session).subscribe({
      next: (cartProduct) => {
        console.log(cartProduct);
        if (cartProduct.hasOwnProperty("status") && cartProduct.status == "failure") {
          console.log("PRODUCT ALREADY ADDED")
        }
      }
    });
      }
    }

}
